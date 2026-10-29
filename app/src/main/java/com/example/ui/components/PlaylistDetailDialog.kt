package com.example.ui.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.model.MediaTrack
import com.example.model.Playlist
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MidnightBlack
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PlaylistDetailDialog(
  playlist: Playlist,
  allTracks: List<MediaTrack>,
  currentTrackId: String?,
  onDismiss: () -> Unit,
  onPlayTrack: (MediaTrack, List<MediaTrack>) -> Unit
) {
  val playlistTracks = playlist.trackIds.mapNotNull { id -> allTracks.find { it.id == id } }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      color = MidnightBlack,
      modifier = Modifier
        .fillMaxSize()
        .testTag("playlist_detail_dialog")
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // Header with Close
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(SurfaceCardElevated)
          ) {
            Icon(Icons.Default.Close, "Close", tint = TextPrimary)
          }

          Text(
            text = "PLAYLIST",
            style = MaterialTheme.typography.labelSmall,
            color = ElectricCyan,
            fontWeight = FontWeight.Bold
          )

          Spacer(modifier = Modifier.size(40.dp))
        }

        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
        ) {
          // Playlist Artwork & Info
          item {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier.fillMaxWidth()
            ) {
              Box(
                modifier = Modifier
                  .size(180.dp)
                  .clip(RoundedCornerShape(20.dp))
                  .border(2.dp, Brush.linearGradient(listOf(NeonViolet, ElectricCyan)), RoundedCornerShape(20.dp))
              ) {
                AsyncImage(
                  model = playlist.coverUrl,
                  contentDescription = playlist.title,
                  contentScale = ContentScale.Crop,
                  modifier = Modifier.fillMaxSize()
                )
              }

              Spacer(modifier = Modifier.height(14.dp))

              Text(
                text = playlist.title,
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
              )

              Spacer(modifier = Modifier.height(4.dp))

              Text(
                text = playlist.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 16.dp)
              )

              Spacer(modifier = Modifier.height(8.dp))

              Text(
                text = "Created by ${playlist.createdBy} • ${playlistTracks.size} songs",
                style = MaterialTheme.typography.labelSmall,
                color = ElectricCyan
              )

              Spacer(modifier = Modifier.height(16.dp))

              // Play & Shuffle Buttons
              Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Button(
                  onClick = {
                    if (playlistTracks.isNotEmpty()) {
                      onPlayTrack(playlistTracks.first(), playlistTracks)
                    }
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                ) {
                  Icon(Icons.Default.PlayArrow, null, tint = TextPrimary)
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Play All", color = TextPrimary, fontWeight = FontWeight.Bold)
                }

                Button(
                  onClick = {
                    if (playlistTracks.isNotEmpty()) {
                      val shuffled = playlistTracks.shuffled()
                      onPlayTrack(shuffled.first(), shuffled)
                    }
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = SurfaceCardElevated),
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                ) {
                  Icon(Icons.Default.Shuffle, null, tint = ElectricCyan)
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Shuffle", color = TextPrimary)
                }
              }

              Spacer(modifier = Modifier.height(20.dp))
            }
          }

          // Song List
          itemsIndexed(playlistTracks) { index, track ->
            val isCurrent = track.id == currentTrackId

            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(
                containerColor = if (isCurrent) NeonViolet.copy(alpha = 0.2f) else SurfaceCard
              ),
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable { onPlayTrack(track, playlistTracks) }
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(10.dp)
              ) {
                Text(
                  text = "${index + 1}",
                  style = MaterialTheme.typography.labelMedium,
                  color = if (isCurrent) ElectricCyan else TextSecondary,
                  modifier = Modifier.width(28.dp)
                )

                Box(
                  modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                ) {
                  AsyncImage(
                    model = track.coverArtUrl,
                    contentDescription = track.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                  )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = track.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (isCurrent) ElectricCyan else TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  Text(
                    text = track.artist,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1
                  )
                }

                val mins = track.durationSeconds / 60
                val secs = track.durationSeconds % 60
                Text(
                  text = String.format("%d:%02d", mins, secs),
                  style = MaterialTheme.typography.labelSmall,
                  color = TextSecondary
                )
              }
            }
          }
        }
      }
    }
  }
}
