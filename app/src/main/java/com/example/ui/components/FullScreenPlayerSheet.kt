package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.model.MediaTrack
import com.example.model.RepeatMode as DomainRepeatMode
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.HotMagenta
import com.example.ui.theme.MidnightBlack
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun FullScreenPlayerSheet(
  track: MediaTrack?,
  isPlaying: Boolean,
  progressMs: Int,
  durationMs: Int,
  isShuffle: Boolean,
  repeatMode: DomainRepeatMode,
  volume: Float,
  playbackSpeed: Float,
  isFavorite: Boolean,
  visualizerLevels: List<Float>,
  queue: List<MediaTrack>,
  currentQueueIndex: Int,
  language: String,
  onDismiss: () -> Unit,
  onTogglePlayPause: () -> Unit,
  onSkipNext: () -> Unit,
  onSkipPrevious: () -> Unit,
  onSeekTo: (Int) -> Unit,
  onToggleShuffle: () -> Unit,
  onCycleRepeat: () -> Unit,
  onVolumeChange: (Float) -> Unit,
  onSpeedChange: (Float) -> Unit,
  onToggleFavorite: () -> Unit,
  onSelectQueueTrack: (Int) -> Unit
) {
  if (track == null) return

  var isUserDraggingSlider by remember { mutableStateOf(false) }
  var dragPositionMs by remember { mutableFloatStateOf(0f) }
  var currentTab by remember { mutableStateOf<PlayerTab>(PlayerTab.CONTROLS) }
  var showVolumeSlider by remember { mutableStateOf(false) }

  // Rotating vinyl animation
  val rotation = remember { Animatable(0f) }
  LaunchedEffect(isPlaying) {
    if (isPlaying) {
      rotation.animateTo(
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
          animation = tween(14000, easing = LinearEasing),
          repeatMode = RepeatMode.Restart
        )
      )
    } else {
      rotation.stop()
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
  ) {
    Surface(
      color = MidnightBlack,
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
        .testTag("full_screen_player_sheet")
    ) {
      Box(modifier = Modifier.fillMaxSize()) {
        // Ambient background glow
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                colors = listOf(
                  NeonViolet.copy(alpha = 0.35f),
                  DarkNavyBg,
                  MidnightBlack
                )
              )
            )
        )

        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
          // Top bar: Collapse, Title, Options
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            IconButton(
              onClick = onDismiss,
              modifier = Modifier
                .size(48.dp)
                .testTag("full_player_close_button")
            ) {
              Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Collapse Player",
                tint = TextPrimary,
                modifier = Modifier.size(32.dp)
              )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "PLAYING FROM PLAYLIST",
                style = MaterialTheme.typography.labelSmall,
                color = ElectricCyan
              )
              Text(
                text = track.album.ifBlank { "TrendWave Top Hits" },
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }

            IconButton(
              onClick = onToggleFavorite,
              modifier = Modifier
                .size(48.dp)
                .testTag("full_player_fav_button")
            ) {
              Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (isFavorite) HotMagenta else TextPrimary,
                modifier = Modifier.size(26.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Tab Selector: Controls vs Lyrics vs Queue
          Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
          ) {
            FilterChip(
              selected = currentTab == PlayerTab.CONTROLS,
              onClick = { currentTab = PlayerTab.CONTROLS },
              label = { Text("Player") },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = NeonViolet,
                selectedLabelColor = TextPrimary,
                containerColor = SurfaceCard,
                labelColor = TextSecondary
              )
            )
            Spacer(modifier = Modifier.width(8.dp))
            FilterChip(
              selected = currentTab == PlayerTab.LYRICS,
              onClick = { currentTab = PlayerTab.LYRICS },
              label = { Text(LanguageHelper.get("lyrics", language)) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = NeonViolet,
                selectedLabelColor = TextPrimary,
                containerColor = SurfaceCard,
                labelColor = TextSecondary
              )
            )
            Spacer(modifier = Modifier.width(8.dp))
            FilterChip(
              selected = currentTab == PlayerTab.QUEUE,
              onClick = { currentTab = PlayerTab.QUEUE },
              label = { Text("${LanguageHelper.get("up_next", language)} (${queue.size})") },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = NeonViolet,
                selectedLabelColor = TextPrimary,
                containerColor = SurfaceCard,
                labelColor = TextSecondary
              )
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          when (currentTab) {
            PlayerTab.CONTROLS -> {
              // Main Visual & Controls
              Column(
                modifier = Modifier
                  .weight(1f)
                  .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
              ) {
                // Vinyl / Artwork Canvas
                Box(
                  contentAlignment = Alignment.Center,
                  modifier = Modifier
                    .size(260.dp)
                    .shadow(32.dp, CircleShape, spotColor = NeonViolet)
                    .clip(CircleShape)
                    .background(Color(0xFF10121C))
                    .border(4.dp, Brush.sweepGradient(listOf(NeonViolet, ElectricCyan, HotMagenta, NeonViolet)), CircleShape)
                ) {
                  AsyncImage(
                    model = track.coverArtUrl,
                    contentDescription = track.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                      .size(240.dp)
                      .clip(CircleShape)
                      .rotate(rotation.value)
                  )
                  // Center Vinyl spindle hole
                  Box(
                    modifier = Modifier
                      .size(36.dp)
                      .clip(CircleShape)
                      .background(MidnightBlack)
                      .border(3.dp, ElectricCyan, CircleShape)
                  )
                }

                // Track Title & Artist Info
                Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                ) {
                  Text(
                    text = track.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "${track.artist} • ${track.category} • ${track.releaseYear}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ElectricCyan,
                    textAlign = TextAlign.Center
                  )
                }

                // Live animated equalizer waveform
                WaveformVisualizer(
                  isPlaying = isPlaying,
                  levels = visualizerLevels,
                  height = 36.dp,
                  barCount = 28,
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                )

                // Seek Bar Section
                val activePos = if (isUserDraggingSlider) dragPositionMs else progressMs.toFloat()
                val totalDuration = durationMs.coerceAtLeast(1000)

                Column(modifier = Modifier.fillMaxWidth()) {
                  Slider(
                    value = activePos.coerceIn(0f, totalDuration.toFloat()),
                    onValueChange = {
                      isUserDraggingSlider = true
                      dragPositionMs = it
                    },
                    onValueChangeFinished = {
                      isUserDraggingSlider = false
                      onSeekTo(dragPositionMs.toInt())
                    },
                    valueRange = 0f..totalDuration.toFloat(),
                    colors = SliderDefaults.colors(
                      thumbColor = ElectricCyan,
                      activeTrackColor = NeonViolet,
                      inactiveTrackColor = SurfaceCardElevated
                    ),
                    modifier = Modifier
                      .fillMaxWidth()
                      .testTag("full_player_seek_slider")
                  )

                  Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Text(
                      text = formatTime(activePos.toInt()),
                      style = MaterialTheme.typography.bodySmall,
                      color = TextSecondary
                    )
                    Text(
                      text = formatTime(totalDuration),
                      style = MaterialTheme.typography.bodySmall,
                      color = TextSecondary
                    )
                  }
                }

                // Playback Control Buttons
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceEvenly,
                  modifier = Modifier.fillMaxWidth()
                ) {
                  // Shuffle
                  IconButton(
                    onClick = onToggleShuffle,
                    modifier = Modifier
                      .size(48.dp)
                      .testTag("full_player_shuffle_button")
                  ) {
                    Icon(
                      imageVector = Icons.Default.Shuffle,
                      contentDescription = "Shuffle",
                      tint = if (isShuffle) ElectricCyan else TextTertiary,
                      modifier = Modifier.size(24.dp)
                    )
                  }

                  // Previous
                  IconButton(
                    onClick = onSkipPrevious,
                    modifier = Modifier
                      .size(54.dp)
                      .testTag("full_player_previous_button")
                  ) {
                    Icon(
                      imageVector = Icons.Default.SkipPrevious,
                      contentDescription = "Previous Track",
                      tint = TextPrimary,
                      modifier = Modifier.size(36.dp)
                    )
                  }

                  // Play / Pause Main Button
                  Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                      .size(72.dp)
                      .shadow(20.dp, CircleShape, spotColor = NeonViolet)
                      .clip(CircleShape)
                      .background(
                        Brush.linearGradient(
                          colors = listOf(NeonViolet, ElectricCyan)
                        )
                      )
                      .clickable(onClick = onTogglePlayPause)
                      .testTag("full_player_play_pause_button")
                  ) {
                    Icon(
                      imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                      contentDescription = if (isPlaying) "Pause" else "Play",
                      tint = TextPrimary,
                      modifier = Modifier.size(40.dp)
                    )
                  }

                  // Next
                  IconButton(
                    onClick = onSkipNext,
                    modifier = Modifier
                      .size(54.dp)
                      .testTag("full_player_next_button")
                  ) {
                    Icon(
                      imageVector = Icons.Default.SkipNext,
                      contentDescription = "Next Track",
                      tint = TextPrimary,
                      modifier = Modifier.size(36.dp)
                    )
                  }

                  // Repeat
                  IconButton(
                    onClick = onCycleRepeat,
                    modifier = Modifier
                      .size(48.dp)
                      .testTag("full_player_repeat_button")
                  ) {
                    val repeatIcon = if (repeatMode == DomainRepeatMode.ONE) Icons.Default.RepeatOne else Icons.Default.Repeat
                    val repeatTint = if (repeatMode != DomainRepeatMode.OFF) ElectricCyan else TextTertiary
                    Icon(
                      imageVector = repeatIcon,
                      contentDescription = "Repeat Mode",
                      tint = repeatTint,
                      modifier = Modifier.size(24.dp)
                    )
                  }
                }

                // Volume & Speed bar
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween,
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                ) {
                  // Volume toggle
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                  ) {
                    IconButton(
                      onClick = { showVolumeSlider = !showVolumeSlider },
                      modifier = Modifier.size(44.dp)
                    ) {
                      Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Volume",
                        tint = if (showVolumeSlider) ElectricCyan else TextSecondary,
                        modifier = Modifier.size(20.dp)
                      )
                    }
                    if (showVolumeSlider) {
                      Slider(
                        value = volume,
                        onValueChange = onVolumeChange,
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(
                          thumbColor = ElectricCyan,
                          activeTrackColor = ElectricCyan
                        ),
                        modifier = Modifier
                          .weight(1f)
                          .testTag("full_player_volume_slider")
                      )
                    }
                  }

                  // Speed toggle
                  val nextSpeed = when (playbackSpeed) {
                    1.0f -> 1.25f
                    1.25f -> 1.5f
                    1.5f -> 0.75f
                    else -> 1.0f
                  }
                  Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceCardElevated),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                      .clickable { onSpeedChange(nextSpeed) }
                      .testTag("full_player_speed_card")
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                      Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = "Playback Speed",
                        tint = NeonViolet,
                        modifier = Modifier.size(16.dp)
                      )
                      Spacer(modifier = Modifier.width(4.dp))
                      Text(
                        text = "${playbackSpeed}x",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextPrimary
                      )
                    }
                  }
                }
              }
            }

            PlayerTab.LYRICS -> {
              // Lyrics View
              Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard.copy(alpha = 0.8f)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                  .weight(1f)
                  .fillMaxWidth()
                  .padding(8.dp)
              ) {
                LazyColumn(
                  modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                  verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                  item {
                    Text(
                      text = "${track.title} — Lyrics",
                      style = MaterialTheme.typography.titleMedium,
                      color = NeonViolet,
                      fontWeight = FontWeight.Bold
                    )
                  }
                  if (track.lyrics.isEmpty()) {
                    item {
                      Text(
                        text = "Synchronized lyrics will load automatically during playback.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                      )
                    }
                  } else {
                    itemsIndexed(track.lyrics) { index, line ->
                      Text(
                        text = line,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (index == 0) TextPrimary else TextSecondary,
                        fontWeight = if (index == 0) FontWeight.Bold else FontWeight.Normal
                      )
                    }
                  }
                }
              }
            }

            PlayerTab.QUEUE -> {
              // Up Next Queue View
              Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard.copy(alpha = 0.8f)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                  .weight(1f)
                  .fillMaxWidth()
                  .padding(8.dp)
              ) {
                LazyColumn(
                  modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                  verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  item {
                    Text(
                      text = "Playing Queue (${queue.size} songs)",
                      style = MaterialTheme.typography.titleMedium,
                      color = ElectricCyan,
                      fontWeight = FontWeight.Bold,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                  }

                  itemsIndexed(queue) { index, itemTrack ->
                    val isCurrent = index == currentQueueIndex
                    Card(
                      colors = CardDefaults.cardColors(
                        containerColor = if (isCurrent) NeonViolet.copy(alpha = 0.25f) else SurfaceCardElevated
                      ),
                      shape = RoundedCornerShape(12.dp),
                      modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectQueueTrack(index) }
                    ) {
                      Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(10.dp)
                      ) {
                        Text(
                          text = "${index + 1}",
                          style = MaterialTheme.typography.labelMedium,
                          color = if (isCurrent) ElectricCyan else TextTertiary,
                          modifier = Modifier.width(28.dp)
                        )
                        Box(
                          modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                        ) {
                          AsyncImage(
                            model = itemTrack.coverArtUrl,
                            contentDescription = itemTrack.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                          )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                          Text(
                            text = itemTrack.title,
                            style = MaterialTheme.typography.titleSmall,
                            color = if (isCurrent) ElectricCyan else TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                          )
                          Text(
                            text = itemTrack.artist,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            maxLines = 1
                          )
                        }
                        if (isCurrent) {
                          WaveformVisualizer(
                            isPlaying = isPlaying,
                            levels = visualizerLevels,
                            height = 14.dp,
                            barCount = 4,
                            modifier = Modifier.width(24.dp)
                          )
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

enum class PlayerTab {
  CONTROLS, LYRICS, QUEUE
}

private fun formatTime(millis: Int): String {
  val totalSec = millis / 1000
  val minutes = totalSec / 60
  val seconds = totalSec % 60
  return String.format("%02d:%02d", minutes, seconds)
}
