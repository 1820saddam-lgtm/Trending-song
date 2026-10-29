package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.model.Artist
import com.example.model.MediaTrack
import com.example.model.MediaVideo
import com.example.model.Playlist
import com.example.model.UserProfile
import com.example.ui.components.LanguageHelper
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.HotMagenta
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun LibraryScreen(
  allTracks: List<MediaTrack>,
  allVideos: List<MediaVideo>,
  artists: List<Artist>,
  userProfile: UserProfile,
  language: String,
  onCreatePlaylist: (String, String) -> Unit,
  onPlayTrack: (MediaTrack, List<MediaTrack>) -> Unit,
  onPlayVideo: (MediaVideo) -> Unit,
  onOpenPlaylist: (Playlist) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableIntStateOf(0) }
  var showCreatePlaylistDialog by remember { mutableStateOf(false) }

  val likedTracks = allTracks.filter { userProfile.favoriteTrackIds.contains(it.id) }
  val savedVideos = allVideos.filter { userProfile.favoriteVideoIds.contains(it.id) }
  val historyTracks = userProfile.historyTrackIds.mapNotNull { id -> allTracks.find { it.id == id } }
  val followedArtists = artists.filter { userProfile.followedArtistIds.contains(it.id) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(DarkNavyBg),
    contentPadding = PaddingValues(bottom = 90.dp)
  ) {
    // Header
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column {
            Text(
              text = LanguageHelper.get("nav_library", language),
              style = MaterialTheme.typography.headlineLarge,
              color = TextPrimary,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "${userProfile.name}'s Collection",
              style = MaterialTheme.typography.bodySmall,
              color = ElectricCyan
            )
          }

          // New Playlist action button
          IconButton(
            onClick = { showCreatePlaylistDialog = true },
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(NeonViolet)
              .testTag("create_playlist_header_button")
          ) {
            Icon(Icons.Default.Add, "Create Playlist", tint = TextPrimary)
          }
        }
      }
    }

    // Tabs: Liked Songs, Playlists, Saved Videos, History
    item {
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = DarkNavyBg,
        contentColor = TextPrimary,
        indicator = { tabPositions ->
          TabRowDefaults.SecondaryIndicator(
            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
            color = ElectricCyan
          )
        }
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = { Text("Liked (${likedTracks.size})", style = MaterialTheme.typography.labelMedium) }
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = { Text("Playlists (${userProfile.customPlaylists.size})", style = MaterialTheme.typography.labelMedium) }
        )
        Tab(
          selected = selectedTab == 2,
          onClick = { selectedTab = 2 },
          text = { Text("Videos (${savedVideos.size})", style = MaterialTheme.typography.labelMedium) }
        )
        Tab(
          selected = selectedTab == 3,
          onClick = { selectedTab = 3 },
          text = { Text("History", style = MaterialTheme.typography.labelMedium) }
        )
      }
      Spacer(modifier = Modifier.height(12.dp))
    }

    when (selectedTab) {
      0 -> {
        // LIKED SONGS TAB
        if (likedTracks.isEmpty()) {
          item {
            EmptyLibraryCard(
              icon = Icons.Default.Favorite,
              title = "No liked songs yet",
              description = "Tap the heart icon on any song to save it to your library!"
            )
          }
        } else {
          // Play All & Shuffle Banner
          item {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
              Button(
                onClick = { onPlayTrack(likedTracks.first(), likedTracks) },
                colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                shape = RoundedCornerShape(12.dp)
              ) {
                Icon(Icons.Default.PlayArrow, null, tint = TextPrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(LanguageHelper.get("play_all", language), color = TextPrimary)
              }

              Button(
                onClick = { onPlayTrack(likedTracks.shuffled().first(), likedTracks.shuffled()) },
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceCardElevated),
                shape = RoundedCornerShape(12.dp)
              ) {
                Icon(Icons.Default.Shuffle, null, tint = ElectricCyan, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(LanguageHelper.get("shuffle", language), color = TextPrimary)
              }
            }
          }

          items(likedTracks) { track ->
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = SurfaceCard),
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clickable { onPlayTrack(track, likedTracks) }
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(10.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(44.dp)
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
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  Text(
                    text = "${track.artist} • ${track.category}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                  )
                }

                Icon(
                  imageVector = Icons.Default.Favorite,
                  contentDescription = "Liked",
                  tint = HotMagenta,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
          }
        }
      }

      1 -> {
        // PLAYLISTS TAB
        item {
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCardElevated),
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 6.dp)
              .clickable { showCreatePlaylistDialog = true }
              .border(1.dp, NeonViolet.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(14.dp)
            ) {
              Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                  .size(46.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(Brush.linearGradient(listOf(NeonViolet, ElectricCyan)))
              ) {
                Icon(Icons.Default.Add, null, tint = TextPrimary, modifier = Modifier.size(28.dp))
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "Create New Playlist",
                  style = MaterialTheme.typography.titleMedium,
                  color = TextPrimary,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "Build your custom mixes and soundtracks",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextSecondary
                )
              }
            }
          }
        }

        if (userProfile.customPlaylists.isEmpty()) {
          item {
            EmptyLibraryCard(
              icon = Icons.Default.QueueMusic,
              title = "No custom playlists yet",
              description = "Tap 'Create New Playlist' above to start organizing your favorites!"
            )
          }
        } else {
          items(userProfile.customPlaylists) { playlist ->
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = SurfaceCard),
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clickable { onOpenPlaylist(playlist) }
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(10.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                ) {
                  AsyncImage(
                    model = playlist.coverUrl,
                    contentDescription = playlist.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                  )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = playlist.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary
                  )
                  Text(
                    text = "${playlist.trackIds.size} songs • ${playlist.description}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                }
              }
            }
          }
        }
      }

      2 -> {
        // SAVED VIDEOS TAB
        if (savedVideos.isEmpty()) {
          item {
            EmptyLibraryCard(
              icon = Icons.Default.VideoLibrary,
              title = "No saved videos",
              description = "Bookmark viral short videos and full music videos to watch anytime."
            )
          }
        } else {
          items(savedVideos) { vid ->
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = SurfaceCard),
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clickable { onPlayVideo(vid) }
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(10.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(width = 80.dp, height = 50.dp)
                    .clip(RoundedCornerShape(8.dp))
                ) {
                  AsyncImage(
                    model = vid.thumbnailUrl,
                    contentDescription = vid.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                  )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = vid.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  Text(
                    text = "${vid.creator} • ${vid.category}",
                    style = MaterialTheme.typography.bodySmall,
                    color = ElectricCyan
                  )
                }
              }
            }
          }
        }
      }

      3 -> {
        // RECENT HISTORY TAB
        if (historyTracks.isEmpty()) {
          item {
            EmptyLibraryCard(
              icon = Icons.Default.History,
              title = "Listening history is empty",
              description = "Songs you listen to will automatically appear here."
            )
          }
        } else {
          items(historyTracks) { track ->
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = SurfaceCard),
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clickable { onPlayTrack(track, historyTracks) }
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(10.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                ) {
                  AsyncImage(
                    model = track.coverArtUrl,
                    contentDescription = track.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                  )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = track.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    maxLines = 1
                  )
                  Text(
                    text = track.artist,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                  )
                }
                Icon(
                  imageVector = Icons.Default.History,
                  contentDescription = null,
                  tint = TextTertiary,
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }
        }
      }
    }
  }

  // Create Playlist Dialog
  if (showCreatePlaylistDialog) {
    CreatePlaylistDialog(
      onDismiss = { showCreatePlaylistDialog = false },
      onCreate = { title, desc ->
        onCreatePlaylist(title, desc)
        showCreatePlaylistDialog = false
      }
    )
  }
}

@Composable
fun EmptyLibraryCard(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String,
  description: String
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp)
  ) {
    Column(
      modifier = Modifier.padding(28.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(56.dp)
          .clip(CircleShape)
          .background(SurfaceCardElevated)
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = ElectricCyan,
          modifier = Modifier.size(30.dp)
        )
      }
      Spacer(modifier = Modifier.height(12.dp))
      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = TextPrimary,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = description,
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondary,
        modifier = Modifier.padding(horizontal = 8.dp)
      )
    }
  }
}

@Composable
fun CreatePlaylistDialog(
  onDismiss: () -> Unit,
  onCreate: (String, String) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = DarkNavyBg),
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .testTag("create_playlist_dialog")
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "New Playlist",
          style = MaterialTheme.typography.headlineMedium,
          color = TextPrimary,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Playlist Title", color = TextSecondary) },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = SurfaceCard,
            unfocusedContainerColor = SurfaceCard,
            focusedBorderColor = NeonViolet,
            unfocusedBorderColor = SurfaceCardElevated,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("playlist_title_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("Description (Optional)", color = TextSecondary) },
          maxLines = 2,
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = SurfaceCard,
            unfocusedContainerColor = SurfaceCard,
            focusedBorderColor = NeonViolet,
            unfocusedBorderColor = SurfaceCardElevated,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          ),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
          horizontalArrangement = Arrangement.End,
          modifier = Modifier.fillMaxWidth()
        ) {
          TextButton(onClick = onDismiss) {
            Text("Cancel", color = TextSecondary)
          }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = {
              if (title.isNotBlank()) onCreate(title, description)
            },
            colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.testTag("create_playlist_save_button")
          ) {
            Text("Create", color = TextPrimary, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
