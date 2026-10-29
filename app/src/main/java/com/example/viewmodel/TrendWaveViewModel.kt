package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.TrendWaveRepository
import com.example.model.MediaTrack
import com.example.model.MediaVideo
import com.example.model.Playlist
import com.example.model.TrendingFilter
import com.example.model.UserRole
import com.example.player.PlaybackManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TrendWaveViewModel(application: Application) : AndroidViewModel(application) {

  val repository = TrendWaveRepository()
  val playbackManager = PlaybackManager(application)

  // Navigation tab (0 = Home, 1 = Explore, 2 = Videos, 3 = Library, 4 = Profile)
  private val _currentNavIndex = MutableStateFlow(0)
  val currentNavIndex: StateFlow<Int> = _currentNavIndex.asStateFlow()

  // Full screen player visibility
  private val _showFullScreenPlayer = MutableStateFlow(false)
  val showFullScreenPlayer: StateFlow<Boolean> = _showFullScreenPlayer.asStateFlow()

  // VIP Dialog visibility
  private val _showPremiumDialog = MutableStateFlow(false)
  val showPremiumDialog: StateFlow<Boolean> = _showPremiumDialog.asStateFlow()

  // Active Video passed into Videos screen directly
  private val _selectedVideoForPlayback = MutableStateFlow<MediaVideo?>(null)
  val selectedVideoForPlayback: StateFlow<MediaVideo?> = _selectedVideoForPlayback.asStateFlow()

  // Active Playlist detail dialog
  private val _activePlaylistDetail = MutableStateFlow<Playlist?>(null)
  val activePlaylistDetail: StateFlow<Playlist?> = _activePlaylistDetail.asStateFlow()

  // Trending filter state in Explore
  private val _selectedTrendingFilter = MutableStateFlow(TrendingFilter.TODAY)
  val selectedTrendingFilter: StateFlow<TrendingFilter> = _selectedTrendingFilter.asStateFlow()

  // Data streams from repository
  val tracks: StateFlow<List<MediaTrack>> = repository.tracks
  val videos: StateFlow<List<MediaVideo>> = repository.videos
  val artists = repository.artists
  val playlists = repository.playlists
  val comments = repository.comments
  val userProfile = repository.userProfile
  val adminAnalytics = repository.adminAnalytics
  val announcements = repository.announcements

  // Playback Manager states
  val currentTrack = playbackManager.currentTrack
  val isPlaying = playbackManager.isPlaying
  val playbackPositionMs = playbackManager.playbackPositionMs
  val durationMs = playbackManager.durationMs
  val isShuffle = playbackManager.isShuffle
  val repeatMode = playbackManager.repeatMode
  val volume = playbackManager.volume
  val playbackSpeed = playbackManager.playbackSpeed
  val visualizerLevels = playbackManager.visualizerLevels
  val queue = playbackManager.queue
  val currentQueueIndex = playbackManager.currentIndex

  fun setNavIndex(index: Int) {
    _currentNavIndex.value = index
  }

  fun setShowFullScreenPlayer(show: Boolean) {
    _showFullScreenPlayer.value = show
  }

  fun setShowPremiumDialog(show: Boolean) {
    _showPremiumDialog.value = show
  }

  fun setSelectedTrendingFilter(filter: TrendingFilter) {
    _selectedTrendingFilter.value = filter
  }

  fun openPlaylist(playlist: Playlist) {
    _activePlaylistDetail.value = playlist
  }

  fun closePlaylistDetail() {
    _activePlaylistDetail.value = null
  }

  fun playTrackFromList(track: MediaTrack, list: List<MediaTrack>) {
    val idx = list.indexOfFirst { it.id == track.id }.coerceAtLeast(0)
    playbackManager.setQueue(list, idx)
    repository.recordTrackPlayed(track.id)
  }

  fun playSingleTrack(track: MediaTrack) {
    playbackManager.setQueue(listOf(track), 0)
    repository.recordTrackPlayed(track.id)
  }

  fun playVideo(video: MediaVideo) {
    _selectedVideoForPlayback.value = video
    _currentNavIndex.value = 2 // Switch to Videos tab
    repository.recordVideoWatched(video.id)
  }

  fun clearSelectedVideo() {
    _selectedVideoForPlayback.value = null
  }

  fun toggleLanguage() {
    val cur = userProfile.value.preferredLanguage
    repository.setLanguage(if (cur == "bn") "en" else "bn")
  }

  fun toggleTrackFavorite(trackId: String) {
    repository.toggleFavoriteTrack(trackId)
  }

  fun toggleVideoFavorite(videoId: String) {
    repository.toggleFavoriteVideo(videoId)
  }

  fun toggleFollowArtist(artistId: String) {
    repository.toggleFollowArtist(artistId)
  }

  fun createPlaylist(title: String, desc: String) {
    repository.createCustomPlaylist(title, desc)
  }

  fun addComment(videoId: String, text: String) {
    repository.addComment(videoId, text, userProfile.value.name)
  }

  fun upgradeToPremium() {
    repository.setPremiumStatus(true)
  }

  fun authenticate(name: String, email: String, isGoogle: Boolean) {
    repository.authenticateUser(name, email, isGoogle)
  }

  fun switchUserRole(role: UserRole) {
    repository.switchUserRole(role)
  }

  override fun onCleared() {
    super.onCleared()
    playbackManager.release()
  }
}
