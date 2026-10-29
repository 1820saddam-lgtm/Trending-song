package com.example.model

/**
 * Domain models for TrendWave Music & Video Streaming App
 */

data class MediaTrack(
  val id: String,
  val title: String,
  val artist: String,
  val album: String = "",
  val durationSeconds: Int,
  val streamUrl: String,
  val coverArtUrl: String,
  val category: String, // "Bengali", "Hindi", "English", "Arabic", "DJ Remix", "Instrumental", "Romantic", "Energetic", "Relaxing"
  val language: String, // "bn", "hi", "en", "ar"
  val playsCount: Long,
  val likesCount: Long,
  val lyrics: List<String> = emptyList(),
  val releaseYear: Int = 2026,
  val isFeatured: Boolean = false,
  val isTrendingBangladesh: Boolean = false,
  val isTrendingWorldwide: Boolean = false,
)

data class MediaVideo(
  val id: String,
  val title: String,
  val creator: String,
  val durationSeconds: Int,
  val videoUrl: String,
  val thumbnailUrl: String,
  val category: String,
  val viewsCount: Long,
  val likesCount: Long,
  val commentsCount: Long,
  val description: String,
  val isShort: Boolean, // true = vertical short, false = 16:9 music video
  val tags: List<String> = emptyList(),
  val associatedTrackId: String? = null,
  val dateAdded: String = "2026-10",
  val isFeatured: Boolean = false,
)

data class VideoComment(
  val id: String,
  val userName: String,
  val userAvatarColor: Long, // Hex color for avatar
  val text: String,
  val timeAgo: String,
  val likes: Int = 0,
)

data class Playlist(
  val id: String,
  val title: String,
  val description: String,
  val coverUrl: String,
  val trackIds: List<String>,
  val isCustom: Boolean = false,
  val createdBy: String = "TrendWave",
)

data class Artist(
  val id: String,
  val name: String,
  val bio: String,
  val imageUrl: String,
  val monthlyListeners: Long,
  val genres: List<String>,
  val isFollowed: Boolean = false,
)

enum class UserRole {
  USER, ADMIN
}

data class UserProfile(
  val id: String = "user_guest_1",
  val name: String = "Wave Explorer",
  val email: String = "explorer@trendwave.io",
  val avatarUrl: String = "",
  val isPremium: Boolean = false,
  val role: UserRole = UserRole.USER,
  val preferredLanguage: String = "en", // "en" or "bn"
  val audioQuality: String = "High (256 kbps)",
  val favoriteTrackIds: Set<String> = emptySet(),
  val favoriteVideoIds: Set<String> = emptySet(),
  val customPlaylists: List<Playlist> = emptyList(),
  val historyTrackIds: List<String> = emptyList(),
  val historyVideoIds: List<String> = emptyList(),
  val followedArtistIds: Set<String> = emptySet(),
)

data class AdminAnalytics(
  val totalPlays: Long = 1845920L,
  val activeUsers: Long = 42890L,
  val totalSongs: Int = 48,
  val totalVideos: Int = 32,
  val reportedContentCount: Int = 3,
  val storageUsedMb: Double = 624.5,
  val monthlyRevenueUsd: Double = 8940.0,
)

enum class TrendingFilter(val labelEn: String, val labelBn: String) {
  TODAY("Today", "আজকের"),
  THIS_WEEK("This Week", "এই সপ্তাহের"),
  MOST_POPULAR("Most Popular", "সবচেয়ে জনপ্রিয়"),
  MOST_VIEWED("Most Viewed", "সর্বাধিক দেখা"),
  NEW_RELEASES("New Releases", "নতুন মুক্তি"),
  BANGLADESH("Bangladesh", "বাংলাদেশ"),
  WORLDWIDE("Worldwide", "বিশ্বব্যাপী")
}

enum class RepeatMode {
  OFF, ALL, ONE
}

enum class VideoQuality(val label: String) {
  AUTO("Auto (1080p)"),
  HD_1080("1080p Full HD"),
  HD_720("720p HD"),
  SD_480("480p SD")
}
