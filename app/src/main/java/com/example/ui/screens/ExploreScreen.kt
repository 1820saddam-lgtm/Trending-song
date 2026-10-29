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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import coil.compose.AsyncImage
import com.example.model.MediaTrack
import com.example.model.TrendingFilter
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
fun ExploreScreen(
  tracks: List<MediaTrack>,
  selectedFilter: TrendingFilter,
  favoriteTrackIds: Set<String>,
  currentPlayingTrackId: String?,
  language: String,
  onFilterSelected: (TrendingFilter) -> Unit,
  onTrackSelected: (MediaTrack, List<MediaTrack>) -> Unit,
  onToggleFavorite: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var searchQuery by remember { mutableStateOf("") }

  // Autocomplete quick queries
  val autocompleteKeywords = listOf(
    "Tahsan", "Arijit Singh", "Deora", "The Weeknd", "Taylor Swift", "Bengali", "DJ Remix", "Oud"
  )

  // Filtered tracks
  val displayedTracks = tracks.filter { trk ->
    if (searchQuery.isBlank()) true
    else trk.title.contains(searchQuery, ignoreCase = true) ||
      trk.artist.contains(searchQuery, ignoreCase = true) ||
      trk.category.contains(searchQuery, ignoreCase = true) ||
      trk.album.contains(searchQuery, ignoreCase = true)
  }

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
              text = LanguageHelper.get("nav_explore", language),
              style = MaterialTheme.typography.headlineLarge,
              color = TextPrimary,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Discover top charts, viral anthems & regional hits",
              style = MaterialTheme.typography.bodySmall,
              color = ElectricCyan
            )
          }

          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(NeonViolet.copy(alpha = 0.2f))
          ) {
            Icon(
              imageVector = Icons.Default.TrendingUp,
              contentDescription = "Trending",
              tint = NeonViolet
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar with clear
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = {
            Text(
              text = LanguageHelper.get("search_hint", language),
              color = TextTertiary
            )
          },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "Search",
              tint = ElectricCyan
            )
          },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(
                  imageVector = Icons.Default.Clear,
                  contentDescription = "Clear",
                  tint = TextSecondary
                )
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(16.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = SurfaceCard,
            unfocusedContainerColor = SurfaceCard,
            focusedBorderColor = ElectricCyan,
            unfocusedBorderColor = SurfaceCardElevated,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("explore_search_text_field")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Autocomplete suggestions
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(autocompleteKeywords) { kw ->
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(if (searchQuery == kw) NeonViolet else SurfaceCard)
                .clickable { searchQuery = kw }
                .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Text(
                text = kw,
                style = MaterialTheme.typography.labelSmall,
                color = if (searchQuery == kw) TextPrimary else TextSecondary
              )
            }
          }
        }
      }
    }

    // Trending Filters Row
    item {
      Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
          Icon(
            imageVector = Icons.Default.FilterList,
            contentDescription = "Filters",
            tint = ElectricCyan,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "TRENDING BY RANKING",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            fontWeight = FontWeight.Bold
          )
        }

        LazyRow(
          contentPadding = PaddingValues(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(TrendingFilter.values()) { filter ->
            val isSelected = selectedFilter == filter
            val label = if (language == "bn") filter.labelBn else filter.labelEn
            FilterChip(
              selected = isSelected,
              onClick = { onFilterSelected(filter) },
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
    }

    // Results Header
    item {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp)
      ) {
        Text(
          text = if (searchQuery.isBlank()) "Top Charts (${displayedTracks.size})" else "Search Results (${displayedTracks.size})",
          style = MaterialTheme.typography.titleMedium,
          color = TextPrimary,
          fontWeight = FontWeight.Bold
        )
      }
    }

    // Empty search state
    if (displayedTracks.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceCard),
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "No matching songs found",
              style = MaterialTheme.typography.titleMedium,
              color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Try searching for Bengali, Hindi, English songs or artist names like Tahsan, Arijit Singh, The Weeknd.",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary
            )
          }
        }
      }
    } else {
      // List of Top Ranked Tracks
      itemsIndexed(displayedTracks) { index, track ->
        val isCurrent = track.id == currentPlayingTrackId
        val isFavorite = favoriteTrackIds.contains(track.id)

        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (isCurrent) NeonViolet.copy(alpha = 0.2f) else SurfaceCard
          ),
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onTrackSelected(track, displayedTracks) }
            .border(
              width = if (isCurrent) 1.dp else 0.dp,
              color = if (isCurrent) ElectricCyan else Color.Transparent,
              shape = RoundedCornerShape(14.dp)
            )
            .testTag("explore_track_item_$index")
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(10.dp)
          ) {
            // Rank Number
            Text(
              text = String.format("%02d", index + 1),
              style = MaterialTheme.typography.titleSmall,
              color = if (index < 3) ElectricCyan else TextTertiary,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.width(32.dp)
            )

            // Cover Art
            Box(
              modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(SurfaceCardElevated)
            ) {
              AsyncImage(
                model = track.coverArtUrl,
                contentDescription = track.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
              )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Title & Artist
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = track.title,
                style = MaterialTheme.typography.titleSmall,
                color = if (isCurrent) ElectricCyan else TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Text(
                text = "${track.artist} • ${track.category}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }

            // Stream stats
            Text(
              text = "${track.playsCount / 1000000}M",
              style = MaterialTheme.typography.labelSmall,
              color = TextTertiary,
              modifier = Modifier.padding(horizontal = 6.dp)
            )

            // Favorite button
            IconButton(
              onClick = { onToggleFavorite(track.id) },
              modifier = Modifier.size(40.dp)
            ) {
              Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (isFavorite) HotMagenta else TextSecondary,
                modifier = Modifier.size(20.dp)
              )
            }

            // Play icon
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(if (isCurrent) ElectricCyan else SurfaceCardElevated)
            ) {
              Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play",
                tint = if (isCurrent) DarkNavyBg else TextPrimary,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }
      }
    }
  }
}
