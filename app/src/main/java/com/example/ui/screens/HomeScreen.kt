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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
fun HomeScreen(
  tracks: List<MediaTrack>,
  videos: List<MediaVideo>,
  artists: List<Artist>,
  playlists: List<Playlist>,
  userProfile: UserProfile,
  announcements: List<String>,
  currentPlayingTrackId: String?,
  onTrackSelected: (MediaTrack, List<MediaTrack>) -> Unit,
  onVideoSelected: (MediaVideo) -> Unit,
  onShortsReelSelected: (MediaVideo) -> Unit,
  onPlaylistSelected: (Playlist) -> Unit,
  onArtistFollowToggle: (String) -> Unit,
  onFavoriteToggle: (String) -> Unit,
  onLanguageToggle: () -> Unit,
  onOpenSearch: () -> Unit,
  onOpenPremium: () -> Unit,
  modifier: Modifier = Modifier
) {
  val language = userProfile.preferredLanguage
  val categories = listOf("All", "Bengali", "Hindi", "English", "Arabic", "DJ Remix", "Instrumental")
  var selectedCategory by remember { mutableStateOf("All") }

  val filteredTracks = if (selectedCategory == "All") {
    tracks
  } else {
    tracks.filter { it.category.equals(selectedCategory, ignoreCase = true) }
  }

  val heroTrack = tracks.firstOrNull { it.isFeatured } ?: tracks.firstOrNull()
  val viralShorts = videos.filter { it.isShort }
  val musicVideos = videos.filter { !it.isShort }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(DarkNavyBg),
    contentPadding = PaddingValues(bottom = 90.dp)
  ) {
    // Top App Bar
    item {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .size(38.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(Brush.linearGradient(listOf(NeonViolet, ElectricCyan)))
          ) {
            Icon(
              imageVector = Icons.Default.GraphicEq,
              contentDescription = "TrendWave Logo",
              tint = TextPrimary,
              modifier = Modifier.size(24.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "TrendWave",
              style = MaterialTheme.typography.titleLarge,
              color = TextPrimary,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Trending Music & Video",
              style = MaterialTheme.typography.labelSmall,
              color = ElectricCyan
            )
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          // Language Switch
          TextButton(
            onClick = onLanguageToggle,
            modifier = Modifier.testTag("home_language_toggle_button")
          ) {
            Icon(
              imageVector = Icons.Default.Language,
              contentDescription = "Switch Language",
              tint = ElectricCyan,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (language == "bn") "বাংলা" else "EN",
              style = MaterialTheme.typography.labelMedium,
              color = ElectricCyan
            )
          }

          // Search Button
          IconButton(
            onClick = onOpenSearch,
            modifier = Modifier
              .size(44.dp)
              .testTag("home_search_icon_button")
          ) {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "Search",
              tint = TextPrimary
            )
          }

          // VIP Button
          IconButton(
            onClick = onOpenPremium,
            modifier = Modifier
              .size(44.dp)
              .testTag("home_vip_button")
          ) {
            Icon(
              imageVector = Icons.Default.Diamond,
              contentDescription = "TrendWave VIP",
              tint = if (userProfile.isPremium) ElectricCyan else HotMagenta
            )
          }
        }
      }
    }

    // Announcements Banner (if available)
    if (announcements.isNotEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceCard),
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .border(1.dp, NeonViolet.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(10.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Campaign,
              contentDescription = "Announcement",
              tint = ElectricCyan,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = announcements.first(),
              style = MaterialTheme.typography.bodySmall,
              color = TextPrimary,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }
      }
    }

    // Hero Spotlight Card
    if (heroTrack != null) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(20.dp, RoundedCornerShape(20.dp), spotColor = NeonViolet)
            .clip(RoundedCornerShape(20.dp))
            .background(
              Brush.verticalGradient(
                colors = listOf(SurfaceCardElevated, SurfaceCard)
              )
            )
            .clickable { onTrackSelected(heroTrack, tracks) }
            .testTag("hero_spotlight_card")
        ) {
          Box(modifier = Modifier.fillMaxWidth().height(190.dp)) {
            AsyncImage(
              model = heroTrack.coverArtUrl,
              contentDescription = heroTrack.title,
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )
            // Dark gradient overlay
            Box(
              modifier = Modifier
                .fillMaxSize()
                .background(
                  Brush.verticalGradient(
                    colors = listOf(
                      Color.Transparent,
                      DarkNavyBg.copy(alpha = 0.7f),
                      DarkNavyBg.copy(alpha = 0.95f)
                    )
                  )
                )
            )

            // Content on Hero
            Column(
              modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
              verticalArrangement = Arrangement.SpaceBetween
            ) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(Brush.horizontalGradient(listOf(NeonViolet, HotMagenta)))
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(
                  text = "FEATURED RELEASE",
                  style = MaterialTheme.typography.labelSmall,
                  color = TextPrimary,
                  fontWeight = FontWeight.Bold
                )
              }

              Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = heroTrack.title,
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  Text(
                    text = "${heroTrack.artist} • ${heroTrack.category}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ElectricCyan
                  )
                }

                // Play Button
                Box(
                  contentAlignment = Alignment.Center,
                  modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(NeonViolet, ElectricCyan)))
                    .shadow(8.dp, CircleShape)
                ) {
                  Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play Featured Track",
                    tint = TextPrimary,
                    modifier = Modifier.size(32.dp)
                  )
                }
              }
            }
          }
        }
      }
    }

    // Category Filter Chips
    item {
      LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 4.dp)
      ) {
        items(categories) { cat ->
          val isSelected = selectedCategory == cat
          val label = when (cat.lowercase()) {
            "all" -> LanguageHelper.get("all", language)
            "bengali" -> LanguageHelper.get("bengali", language)
            "hindi" -> LanguageHelper.get("hindi", language)
            "english" -> LanguageHelper.get("english", language)
            "arabic" -> LanguageHelper.get("arabic", language)
            "dj remix" -> LanguageHelper.get("dj_remix", language)
            "instrumental" -> LanguageHelper.get("instrumental", language)
            else -> cat
          }

          FilterChip(
            selected = isSelected,
            onClick = { selectedCategory = cat },
            label = { Text(label) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = NeonViolet,
              selectedLabelColor = TextPrimary,
              containerColor = SurfaceCard,
              labelColor = TextSecondary
            ),
            border = FilterChipDefaults.filterChipBorder(
              enabled = true,
              selected = isSelected,
              borderColor = if (isSelected) ElectricCyan else SurfaceCardElevated
            )
          )
        }
      }
    }

    // Trending Songs Horizontal Section
    item {
      SectionHeader(
        title = LanguageHelper.get("trending_today", language),
        actionLabel = LanguageHelper.get("play_all", language),
        onActionClick = {
          if (filteredTracks.isNotEmpty()) {
            onTrackSelected(filteredTracks.first(), filteredTracks)
          }
        }
      )
    }

    item {
      LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        itemsIndexed(filteredTracks) { index, track ->
          TrackCard(
            track = track,
            rank = index + 1,
            isCurrent = track.id == currentPlayingTrackId,
            isFavorite = userProfile.favoriteTrackIds.contains(track.id),
            onPlay = { onTrackSelected(track, filteredTracks) },
            onToggleFavorite = { onFavoriteToggle(track.id) }
          )
        }
      }
    }

    // Viral Shorts Horizontal Preview Reel
    if (viralShorts.isNotEmpty()) {
      item {
        Spacer(modifier = Modifier.height(18.dp))
        SectionHeader(
          title = LanguageHelper.get("viral_shorts", language),
          actionLabel = "Watch All ⚡",
          onActionClick = { onShortsReelSelected(viralShorts.first()) }
        )
      }

      item {
        LazyRow(
          contentPadding = PaddingValues(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(viralShorts) { shortVid ->
            ShortVideoPreviewCard(
              video = shortVid,
              onClick = { onShortsReelSelected(shortVid) }
            )
          }
        }
      }
    }

    // Popular Music Videos (16:9)
    if (musicVideos.isNotEmpty()) {
      item {
        Spacer(modifier = Modifier.height(18.dp))
        SectionHeader(
          title = LanguageHelper.get("popular_videos", language),
          actionLabel = "View All",
          onActionClick = { onVideoSelected(musicVideos.first()) }
        )
      }

      item {
        LazyRow(
          contentPadding = PaddingValues(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          items(musicVideos) { vid ->
            MusicVideoCard(
              video = vid,
              onClick = { onVideoSelected(vid) }
            )
          }
        }
      }
    }

    // Featured Artists Row
    item {
      Spacer(modifier = Modifier.height(18.dp))
      SectionHeader(
        title = LanguageHelper.get("featured_artists", language),
        actionLabel = "",
        onActionClick = {}
      )
    }

    item {
      LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        items(artists) { artist ->
          ArtistCircularCard(
            artist = artist,
            isFollowed = userProfile.followedArtistIds.contains(artist.id),
            onFollowToggle = { onArtistFollowToggle(artist.id) }
          )
        }
      }
    }

    // Mood & Genre Playlists
    item {
      Spacer(modifier = Modifier.height(18.dp))
      SectionHeader(
        title = LanguageHelper.get("mood_playlists", language),
        actionLabel = "",
        onActionClick = {}
      )
    }

    item {
      LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        items(playlists) { playlist ->
          PlaylistCard(
            playlist = playlist,
            onClick = { onPlaylistSelected(playlist) }
          )
        }
      }
    }

    // Sponsored / Ad Banner (if not Premium)
    if (!userProfile.isPremium) {
      item {
        Spacer(modifier = Modifier.height(20.dp))
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceCardElevated),
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onOpenPremium)
            .testTag("home_sponsored_banner")
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(14.dp)
          ) {
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Brush.linearGradient(listOf(NeonViolet, ElectricCyan)))
            ) {
              Icon(
                imageVector = Icons.Default.Diamond,
                contentDescription = "VIP",
                tint = TextPrimary,
                modifier = Modifier.size(28.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF334155))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = LanguageHelper.get("sponsored", language).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = ElectricCyan,
                    fontWeight = FontWeight.Bold
                  )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "TrendWave VIP",
                  style = MaterialTheme.typography.titleSmall,
                  color = TextPrimary,
                  fontWeight = FontWeight.Bold
                )
              }
              Text(
                text = LanguageHelper.get("ad_free_message", language),
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
}

@Composable
fun SectionHeader(
  title: String,
  actionLabel: String,
  onActionClick: () -> Unit
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp)
  ) {
    Text(
      text = title,
      style = MaterialTheme.typography.titleLarge,
      color = TextPrimary,
      fontWeight = FontWeight.Bold
    )
    if (actionLabel.isNotBlank()) {
      Text(
        text = actionLabel,
        style = MaterialTheme.typography.labelLarge,
        color = ElectricCyan,
        modifier = Modifier.clickable(onClick = onActionClick)
      )
    }
  }
}

@Composable
fun TrackCard(
  track: MediaTrack,
  rank: Int,
  isCurrent: Boolean,
  isFavorite: Boolean,
  onPlay: () -> Unit,
  onToggleFavorite: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isCurrent) NeonViolet.copy(alpha = 0.2f) else SurfaceCard
    ),
    modifier = Modifier
      .width(150.dp)
      .clickable(onClick = onPlay)
      .border(
        width = if (isCurrent) 1.5.dp else 0.5.dp,
        color = if (isCurrent) ElectricCyan else SurfaceCardElevated,
        shape = RoundedCornerShape(16.dp)
      )
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      Box(
        modifier = Modifier
          .size(130.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(SurfaceCardElevated)
      ) {
        AsyncImage(
          model = track.coverArtUrl,
          contentDescription = track.title,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )

        // Rank Badge
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .padding(6.dp)
            .size(24.dp)
            .clip(CircleShape)
            .background(DarkNavyBg.copy(alpha = 0.85f))
        ) {
          Text(
            text = "#$rank",
            style = MaterialTheme.typography.labelSmall,
            color = ElectricCyan,
            fontWeight = FontWeight.Bold
          )
        }

        // Quick favorite button
        IconButton(
          onClick = onToggleFavorite,
          modifier = Modifier
            .align(Alignment.TopEnd)
            .size(32.dp)
        ) {
          Icon(
            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "Favorite",
            tint = if (isFavorite) HotMagenta else Color.White,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

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
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(4.dp))

      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(SurfaceCardElevated)
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = track.category,
            style = MaterialTheme.typography.labelSmall,
            color = TextTertiary
          )
        }
      }
    }
  }
}

@Composable
fun ShortVideoPreviewCard(
  video: MediaVideo,
  onClick: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
    modifier = Modifier
      .width(130.dp)
      .height(200.dp)
      .clickable(onClick = onClick)
      .border(1.dp, NeonViolet.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
  ) {
    Box(modifier = Modifier.fillMaxSize()) {
      AsyncImage(
        model = video.thumbnailUrl,
        contentDescription = video.title,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
      )

      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.verticalGradient(
              colors = listOf(Color.Transparent, DarkNavyBg.copy(alpha = 0.9f))
            )
          )
      )

      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(HotMagenta)
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = "SHORTS",
            style = MaterialTheme.typography.labelSmall,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
          )
        }

        Column {
          Text(
            text = video.title,
            style = MaterialTheme.typography.labelMedium,
            color = TextPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = "${video.viewsCount / 1000}k views",
            style = MaterialTheme.typography.labelSmall,
            color = ElectricCyan
          )
        }
      }
    }
  }
}

@Composable
fun MusicVideoCard(
  video: MediaVideo,
  onClick: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
    modifier = Modifier
      .width(240.dp)
      .clickable(onClick = onClick)
  ) {
    Column {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(135.dp)
          .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
      ) {
        AsyncImage(
          model = video.thumbnailUrl,
          contentDescription = video.title,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )

        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .align(Alignment.Center)
            .size(40.dp)
            .clip(CircleShape)
            .background(DarkNavyBg.copy(alpha = 0.8f))
        ) {
          Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Play Video",
            tint = ElectricCyan,
            modifier = Modifier.size(24.dp)
          )
        }

        // Duration chip
        Box(
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(6.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(DarkNavyBg.copy(alpha = 0.85f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          val mins = video.durationSeconds / 60
          val secs = video.durationSeconds % 60
          Text(
            text = String.format("%d:%02d", mins, secs),
            style = MaterialTheme.typography.labelSmall,
            color = TextPrimary
          )
        }
      }

      Column(modifier = Modifier.padding(10.dp)) {
        Text(
          text = video.title,
          style = MaterialTheme.typography.titleSmall,
          color = TextPrimary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          text = "${video.creator} • ${video.viewsCount / 1000000}M views",
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondary
        )
      }
    }
  }
}

@Composable
fun ArtistCircularCard(
  artist: Artist,
  isFollowed: Boolean,
  onFollowToggle: () -> Unit
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.width(96.dp)
  ) {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
        .size(80.dp)
        .clip(CircleShape)
        .border(2.dp, Brush.linearGradient(listOf(NeonViolet, ElectricCyan)), CircleShape)
    ) {
      AsyncImage(
        model = artist.imageUrl,
        contentDescription = artist.name,
        contentScale = ContentScale.Crop,
        modifier = Modifier
          .fillMaxSize()
          .clip(CircleShape)
      )
    }

    Spacer(modifier = Modifier.height(6.dp))

    Text(
      text = artist.name,
      style = MaterialTheme.typography.labelMedium,
      color = TextPrimary,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )

    Spacer(modifier = Modifier.height(4.dp))

    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
        .clip(RoundedCornerShape(12.dp))
        .background(if (isFollowed) SurfaceCardElevated else NeonViolet)
        .clickable(onClick = onFollowToggle)
        .padding(horizontal = 10.dp, vertical = 3.dp)
    ) {
      Text(
        text = if (isFollowed) "Following" else "+ Follow",
        style = MaterialTheme.typography.labelSmall,
        color = if (isFollowed) ElectricCyan else TextPrimary,
        fontWeight = FontWeight.SemiBold
      )
    }
  }
}

@Composable
fun PlaylistCard(
  playlist: Playlist,
  onClick: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
    modifier = Modifier
      .width(160.dp)
      .clickable(onClick = onClick)
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      Box(
        modifier = Modifier
          .size(140.dp)
          .clip(RoundedCornerShape(12.dp))
      ) {
        AsyncImage(
          model = playlist.coverUrl,
          contentDescription = playlist.title,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = playlist.title,
        style = MaterialTheme.typography.titleSmall,
        color = TextPrimary,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      Text(
        text = "${playlist.trackIds.size} tracks",
        style = MaterialTheme.typography.bodySmall,
        color = ElectricCyan
      )
    }
  }
}
