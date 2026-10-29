package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.FullScreenPlayerSheet
import com.example.ui.components.LanguageHelper
import com.example.ui.components.MiniPlayerBar
import com.example.ui.components.PlaylistDetailDialog
import com.example.ui.components.PremiumDialog
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.ProfileAdminScreen
import com.example.ui.screens.VideosScreen
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MidnightBlack
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TrendWaveTheme
import com.example.viewmodel.TrendWaveViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      TrendWaveTheme {
        TrendWaveApp()
      }
    }
  }
}

@Composable
fun TrendWaveApp(
  viewModel: TrendWaveViewModel = viewModel()
) {
  val currentNavIndex by viewModel.currentNavIndex.collectAsState()
  val showFullScreenPlayer by viewModel.showFullScreenPlayer.collectAsState()
  val showPremiumDialog by viewModel.showPremiumDialog.collectAsState()
  val selectedVideoForPlayback by viewModel.selectedVideoForPlayback.collectAsState()
  val activePlaylistDetail by viewModel.activePlaylistDetail.collectAsState()
  val selectedTrendingFilter by viewModel.selectedTrendingFilter.collectAsState()

  val tracks by viewModel.tracks.collectAsState()
  val videos by viewModel.videos.collectAsState()
  val artists by viewModel.artists.collectAsState()
  val playlists by viewModel.playlists.collectAsState()
  val comments by viewModel.comments.collectAsState()
  val userProfile by viewModel.userProfile.collectAsState()
  val adminAnalytics by viewModel.adminAnalytics.collectAsState()
  val announcements by viewModel.announcements.collectAsState()

  val currentTrack by viewModel.currentTrack.collectAsState()
  val isPlaying by viewModel.isPlaying.collectAsState()
  val playbackPositionMs by viewModel.playbackPositionMs.collectAsState()
  val durationMs by viewModel.durationMs.collectAsState()
  val isShuffle by viewModel.isShuffle.collectAsState()
  val repeatMode by viewModel.repeatMode.collectAsState()
  val volume by viewModel.volume.collectAsState()
  val playbackSpeed by viewModel.playbackSpeed.collectAsState()
  val visualizerLevels by viewModel.visualizerLevels.collectAsState()
  val queue by viewModel.queue.collectAsState()
  val currentQueueIndex by viewModel.currentQueueIndex.collectAsState()

  val language = userProfile.preferredLanguage

  // System Back Navigation
  BackHandler(enabled = showFullScreenPlayer || activePlaylistDetail != null || currentNavIndex != 0) {
    if (showFullScreenPlayer) {
      viewModel.setShowFullScreenPlayer(false)
    } else if (activePlaylistDetail != null) {
      viewModel.closePlaylistDetail()
    } else if (currentNavIndex != 0) {
      viewModel.setNavIndex(0)
    }
  }

  Scaffold(
    containerColor = MidnightBlack,
    bottomBar = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(MidnightBlack)
      ) {
        // Floating Mini-Player bar (docked above bottom nav)
        MiniPlayerBar(
          currentTrack = currentTrack,
          isPlaying = isPlaying,
          progressMs = playbackPositionMs,
          durationMs = durationMs,
          isFavorite = currentTrack?.let { userProfile.favoriteTrackIds.contains(it.id) } ?: false,
          visualizerLevels = visualizerLevels,
          onTogglePlayPause = { viewModel.playbackManager.togglePlayPause() },
          onSkipNext = { viewModel.playbackManager.skipNext() },
          onToggleFavorite = { currentTrack?.let { viewModel.toggleTrackFavorite(it.id) } },
          onOpenFullScreen = { viewModel.setShowFullScreenPlayer(true) }
        )

        // Bottom Navigation Bar
        NavigationBar(
          containerColor = DarkNavyBg,
          contentColor = TextPrimary,
          tonalElevation = 8.dp,
          modifier = Modifier.testTag("main_bottom_nav_bar")
        ) {
          val navItems = listOf(
            Triple(0, LanguageHelper.get("nav_home", language), Icons.Default.Home),
            Triple(1, LanguageHelper.get("nav_explore", language), Icons.Default.Explore),
            Triple(2, LanguageHelper.get("nav_videos", language), Icons.Default.PlayCircle),
            Triple(3, LanguageHelper.get("nav_library", language), Icons.Default.LibraryMusic),
            Triple(4, LanguageHelper.get("nav_profile", language), Icons.Default.Person)
          )

          navItems.forEach { (index, label, icon) ->
            val isSelected = currentNavIndex == index
            NavigationBarItem(
              selected = isSelected,
              onClick = { viewModel.setNavIndex(index) },
              icon = {
                Icon(
                  imageVector = icon,
                  contentDescription = label,
                  modifier = Modifier.size(24.dp)
                )
              },
              label = {
                Text(
                  text = label,
                  style = MaterialTheme.typography.labelSmall
                )
              },
              colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TextPrimary,
                selectedTextColor = ElectricCyan,
                indicatorColor = NeonViolet,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
              ),
              modifier = Modifier.testTag("nav_item_$index")
            )
          }
        }
      }
    },
    modifier = Modifier.fillMaxSize()
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .statusBarsPadding()
    ) {
      when (currentNavIndex) {
        0 -> HomeScreen(
          tracks = tracks,
          videos = videos,
          artists = artists,
          playlists = playlists,
          userProfile = userProfile,
          announcements = announcements,
          currentPlayingTrackId = currentTrack?.id,
          onTrackSelected = { track, list -> viewModel.playTrackFromList(track, list) },
          onVideoSelected = { vid -> viewModel.playVideo(vid) },
          onShortsReelSelected = { vid -> viewModel.playVideo(vid) },
          onPlaylistSelected = { pl -> viewModel.openPlaylist(pl) },
          onArtistFollowToggle = { artistId -> viewModel.toggleFollowArtist(artistId) },
          onFavoriteToggle = { trackId -> viewModel.toggleTrackFavorite(trackId) },
          onLanguageToggle = { viewModel.toggleLanguage() },
          onOpenSearch = { viewModel.setNavIndex(1) },
          onOpenPremium = { viewModel.setShowPremiumDialog(true) }
        )

        1 -> ExploreScreen(
          tracks = tracks,
          selectedFilter = selectedTrendingFilter,
          favoriteTrackIds = userProfile.favoriteTrackIds,
          currentPlayingTrackId = currentTrack?.id,
          language = language,
          onFilterSelected = { filter -> viewModel.setSelectedTrendingFilter(filter) },
          onTrackSelected = { track, list -> viewModel.playTrackFromList(track, list) },
          onToggleFavorite = { trackId -> viewModel.toggleTrackFavorite(trackId) }
        )

        2 -> VideosScreen(
          videos = videos,
          commentsMap = comments,
          favoriteVideoIds = userProfile.favoriteVideoIds,
          language = language,
          onToggleFavoriteVideo = { videoId -> viewModel.toggleVideoFavorite(videoId) },
          onAddComment = { videoId, text -> viewModel.addComment(videoId, text) },
          onPlayAssociatedTrack = { trackId ->
            val track = tracks.find { it.id == trackId }
            if (track != null) viewModel.playSingleTrack(track)
          },
          initialSelectedVideo = selectedVideoForPlayback
        )

        3 -> LibraryScreen(
          allTracks = tracks,
          allVideos = videos,
          artists = artists,
          userProfile = userProfile,
          language = language,
          onCreatePlaylist = { title, desc -> viewModel.createPlaylist(title, desc) },
          onPlayTrack = { track, list -> viewModel.playTrackFromList(track, list) },
          onPlayVideo = { vid -> viewModel.playVideo(vid) },
          onOpenPlaylist = { pl -> viewModel.openPlaylist(pl) }
        )

        4 -> ProfileAdminScreen(
          userProfile = userProfile,
          analytics = adminAnalytics,
          tracks = tracks,
          videos = videos,
          onSetLanguage = { lang -> viewModel.repository.setLanguage(lang) },
          onSetAudioQuality = { quality -> viewModel.repository.setAudioQuality(quality) },
          onAuthenticate = { name, email, isGoogle -> viewModel.authenticate(name, email, isGoogle) },
          onSwitchRole = { role -> viewModel.switchUserRole(role) },
          onOpenPremium = { viewModel.setShowPremiumDialog(true) },
          onAddTrack = { trk -> viewModel.repository.addTrack(trk) },
          onDeleteTrack = { id -> viewModel.repository.deleteTrack(id) },
          onToggleFeatureTrack = { id -> viewModel.repository.toggleFeatureTrack(id) },
          onAddVideo = { vid -> viewModel.repository.addVideo(vid) },
          onDeleteVideo = { id -> viewModel.repository.deleteVideo(id) },
          onPostAnnouncement = { msg -> viewModel.repository.postAnnouncement(msg) }
        )
      }
    }
  }

  // Full Screen Music Player Sheet Dialog
  if (showFullScreenPlayer && currentTrack != null) {
    FullScreenPlayerSheet(
      track = currentTrack,
      isPlaying = isPlaying,
      progressMs = playbackPositionMs,
      durationMs = durationMs,
      isShuffle = isShuffle,
      repeatMode = repeatMode,
      volume = volume,
      playbackSpeed = playbackSpeed,
      isFavorite = currentTrack?.let { userProfile.favoriteTrackIds.contains(it.id) } ?: false,
      visualizerLevels = visualizerLevels,
      queue = queue,
      currentQueueIndex = currentQueueIndex,
      language = language,
      onDismiss = { viewModel.setShowFullScreenPlayer(false) },
      onTogglePlayPause = { viewModel.playbackManager.togglePlayPause() },
      onSkipNext = { viewModel.playbackManager.skipNext() },
      onSkipPrevious = { viewModel.playbackManager.skipPrevious() },
      onSeekTo = { pos -> viewModel.playbackManager.seekTo(pos) },
      onToggleShuffle = { viewModel.playbackManager.toggleShuffle() },
      onCycleRepeat = { viewModel.playbackManager.cycleRepeatMode() },
      onVolumeChange = { v -> viewModel.playbackManager.setVolume(v) },
      onSpeedChange = { s -> viewModel.playbackManager.setPlaybackSpeed(s) },
      onToggleFavorite = { currentTrack?.let { viewModel.toggleTrackFavorite(it.id) } },
      onSelectQueueTrack = { idx ->
        if (idx in queue.indices) {
          viewModel.playbackManager.playTrack(queue[idx])
        }
      }
    )
  }

  // VIP / Premium Dialog
  if (showPremiumDialog) {
    PremiumDialog(
      isCurrentlyPremium = userProfile.isPremium,
      onDismiss = { viewModel.setShowPremiumDialog(false) },
      onUpgradeConfirmed = { viewModel.upgradeToPremium() }
    )
  }

  // Playlist Detail Dialog
  activePlaylistDetail?.let { pl ->
    PlaylistDetailDialog(
      playlist = pl,
      allTracks = tracks,
      currentTrackId = currentTrack?.id,
      onDismiss = { viewModel.closePlaylistDetail() },
      onPlayTrack = { track, list -> viewModel.playTrackFromList(track, list) }
    )
  }
}
