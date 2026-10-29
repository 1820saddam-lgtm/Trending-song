package com.example.ui.screens

import android.net.Uri
import android.widget.VideoView
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.ClosedCaptionDisabled
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.model.MediaVideo
import com.example.model.VideoComment
import com.example.model.VideoQuality
import com.example.ui.components.LanguageHelper
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
fun VideosScreen(
  videos: List<MediaVideo>,
  commentsMap: Map<String, List<VideoComment>>,
  favoriteVideoIds: Set<String>,
  language: String,
  onToggleFavoriteVideo: (String) -> Unit,
  onAddComment: (String, String) -> Unit,
  onPlayAssociatedTrack: (String) -> Unit,
  initialSelectedVideo: MediaVideo? = null,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableIntStateOf(if (initialSelectedVideo?.isShort == false) 1 else 0) }
  val shortsList = videos.filter { it.isShort }
  val musicVideosList = videos.filter { !it.isShort }

  // Active Short Video index for swipe/navigate
  var currentShortIndex by remember {
    mutableIntStateOf(
      if (initialSelectedVideo != null && initialSelectedVideo.isShort) {
        val idx = shortsList.indexOfFirst { it.id == initialSelectedVideo.id }
        if (idx >= 0) idx else 0
      } else 0
    )
  }

  // Active Widescreen Video Player Modal
  var activeMusicVideo by remember {
    mutableStateOf(if (initialSelectedVideo != null && !initialSelectedVideo.isShort) initialSelectedVideo else null)
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MidnightBlack)
  ) {
    // Top Tab Selector (Shorts vs Music Videos)
    TabRow(
      selectedTabIndex = selectedTab,
      containerColor = DarkNavyBg,
      contentColor = TextPrimary,
      indicator = { tabPositions ->
        TabRowDefaults.SecondaryIndicator(
          modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
          color = NeonViolet
        )
      }
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = {
          Text(
            text = "Viral Shorts ⚡",
            style = MaterialTheme.typography.titleSmall,
            color = if (selectedTab == 0) ElectricCyan else TextSecondary
          )
        },
        modifier = Modifier.testTag("tab_viral_shorts")
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = {
          Text(
            text = "Music Videos 🎬",
            style = MaterialTheme.typography.titleSmall,
            color = if (selectedTab == 1) ElectricCyan else TextSecondary
          )
        },
        modifier = Modifier.testTag("tab_music_videos")
      )
    }

    if (selectedTab == 0) {
      // VERTICAL SHORTS FEED
      if (shortsList.isNotEmpty()) {
        val currentVideo = shortsList[currentShortIndex % shortsList.size]
        ShortVideoViewer(
          video = currentVideo,
          comments = commentsMap[currentVideo.id] ?: emptyList(),
          isFavorite = favoriteVideoIds.contains(currentVideo.id),
          language = language,
          onToggleFavorite = { onToggleFavoriteVideo(currentVideo.id) },
          onAddComment = { text -> onAddComment(currentVideo.id, text) },
          onPlayAssociatedTrack = {
            currentVideo.associatedTrackId?.let { onPlayAssociatedTrack(it) }
          },
          onNext = {
            currentShortIndex = (currentShortIndex + 1) % shortsList.size
          },
          onPrev = {
            currentShortIndex = if (currentShortIndex > 0) currentShortIndex - 1 else shortsList.size - 1
          },
          hasPrev = shortsList.size > 1,
          hasNext = shortsList.size > 1,
          modifier = Modifier.weight(1f)
        )
      }
    } else {
      // MUSIC VIDEOS GRID / FEED
      MusicVideosFeed(
        videos = musicVideosList,
        favoriteVideoIds = favoriteVideoIds,
        onVideoSelected = { activeMusicVideo = it },
        onToggleFavorite = onToggleFavoriteVideo,
        modifier = Modifier.weight(1f)
      )
    }
  }

  // Full Screen Music Video Player Dialog
  activeMusicVideo?.let { vid ->
    MusicVideoPlayerDialog(
      video = vid,
      comments = commentsMap[vid.id] ?: emptyList(),
      isFavorite = favoriteVideoIds.contains(vid.id),
      onDismiss = { activeMusicVideo = null },
      onToggleFavorite = { onToggleFavoriteVideo(vid.id) },
      onAddComment = { text -> onAddComment(vid.id, text) }
    )
  }
}

@Composable
fun ShortVideoViewer(
  video: MediaVideo,
  comments: List<VideoComment>,
  isFavorite: Boolean,
  language: String,
  onToggleFavorite: () -> Unit,
  onAddComment: (String) -> Unit,
  onPlayAssociatedTrack: () -> Unit,
  onNext: () -> Unit,
  onPrev: () -> Unit,
  hasPrev: Boolean,
  hasNext: Boolean,
  modifier: Modifier = Modifier
) {
  var isPlaying by remember { mutableStateOf(true) }
  var showCommentsSheet by remember { mutableStateOf(false) }
  var showQualityMenu by remember { mutableStateOf(false) }
  var selectedQuality by remember { mutableStateOf(VideoQuality.AUTO) }
  var showCaptions by remember { mutableStateOf(true) }

  // Rotating audio disc animation
  val rotation = remember { Animatable(0f) }
  LaunchedEffect(isPlaying) {
    if (isPlaying) {
      rotation.animateTo(
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
          animation = tween(4000, easing = LinearEasing),
          repeatMode = RepeatMode.Restart
        )
      )
    } else {
      rotation.stop()
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(MidnightBlack)
      .testTag("short_video_viewer")
  ) {
    // Video surface
    VideoPlayerSurface(
      videoUrl = video.videoUrl,
      thumbnailUrl = video.thumbnailUrl,
      isPlaying = isPlaying,
      modifier = Modifier.fillMaxSize()
    )

    // Tap overlay to play/pause
    Box(
      modifier = Modifier
        .fillMaxSize()
        .clickable { isPlaying = !isPlaying }
    )

    // Play/Pause indicator icon when paused
    if (!isPlaying) {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .align(Alignment.Center)
          .size(70.dp)
          .clip(CircleShape)
          .background(MidnightBlack.copy(alpha = 0.6f))
      ) {
        Icon(
          imageVector = Icons.Default.PlayArrow,
          contentDescription = "Paused",
          tint = TextPrimary,
          modifier = Modifier.size(44.dp)
        )
      }
    }

    // Top Controls Bar (Quality & Captions & Navigation)
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp)
        .align(Alignment.TopCenter)
    ) {
      // Prev / Next quick buttons
      Row(verticalAlignment = Alignment.CenterVertically) {
        if (hasPrev) {
          IconButton(
            onClick = onPrev,
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(DarkNavyBg.copy(alpha = 0.7f))
          ) {
            Icon(Icons.Default.NavigateBefore, "Previous Video", tint = TextPrimary)
          }
        }
        Spacer(modifier = Modifier.width(6.dp))
        if (hasNext) {
          IconButton(
            onClick = onNext,
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(DarkNavyBg.copy(alpha = 0.7f))
          ) {
            Icon(Icons.Default.NavigateNext, "Next Video", tint = TextPrimary)
          }
        }
      }

      // Quality & Captions options
      Row(verticalAlignment = Alignment.CenterVertically) {
        // Captions toggle
        IconButton(
          onClick = { showCaptions = !showCaptions },
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(DarkNavyBg.copy(alpha = 0.7f))
        ) {
          Icon(
            imageVector = if (showCaptions) Icons.Default.ClosedCaption else Icons.Default.ClosedCaptionDisabled,
            contentDescription = "Captions",
            tint = if (showCaptions) ElectricCyan else TextSecondary
          )
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Quality Picker
        Box {
          IconButton(
            onClick = { showQualityMenu = true },
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(DarkNavyBg.copy(alpha = 0.7f))
          ) {
            Icon(Icons.Default.HighQuality, "Quality", tint = ElectricCyan)
          }

          DropdownMenu(
            expanded = showQualityMenu,
            onDismissRequest = { showQualityMenu = false },
            modifier = Modifier.background(SurfaceCardElevated)
          ) {
            VideoQuality.values().forEach { q ->
              DropdownMenuItem(
                text = { Text(q.label, color = if (selectedQuality == q) ElectricCyan else TextPrimary) },
                onClick = {
                  selectedQuality = q
                  showQualityMenu = false
                }
              )
            }
          }
        }
      }
    }

    // Right Action Rail (Likes, Comments, Share, Disc)
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp),
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(end = 12.dp, bottom = 80.dp)
    ) {
      // Like / Heart Button
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
          onClick = onToggleFavorite,
          modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(DarkNavyBg.copy(alpha = 0.6f))
            .testTag("shorts_like_button")
        ) {
          Icon(
            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "Like",
            tint = if (isFavorite) HotMagenta else TextPrimary,
            modifier = Modifier.size(28.dp)
          )
        }
        Text(
          text = "${video.likesCount / 1000}k",
          style = MaterialTheme.typography.labelSmall,
          color = TextPrimary
        )
      }

      // Comment Button
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
          onClick = { showCommentsSheet = true },
          modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(DarkNavyBg.copy(alpha = 0.6f))
            .testTag("shorts_comment_button")
        ) {
          Icon(
            imageVector = Icons.Default.ChatBubble,
            contentDescription = "Comments",
            tint = TextPrimary,
            modifier = Modifier.size(24.dp)
          )
        }
        Text(
          text = "${video.commentsCount}",
          style = MaterialTheme.typography.labelSmall,
          color = TextPrimary
        )
      }

      // Save / Bookmark
      IconButton(
        onClick = onToggleFavorite,
        modifier = Modifier
          .size(48.dp)
          .clip(CircleShape)
          .background(DarkNavyBg.copy(alpha = 0.6f))
      ) {
        Icon(
          imageVector = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
          contentDescription = "Save",
          tint = if (isFavorite) ElectricCyan else TextPrimary,
          modifier = Modifier.size(24.dp)
        )
      }

      // Rotating sound disc
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(44.dp)
          .clip(CircleShape)
          .background(DarkNavyBg)
          .border(2.dp, Brush.sweepGradient(listOf(NeonViolet, ElectricCyan, HotMagenta, NeonViolet)), CircleShape)
          .rotate(rotation.value)
          .clickable(onClick = onPlayAssociatedTrack)
      ) {
        Icon(
          imageVector = Icons.Default.MusicNote,
          contentDescription = "Audio Track",
          tint = ElectricCyan,
          modifier = Modifier.size(20.dp)
        )
      }
    }

    // Bottom Info Overlay (Creator, Title, Tags, Track)
    Column(
      modifier = Modifier
        .align(Alignment.BottomStart)
        .fillMaxWidth(0.78f)
        .padding(start = 16.dp, bottom = 80.dp)
    ) {
      // Subtitles overlay if enabled
      if (showCaptions) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(MidnightBlack.copy(alpha = 0.75f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = "♪ [${video.category} Vibe]: ${video.title} ♪",
            style = MaterialTheme.typography.labelSmall,
            color = ElectricCyan
          )
        }
        Spacer(modifier = Modifier.height(6.dp))
      }

      Text(
        text = video.creator,
        style = MaterialTheme.typography.titleMedium,
        color = TextPrimary,
        fontWeight = FontWeight.Bold
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = video.description,
        style = MaterialTheme.typography.bodySmall,
        color = TextPrimary,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(6.dp))

      // Tags
      Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        video.tags.take(3).forEach { tag ->
          Text(
            text = tag,
            style = MaterialTheme.typography.labelSmall,
            color = ElectricCyan,
            fontWeight = FontWeight.SemiBold
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Sound track link
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .background(DarkNavyBg.copy(alpha = 0.7f))
          .clickable(onClick = onPlayAssociatedTrack)
          .padding(horizontal = 10.dp, vertical = 4.dp)
      ) {
        Icon(
          imageVector = Icons.Default.MusicNote,
          contentDescription = null,
          tint = NeonViolet,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Original Audio • ${video.creator}",
          style = MaterialTheme.typography.labelSmall,
          color = TextPrimary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }

  // Comments Sheet Dialog
  if (showCommentsSheet) {
    CommentsDialog(
      comments = comments,
      onDismiss = { showCommentsSheet = false },
      onPostComment = onAddComment
    )
  }
}

@Composable
fun VideoPlayerSurface(
  videoUrl: String,
  thumbnailUrl: String,
  isPlaying: Boolean,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var hasVideoError by remember { mutableStateOf(false) }

  Box(modifier = modifier) {
    if (!hasVideoError && videoUrl.isNotBlank()) {
      AndroidView(
        factory = { ctx ->
          VideoView(ctx).apply {
            setVideoURI(Uri.parse(videoUrl))
            setOnPreparedListener { mp ->
              mp.isLooping = true
              if (isPlaying) start()
            }
            setOnErrorListener { _, _, _ ->
              hasVideoError = true
              true
            }
          }
        },
        update = { vv ->
          if (isPlaying && !vv.isPlaying) {
            vv.start()
          } else if (!isPlaying && vv.isPlaying) {
            vv.pause()
          }
        },
        modifier = Modifier.fillMaxSize()
      )
    }

    // High quality thumbnail fallback
    if (hasVideoError || videoUrl.isBlank()) {
      AsyncImage(
        model = thumbnailUrl,
        contentDescription = "Video Thumbnail",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
      )
    }

    // Atmospheric dark gradient overlay
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            colors = listOf(
              DarkNavyBg.copy(alpha = 0.4f),
              Color.Transparent,
              DarkNavyBg.copy(alpha = 0.85f)
            )
          )
        )
    )
  }
}

@Composable
fun CommentsDialog(
  comments: List<VideoComment>,
  onDismiss: () -> Unit,
  onPostComment: (String) -> Unit
) {
  var newCommentText by remember { mutableStateOf("") }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = DarkNavyBg),
      modifier = Modifier
        .fillMaxWidth()
        .height(480.dp)
        .padding(8.dp)
        .testTag("comments_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
      ) {
        // Header
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "Comments (${comments.size})",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
          )
          IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Default.Close, "Close", tint = TextSecondary)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // List of comments
        LazyColumn(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(comments) { comment ->
            Row(modifier = Modifier.fillMaxWidth()) {
              Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(Color(comment.userAvatarColor))
              ) {
                Text(
                  text = comment.userName.take(1).uppercase(),
                  style = MaterialTheme.typography.labelMedium,
                  color = Color.White,
                  fontWeight = FontWeight.Bold
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = comment.userName,
                    style = MaterialTheme.typography.labelMedium,
                    color = ElectricCyan,
                    fontWeight = FontWeight.SemiBold
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = comment.timeAgo,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary
                  )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = comment.text,
                  style = MaterialTheme.typography.bodySmall,
                  color = TextPrimary
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Input field
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          OutlinedTextField(
            value = newCommentText,
            onValueChange = { newCommentText = it },
            placeholder = { Text("Add a comment...", color = TextTertiary) },
            singleLine = true,
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = SurfaceCard,
              unfocusedContainerColor = SurfaceCard,
              focusedBorderColor = NeonViolet,
              unfocusedBorderColor = SurfaceCardElevated,
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary
            ),
            modifier = Modifier.weight(1f)
          )
          Spacer(modifier = Modifier.width(8.dp))
          IconButton(
            onClick = {
              if (newCommentText.isNotBlank()) {
                onPostComment(newCommentText)
                newCommentText = ""
              }
            },
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(NeonViolet)
          ) {
            Icon(Icons.Default.Send, "Send", tint = TextPrimary, modifier = Modifier.size(20.dp))
          }
        }
      }
    }
  }
}

@Composable
fun MusicVideosFeed(
  videos: List<MediaVideo>,
  favoriteVideoIds: Set<String>,
  onVideoSelected: (MediaVideo) -> Unit,
  onToggleFavorite: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedCategory by remember { mutableStateOf("All") }
  val categories = listOf("All", "Bengali", "Hindi", "English")

  val filtered = if (selectedCategory == "All") {
    videos
  } else {
    videos.filter { it.category.equals(selectedCategory, ignoreCase = true) }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(categories) { cat ->
          val isSelected = selectedCategory == cat
          FilterChip(
            selected = isSelected,
            onClick = { selectedCategory = cat },
            label = { Text(cat) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = NeonViolet,
              selectedLabelColor = TextPrimary,
              containerColor = SurfaceCard,
              labelColor = TextSecondary
            )
          )
        }
      }
    }

    items(filtered) { video ->
      val isFavorite = favoriteVideoIds.contains(video.id)

      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onVideoSelected(video) }
          .border(1.dp, SurfaceCardElevated, RoundedCornerShape(18.dp))
      ) {
        Column {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(180.dp)
          ) {
            AsyncImage(
              model = video.thumbnailUrl,
              contentDescription = video.title,
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )

            // Play Icon
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier
                .align(Alignment.Center)
                .size(48.dp)
                .clip(CircleShape)
                .background(DarkNavyBg.copy(alpha = 0.85f))
            ) {
              Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play Video",
                tint = ElectricCyan,
                modifier = Modifier.size(30.dp)
              )
            }

            // Duration
            Box(
              modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(MidnightBlack.copy(alpha = 0.85f))
                .padding(horizontal = 8.dp, vertical = 3.dp)
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

          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              verticalAlignment = Alignment.Top,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = video.title,
                  style = MaterialTheme.typography.titleMedium,
                  color = TextPrimary,
                  maxLines = 2,
                  overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "${video.creator} • ${video.viewsCount / 1000000}M views • ${video.category}",
                  style = MaterialTheme.typography.bodySmall,
                  color = ElectricCyan
                )
              }

              IconButton(
                onClick = { onToggleFavorite(video.id) },
                modifier = Modifier.size(40.dp)
              ) {
                Icon(
                  imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                  contentDescription = "Favorite",
                  tint = if (isFavorite) HotMagenta else TextSecondary,
                  modifier = Modifier.size(22.dp)
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun MusicVideoPlayerDialog(
  video: MediaVideo,
  comments: List<VideoComment>,
  isFavorite: Boolean,
  onDismiss: () -> Unit,
  onToggleFavorite: () -> Unit,
  onAddComment: (String) -> Unit
) {
  var isPlaying by remember { mutableStateOf(true) }
  var showComments by remember { mutableStateOf(false) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      color = MidnightBlack,
      modifier = Modifier.fillMaxSize()
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // Video Player Box
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .background(Color.Black)
        ) {
          VideoPlayerSurface(
            videoUrl = video.videoUrl,
            thumbnailUrl = video.thumbnailUrl,
            isPlaying = isPlaying,
            modifier = Modifier.fillMaxSize()
          )

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .align(Alignment.TopStart)
              .padding(8.dp)
              .size(40.dp)
              .clip(CircleShape)
              .background(DarkNavyBg.copy(alpha = 0.7f))
          ) {
            Icon(Icons.Default.Close, "Close", tint = TextPrimary)
          }

          IconButton(
            onClick = { isPlaying = !isPlaying },
            modifier = Modifier
              .align(Alignment.Center)
              .size(54.dp)
              .clip(CircleShape)
              .background(MidnightBlack.copy(alpha = 0.6f))
          ) {
            Icon(
              imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
              contentDescription = if (isPlaying) "Pause" else "Play",
              tint = TextPrimary,
              modifier = Modifier.size(32.dp)
            )
          }
        }

        // Video Details & Comments
        LazyColumn(
          modifier = Modifier
            .weight(1f)
            .padding(16.dp)
        ) {
          item {
            Text(
              text = video.title,
              style = MaterialTheme.typography.titleLarge,
              color = TextPrimary,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "${video.viewsCount / 1000000}M views • ${video.dateAdded}",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Action row: Like, Share, Comments
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceAround
            ) {
              Button(
                onClick = onToggleFavorite,
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (isFavorite) HotMagenta.copy(alpha = 0.2f) else SurfaceCardElevated
                )
              ) {
                Icon(
                  imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                  contentDescription = null,
                  tint = if (isFavorite) HotMagenta else TextPrimary,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "${video.likesCount / 1000}k",
                  color = if (isFavorite) HotMagenta else TextPrimary
                )
              }

              Button(
                onClick = { showComments = !showComments },
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceCardElevated)
              ) {
                Icon(Icons.Default.ChatBubble, null, tint = ElectricCyan, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Comments (${comments.size})", color = TextPrimary)
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
              text = "About this Video",
              style = MaterialTheme.typography.titleSmall,
              color = ElectricCyan,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = video.description,
              style = MaterialTheme.typography.bodyMedium,
              color = TextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))
          }

          if (showComments) {
            item {
              Text(
                text = "Comments",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(8.dp))
            }

            items(comments) { comment ->
              Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp)
              ) {
                Column(modifier = Modifier.padding(10.dp)) {
                  Text(
                    text = comment.userName,
                    style = MaterialTheme.typography.labelMedium,
                    color = ElectricCyan,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = comment.text,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimary
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
