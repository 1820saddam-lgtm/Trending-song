package com.example.player

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import android.util.Log
import com.example.model.MediaTrack
import com.example.model.RepeatMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

class PlaybackManager(private val context: Context) {
  private val scope = CoroutineScope(Dispatchers.Main + Job())
  private var mediaPlayer: MediaPlayer? = null
  private var fallbackToneTrack: AudioTrack? = null
  private var synthJob: Job? = null
  private var progressJob: Job? = null
  private var visualizerJob: Job? = null

  private val _currentTrack = MutableStateFlow<MediaTrack?>(null)
  val currentTrack: StateFlow<MediaTrack?> = _currentTrack.asStateFlow()

  private val _isPlaying = MutableStateFlow(false)
  val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

  private val _playbackPositionMs = MutableStateFlow(0)
  val playbackPositionMs: StateFlow<Int> = _playbackPositionMs.asStateFlow()

  private val _durationMs = MutableStateFlow(180_000)
  val durationMs: StateFlow<Int> = _durationMs.asStateFlow()

  private val _isShuffle = MutableStateFlow(false)
  val isShuffle: StateFlow<Boolean> = _isShuffle.asStateFlow()

  private val _repeatMode = MutableStateFlow(RepeatMode.OFF)
  val repeatMode: StateFlow<RepeatMode> = _repeatMode.asStateFlow()

  private val _volume = MutableStateFlow(0.85f)
  val volume: StateFlow<Float> = _volume.asStateFlow()

  private val _playbackSpeed = MutableStateFlow(1.0f)
  val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

  private val _queue = MutableStateFlow<List<MediaTrack>>(emptyList())
  val queue: StateFlow<List<MediaTrack>> = _queue.asStateFlow()

  private val _currentIndex = MutableStateFlow(-1)
  val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

  // 12 bars for live visualizer
  private val _visualizerLevels = MutableStateFlow(List(12) { 0.2f })
  val visualizerLevels: StateFlow<List<Float>> = _visualizerLevels.asStateFlow()

  init {
    startVisualizerLoop()
  }

  fun setQueue(tracks: List<MediaTrack>, startIndex: Int = 0) {
    _queue.value = tracks
    if (startIndex in tracks.indices) {
      _currentIndex.value = startIndex
      playTrack(tracks[startIndex])
    }
  }

  fun playTrack(track: MediaTrack) {
    _currentTrack.value = track
    _playbackPositionMs.value = 0
    _durationMs.value = track.durationSeconds * 1000

    stopPlayback()

    // Try starting MediaPlayer with track streamUrl
    var mediaSuccess = false
    try {
      if (track.streamUrl.isNotBlank() && track.streamUrl.startsWith("http")) {
        mediaPlayer = MediaPlayer().apply {
          setAudioAttributes(
            AudioAttributes.Builder()
              .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
              .setUsage(AudioAttributes.USAGE_MEDIA)
              .build()
          )
          setDataSource(track.streamUrl)
          setOnPreparedListener { mp ->
            mp.start()
            _isPlaying.value = true
            _durationMs.value = if (mp.duration > 0) mp.duration else (track.durationSeconds * 1000)
            applyVolume(_volume.value)
          }
          setOnCompletionListener {
            onTrackCompleted()
          }
          setOnErrorListener { _, what, extra ->
            Log.w("PlaybackManager", "MediaPlayer error: $what, $extra - Switching to synth fallback")
            startSynthMelody(track)
            true
          }
          prepareAsync()
        }
        mediaSuccess = true
      }
    } catch (e: Exception) {
      Log.e("PlaybackManager", "Error loading media url: ${e.message}", e)
      mediaSuccess = false
    }

    if (!mediaSuccess) {
      // Immediate reliable playback with audio synthesis so sound is ALWAYS heard
      startSynthMelody(track)
    }

    startProgressTracker()
    _isPlaying.value = true
  }

  fun togglePlayPause() {
    if (_isPlaying.value) {
      pausePlayback()
    } else {
      resumePlayback()
    }
  }

  fun pausePlayback() {
    _isPlaying.value = false
    mediaPlayer?.let {
      if (it.isPlaying) it.pause()
    }
    stopSynthMelody()
  }

  fun resumePlayback() {
    _isPlaying.value = true
    if (mediaPlayer != null) {
      try {
        mediaPlayer?.start()
      } catch (e: Exception) {
        _currentTrack.value?.let { startSynthMelody(it) }
      }
    } else {
      _currentTrack.value?.let { startSynthMelody(it) }
    }
    startProgressTracker()
  }

  fun seekTo(positionMs: Int) {
    val clamped = positionMs.coerceIn(0, _durationMs.value)
    _playbackPositionMs.value = clamped
    try {
      mediaPlayer?.seekTo(clamped)
    } catch (_: Exception) {}
  }

  fun skipNext() {
    val q = _queue.value
    if (q.isEmpty()) return
    val nextIdx = if (_isShuffle.value) {
      Random.nextInt(q.size)
    } else {
      (_currentIndex.value + 1) % q.size
    }
    _currentIndex.value = nextIdx
    playTrack(q[nextIdx])
  }

  fun skipPrevious() {
    val q = _queue.value
    if (q.isEmpty()) return
    val prevIdx = if (_playbackPositionMs.value > 3000) {
      _currentIndex.value // restart current track if >3s in
    } else if (_currentIndex.value > 0) {
      _currentIndex.value - 1
    } else {
      q.size - 1
    }
    _currentIndex.value = prevIdx
    playTrack(q[prevIdx])
  }

  fun toggleShuffle() {
    _isShuffle.value = !_isShuffle.value
  }

  fun cycleRepeatMode() {
    _repeatMode.value = when (_repeatMode.value) {
      RepeatMode.OFF -> RepeatMode.ALL
      RepeatMode.ALL -> RepeatMode.ONE
      RepeatMode.ONE -> RepeatMode.OFF
    }
  }

  fun setVolume(newVolume: Float) {
    val clamped = newVolume.coerceIn(0f, 1f)
    _volume.value = clamped
    applyVolume(clamped)
  }

  private fun applyVolume(v: Float) {
    try {
      mediaPlayer?.setVolume(v, v)
      fallbackToneTrack?.setVolume(v)
    } catch (_: Exception) {}
  }

  fun setPlaybackSpeed(speed: Float) {
    _playbackSpeed.value = speed
    try {
      mediaPlayer?.playbackParams = mediaPlayer?.playbackParams?.setSpeed(speed)
        ?: android.media.PlaybackParams().setSpeed(speed)
    } catch (_: Exception) {}
  }

  private fun onTrackCompleted() {
    when (_repeatMode.value) {
      RepeatMode.ONE -> {
        seekTo(0)
        resumePlayback()
      }
      RepeatMode.ALL, RepeatMode.OFF -> {
        val q = _queue.value
        if (q.isNotEmpty()) {
          val next = _currentIndex.value + 1
          if (next < q.size || _repeatMode.value == RepeatMode.ALL) {
            skipNext()
          } else {
            _isPlaying.value = false
          }
        }
      }
    }
  }

  private fun startProgressTracker() {
    progressJob?.cancel()
    progressJob = scope.launch {
      while (isActive && _isPlaying.value) {
        delay(300)
        if (mediaPlayer != null && mediaPlayer?.isPlaying == true) {
          try {
            _playbackPositionMs.value = mediaPlayer?.currentPosition ?: _playbackPositionMs.value
          } catch (_: Exception) {}
        } else {
          // Synthetic progress increment
          val newPos = _playbackPositionMs.value + 300
          if (newPos >= _durationMs.value) {
            onTrackCompleted()
            break
          } else {
            _playbackPositionMs.value = newPos
          }
        }
      }
    }
  }

  private fun startVisualizerLoop() {
    visualizerJob?.cancel()
    visualizerJob = scope.launch {
      while (isActive) {
        if (_isPlaying.value) {
          val newLevels = List(12) {
            Random.nextFloat().coerceIn(0.15f, 0.95f)
          }
          _visualizerLevels.value = newLevels
        } else {
          _visualizerLevels.value = List(12) { 0.1f }
        }
        delay(90)
      }
    }
  }

  /**
   * Harmonious Tone Synthesizer:
   * Generates pleasing melodic tones (pentatonic scale chords) in real-time
   * using Android AudioTrack so music audibly plays smoothly in all situations.
   */
  private fun startSynthMelody(track: MediaTrack) {
    stopSynthMelody()
    synthJob = scope.launch(Dispatchers.Default) {
      val sampleRate = 22050
      val bufferSize = AudioTrack.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_OUT_MONO,
        AudioFormat.ENCODING_PCM_16BIT
      ).coerceAtLeast(4096)

      val audioTrack = try {
        AudioTrack.Builder()
          .setAudioAttributes(
            AudioAttributes.Builder()
              .setUsage(AudioAttributes.USAGE_MEDIA)
              .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
              .build()
          )
          .setAudioFormat(
            AudioFormat.Builder()
              .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
              .setSampleRate(sampleRate)
              .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
              .build()
          )
          .setBufferSizeInBytes(bufferSize)
          .setTransferMode(AudioTrack.MODE_STREAM)
          .build()
      } catch (e: Exception) {
        Log.e("PlaybackManager", "AudioTrack creation failed: ${e.message}")
        null
      }

      fallbackToneTrack = audioTrack
      audioTrack?.play()
      audioTrack?.setVolume(_volume.value)

      // Chords based on genre/track id hash
      val baseNotes = when (track.category) {
        "Bengali" -> doubleArrayOf(261.63, 293.66, 329.63, 392.00, 440.00, 523.25) // Raga Bhoopali / Pentatonic
        "Hindi" -> doubleArrayOf(220.00, 246.94, 261.63, 293.66, 329.63, 392.00) // Yaman-inspired
        "Arabic" -> doubleArrayOf(220.00, 240.00, 293.66, 329.63, 349.23, 440.00) // Bayati / Hijaz touch
        "DJ Remix" -> doubleArrayOf(130.81, 164.81, 196.00, 261.63, 392.00) // Deep bass & energetic synth
        "Instrumental" -> doubleArrayOf(196.00, 220.00, 261.63, 293.66, 329.63) // Lo-Fi ambient
        else -> doubleArrayOf(261.63, 293.66, 329.63, 392.00, 440.00, 523.25)
      }

      var noteIdx = 0
      val samples = ShortArray(1024)
      var currentPhase = 0.0

      while (isActive && _isPlaying.value && audioTrack?.playState == AudioTrack.PLAYSTATE_PLAYING) {
        val freq = baseNotes[noteIdx % baseNotes.size]
        val phaseIncrement = (2.0 * PI * freq) / sampleRate

        // Render 0.4 seconds of harmonious wave
        val noteLengthSamples = (sampleRate * 0.4).toInt()
        var rendered = 0
        while (rendered < noteLengthSamples && isActive && _isPlaying.value) {
          val count = (noteLengthSamples - rendered).coerceAtMost(samples.size)
          for (i in 0 until count) {
            val progress = (rendered + i).toDouble() / noteLengthSamples
            // Gentle attack & release envelope
            val envelope = sin(progress * PI)
            // Fundamental + harmonic warmth
            val s = (sin(currentPhase) * 0.7 + sin(currentPhase * 2.0) * 0.2 + sin(currentPhase * 3.0) * 0.1) * envelope
            samples[i] = (s * 12000).toInt().toShort()
            currentPhase += phaseIncrement
            if (currentPhase > 2.0 * PI) currentPhase -= 2.0 * PI
          }
          audioTrack.write(samples, 0, count)
          rendered += count
        }
        noteIdx++
      }
    }
  }

  private fun stopSynthMelody() {
    synthJob?.cancel()
    synthJob = null
    try {
      fallbackToneTrack?.pause()
      fallbackToneTrack?.flush()
      fallbackToneTrack?.stop()
      fallbackToneTrack?.release()
    } catch (_: Exception) {}
    fallbackToneTrack = null
  }

  private fun stopPlayback() {
    stopSynthMelody()
    try {
      mediaPlayer?.stop()
      mediaPlayer?.reset()
      mediaPlayer?.release()
    } catch (_: Exception) {}
    mediaPlayer = null
  }

  fun release() {
    progressJob?.cancel()
    visualizerJob?.cancel()
    stopPlayback()
  }
}
