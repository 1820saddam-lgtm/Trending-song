package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.model.MediaTrack
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.HotMagenta
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun MiniPlayerBar(
  currentTrack: MediaTrack?,
  isPlaying: Boolean,
  progressMs: Int,
  durationMs: Int,
  isFavorite: Boolean,
  visualizerLevels: List<Float>,
  onTogglePlayPause: () -> Unit,
  onSkipNext: () -> Unit,
  onToggleFavorite: () -> Unit,
  onOpenFullScreen: () -> Unit,
  modifier: Modifier = Modifier
) {
  AnimatedVisibility(
    visible = currentTrack != null,
    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
    modifier = modifier
  ) {
    if (currentTrack == null) return@AnimatedVisibility

    val progress = if (durationMs > 0) (progressMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f

    Surface(
      shape = RoundedCornerShape(16.dp),
      color = Color.Transparent,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 6.dp)
        .shadow(16.dp, RoundedCornerShape(16.dp), spotColor = NeonViolet)
        .clickable(onClick = onOpenFullScreen)
        .testTag("mini_player_surface")
    ) {
      Box(
        modifier = Modifier
          .background(
            Brush.horizontalGradient(
              colors = listOf(
                DarkNavyBg.copy(alpha = 0.95f),
                SurfaceCardElevated.copy(alpha = 0.95f),
                DarkNavyBg.copy(alpha = 0.95f)
              )
            )
          )
      ) {
        Column {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 8.dp)
          ) {
            // Album Art
            Box(
              modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceCardElevated)
            ) {
              AsyncImage(
                model = currentTrack.coverArtUrl,
                contentDescription = currentTrack.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(48.dp)
              )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Track details + mini visualizer
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = currentTrack.title,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = currentTrack.artist,
                  style = MaterialTheme.typography.bodySmall,
                  color = TextSecondary,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                  modifier = Modifier.weight(1f, fill = false)
                )
                Spacer(modifier = Modifier.width(8.dp))
                WaveformVisualizer(
                  isPlaying = isPlaying,
                  levels = visualizerLevels,
                  height = 12.dp,
                  barCount = 6,
                  modifier = Modifier.width(36.dp)
                )
              }
            }

            // Favorite button
            IconButton(
              onClick = onToggleFavorite,
              modifier = Modifier
                .size(48.dp)
                .testTag("mini_player_favorite_button")
            ) {
              Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (isFavorite) HotMagenta else TextSecondary,
                modifier = Modifier.size(22.dp)
              )
            }

            // Play/Pause button
            IconButton(
              onClick = onTogglePlayPause,
              modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                  Brush.linearGradient(
                    colors = listOf(NeonViolet, ElectricCyan)
                  )
                )
                .testTag("mini_player_play_pause_button")
            ) {
              Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = TextPrimary,
                modifier = Modifier.size(24.dp)
              )
            }

            // Next button
            IconButton(
              onClick = onSkipNext,
              modifier = Modifier
                .size(48.dp)
                .testTag("mini_player_next_button")
            ) {
              Icon(
                imageVector = Icons.Default.SkipNext,
                contentDescription = "Next Track",
                tint = TextPrimary,
                modifier = Modifier.size(24.dp)
              )
            }
          }

          // Subtle bottom progress bar
          LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
              .fillMaxWidth()
              .height(3.dp),
            color = ElectricCyan,
            trackColor = Color(0x33FFFFFF)
          )
        }
      }
    }
  }
}
