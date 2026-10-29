package com.example.data

import com.example.model.AdminAnalytics
import com.example.model.Artist
import com.example.model.MediaTrack
import com.example.model.MediaVideo
import com.example.model.Playlist
import com.example.model.TrendingFilter
import com.example.model.UserProfile
import com.example.model.UserRole
import com.example.model.VideoComment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class TrendWaveRepository {

  // Catalog State
  private val _tracks = MutableStateFlow<List<MediaTrack>>(createInitialTracks())
  val tracks: StateFlow<List<MediaTrack>> = _tracks.asStateFlow()

  private val _videos = MutableStateFlow<List<MediaVideo>>(createInitialVideos())
  val videos: StateFlow<List<MediaVideo>> = _videos.asStateFlow()

  private val _artists = MutableStateFlow<List<Artist>>(createInitialArtists())
  val artists: StateFlow<List<Artist>> = _artists.asStateFlow()

  private val _playlists = MutableStateFlow<List<Playlist>>(createInitialPlaylists())
  val playlists: StateFlow<List<Playlist>> = _playlists.asStateFlow()

  private val _comments = MutableStateFlow<Map<String, List<VideoComment>>>(createInitialComments())
  val comments: StateFlow<Map<String, List<VideoComment>>> = _comments.asStateFlow()

  private val _userProfile = MutableStateFlow(
    UserProfile(
      id = "user_guest_1",
      name = "Guest Listener",
      email = "guest@trendwave.io",
      avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&q=80",
      isPremium = false,
      role = UserRole.USER,
      preferredLanguage = "en",
      favoriteTrackIds = setOf("trk_bn_1", "trk_en_1", "trk_hi_1"),
      favoriteVideoIds = setOf("vid_sh_1", "vid_mv_1"),
      historyTrackIds = listOf("trk_bn_1", "trk_en_2", "trk_hi_2"),
      followedArtistIds = setOf("art_tahsan", "art_arijit")
    )
  )
  val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

  private val _adminAnalytics = MutableStateFlow(AdminAnalytics())
  val adminAnalytics: StateFlow<AdminAnalytics> = _adminAnalytics.asStateFlow()

  private val _announcements = MutableStateFlow(
    listOf(
      "🎉 Welcome to TrendWave 2026! Discover trending Bengali, Hindi, English and Arabic hits with ultra HD playback.",
      "🚀 Try our new Viral Short Videos feed! Swipe up for nonstop viral musical hooks."
    )
  )
  val announcements: StateFlow<List<String>> = _announcements.asStateFlow()

  // Track & Media Operations
  fun toggleFavoriteTrack(trackId: String) {
    val current = _userProfile.value
    val newFavs = if (current.favoriteTrackIds.contains(trackId)) {
      current.favoriteTrackIds - trackId
    } else {
      current.favoriteTrackIds + trackId
    }
    _userProfile.value = current.copy(favoriteTrackIds = newFavs)
  }

  fun toggleFavoriteVideo(videoId: String) {
    val current = _userProfile.value
    val newFavs = if (current.favoriteVideoIds.contains(videoId)) {
      current.favoriteVideoIds - videoId
    } else {
      current.favoriteVideoIds + videoId
    }
    _userProfile.value = current.copy(favoriteVideoIds = newFavs)

    // Update video like count
    _videos.value = _videos.value.map { vid ->
      if (vid.id == videoId) {
        val diff = if (newFavs.contains(videoId)) 1 else -1
        vid.copy(likesCount = (vid.likesCount + diff).coerceAtLeast(0))
      } else vid
    }
  }

  fun recordTrackPlayed(trackId: String) {
    val current = _userProfile.value
    val updatedHistory = listOf(trackId) + current.historyTrackIds.filter { it != trackId }
    _userProfile.value = current.copy(historyTrackIds = updatedHistory.take(40))

    // Increment play count
    _tracks.value = _tracks.value.map { trk ->
      if (trk.id == trackId) trk.copy(playsCount = trk.playsCount + 1) else trk
    }

    _adminAnalytics.value = _adminAnalytics.value.copy(
      totalPlays = _adminAnalytics.value.totalPlays + 1
    )
  }

  fun recordVideoWatched(videoId: String) {
    val current = _userProfile.value
    val updatedHistory = listOf(videoId) + current.historyVideoIds.filter { it != videoId }
    _userProfile.value = current.copy(historyVideoIds = updatedHistory.take(40))

    _videos.value = _videos.value.map { vid ->
      if (vid.id == videoId) vid.copy(viewsCount = vid.viewsCount + 1) else vid
    }
  }

  fun toggleFollowArtist(artistId: String) {
    val current = _userProfile.value
    val newFollowed = if (current.followedArtistIds.contains(artistId)) {
      current.followedArtistIds - artistId
    } else {
      current.followedArtistIds + artistId
    }
    _userProfile.value = current.copy(followedArtistIds = newFollowed)

    _artists.value = _artists.value.map { artist ->
      if (artist.id == artistId) artist.copy(isFollowed = newFollowed.contains(artistId)) else artist
    }
  }

  fun createCustomPlaylist(title: String, description: String): Playlist {
    val newPlaylist = Playlist(
      id = "pl_user_${UUID.randomUUID().toString().take(8)}",
      title = title,
      description = description,
      coverUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=400&q=80",
      trackIds = emptyList(),
      isCustom = true,
      createdBy = _userProfile.value.name
    )
    _playlists.value = _playlists.value + newPlaylist
    val current = _userProfile.value
    _userProfile.value = current.copy(customPlaylists = current.customPlaylists + newPlaylist)
    return newPlaylist
  }

  fun addTrackToPlaylist(playlistId: String, trackId: String) {
    _playlists.value = _playlists.value.map { pl ->
      if (pl.id == playlistId && !pl.trackIds.contains(trackId)) {
        pl.copy(trackIds = pl.trackIds + trackId)
      } else pl
    }
  }

  fun addComment(videoId: String, text: String, userName: String) {
    val currentComments = _comments.value[videoId] ?: emptyList()
    val newComment = VideoComment(
      id = "cm_${UUID.randomUUID().toString().take(8)}",
      userName = userName.ifBlank { _userProfile.value.name },
      userAvatarColor = 0xFF8B5CF6,
      text = text,
      timeAgo = "Just now",
      likes = 0
    )
    _comments.value = _comments.value + (videoId to (listOf(newComment) + currentComments))

    // Update video comments count
    _videos.value = _videos.value.map { vid ->
      if (vid.id == videoId) vid.copy(commentsCount = vid.commentsCount + 1) else vid
    }
  }

  fun deleteComment(videoId: String, commentId: String) {
    val currentList = _comments.value[videoId] ?: return
    _comments.value = _comments.value + (videoId to currentList.filter { it.id != commentId })
  }

  // Filter & Search
  fun getTracksByFilter(filter: TrendingFilter): List<MediaTrack> {
    val all = _tracks.value
    return when (filter) {
      TrendingFilter.TODAY -> all.sortedByDescending { it.playsCount }
      TrendingFilter.THIS_WEEK -> all.sortedByDescending { it.likesCount }
      TrendingFilter.MOST_POPULAR -> all.sortedByDescending { it.playsCount }
      TrendingFilter.MOST_VIEWED -> all.sortedByDescending { it.playsCount + it.likesCount }
      TrendingFilter.NEW_RELEASES -> all.filter { it.releaseYear >= 2025 }
      TrendingFilter.BANGLADESH -> all.filter { it.isTrendingBangladesh || it.language == "bn" }
      TrendingFilter.WORLDWIDE -> all.filter { it.isTrendingWorldwide || it.language == "en" }
    }
  }

  // User Profile & Settings
  fun setLanguage(lang: String) {
    _userProfile.value = _userProfile.value.copy(preferredLanguage = lang)
  }

  fun setAudioQuality(quality: String) {
    _userProfile.value = _userProfile.value.copy(audioQuality = quality)
  }

  fun setPremiumStatus(isPremium: Boolean) {
    _userProfile.value = _userProfile.value.copy(isPremium = isPremium)
  }

  fun authenticateUser(name: String, email: String, isGoogle: Boolean = false) {
    val isAdmin = email.equals("admin@trendwave.com", ignoreCase = true)
    _userProfile.value = _userProfile.value.copy(
      name = name,
      email = email,
      role = if (isAdmin) UserRole.ADMIN else UserRole.USER,
      avatarUrl = if (isGoogle) {
        "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=200&q=80"
      } else {
        "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=200&q=80"
      }
    )
  }

  fun switchUserRole(role: UserRole) {
    _userProfile.value = _userProfile.value.copy(role = role)
  }

  // Admin Dashboard Actions
  fun addTrack(track: MediaTrack) {
    _tracks.value = listOf(track) + _tracks.value
    _adminAnalytics.value = _adminAnalytics.value.copy(
      totalSongs = _tracks.value.size
    )
  }

  fun deleteTrack(trackId: String) {
    _tracks.value = _tracks.value.filter { it.id != trackId }
    _adminAnalytics.value = _adminAnalytics.value.copy(
      totalSongs = _tracks.value.size
    )
  }

  fun toggleFeatureTrack(trackId: String) {
    _tracks.value = _tracks.value.map {
      if (it.id == trackId) it.copy(isFeatured = !it.isFeatured) else it
    }
  }

  fun addVideo(video: MediaVideo) {
    _videos.value = listOf(video) + _videos.value
    _adminAnalytics.value = _adminAnalytics.value.copy(
      totalVideos = _videos.value.size
    )
  }

  fun deleteVideo(videoId: String) {
    _videos.value = _videos.value.filter { it.id != videoId }
    _adminAnalytics.value = _adminAnalytics.value.copy(
      totalVideos = _videos.value.size
    )
  }

  fun postAnnouncement(text: String) {
    _announcements.value = listOf(text) + _announcements.value
  }

  // Seed Data Initializers
  private fun createInitialTracks(): List<MediaTrack> {
    return listOf(
      // Bengali Trending
      MediaTrack(
        id = "trk_bn_1",
        title = "Tumi Ashbe Boley",
        artist = "Tahsan Khan",
        album = "Prematal",
        durationSeconds = 214,
        streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        coverArtUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=400&q=80",
        category = "Bengali",
        language = "bn",
        playsCount = 4820000L,
        likesCount = 312000L,
        lyrics = listOf(
          "তুমি আসবে বলেই আকাশ মেঘলা",
          "বৃষ্টি এখনও হয়নি",
          "তুমি আসবে বলেই ভালোবাসার গান",
          "কখনো পুরনো হয়নি..."
        ),
        releaseYear = 2025,
        isFeatured = true,
        isTrendingBangladesh = true,
      ),
      MediaTrack(
        id = "trk_bn_2",
        title = "Deora (Folk Fusion Remix)",
        artist = "Pritom Hasan & Islam Uddin",
        album = "Coke Studio Bangla",
        durationSeconds = 246,
        streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        coverArtUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=400&q=80",
        category = "Bengali",
        language = "bn",
        playsCount = 7610000L,
        likesCount = 590000L,
        lyrics = listOf(
          "ও দেওরা রে ভাই...",
          "মাঝি নাও ছাড়িয়া দে",
          "পাল তুলিয়া দে রে দেওরা",
          "উজান গাঙ্গে ভাসাই নাও..."
        ),
        releaseYear = 2026,
        isFeatured = true,
        isTrendingBangladesh = true,
      ),
      MediaTrack(
        id = "trk_bn_3",
        title = "E Hawa",
        artist = "Meghdol",
        album = "Hawa Film OST",
        durationSeconds = 198,
        streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
        coverArtUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=400&q=80",
        category = "Bengali",
        language = "bn",
        playsCount = 3450000L,
        likesCount = 280000L,
        lyrics = listOf(
          "এ হাওয়া মেঘের দেশে নিয়ে চলো আমায়",
          "সমুদ্রের গান বাজে বুকের ভেতর..."
        ),
        releaseYear = 2025,
        isTrendingBangladesh = true,
      ),
      MediaTrack(
        id = "trk_bn_4",
        title = "Majhe Majhe Tobo Dekha Pai",
        artist = "Arijit Singh",
        album = "Rabindra Smriti",
        durationSeconds = 230,
        streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
        coverArtUrl = "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?auto=format&fit=crop&w=400&q=80",
        category = "Bengali",
        language = "bn",
        playsCount = 5200000L,
        likesCount = 490000L,
        lyrics = listOf(
          "মাঝে মাঝে তব দেখা পাই,",
          "চিরদিন কেন পাই না?",
          "কেন মেঘ আসে হৃদয়-আকাশে,",
          "তোমারে দেখিতে দেয় না..."
        ),
        releaseYear = 2026,
        isTrendingBangladesh = true,
      ),

      // Hindi Hits
      MediaTrack(
        id = "trk_hi_1",
        title = "Apna Bana Le",
        artist = "Arijit Singh & Sachin-Jigar",
        album = "Bhediya",
        durationSeconds = 261,
        streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3",
        coverArtUrl = "https://images.unsplash.com/photo-1459749411175-04bf5292ceea?auto=format&fit=crop&w=400&q=80",
        category = "Hindi",
        language = "hi",
        playsCount = 9200000L,
        likesCount = 890000L,
        lyrics = listOf(
          "तू अपना बना ले पिया...",
          "सब कुछ छोड़ के आऊं",
          "दिल की ये गुज़ारिश है",
          "तेरी बाहों में सो जाऊं..."
        ),
        releaseYear = 2025,
        isFeatured = true,
        isTrendingWorldwide = true,
      ),
      MediaTrack(
        id = "trk_hi_2",
        title = "Chaleya (Romantic Vibe)",
        artist = "Arijit Singh & Shilpa Rao",
        album = "Jawan",
        durationSeconds = 200,
        streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3",
        coverArtUrl = "https://images.unsplash.com/photo-1518609878373-06d740f60d8b?auto=format&fit=crop&w=400&q=80",
        category = "Hindi",
        language = "hi",
        playsCount = 8400000L,
        likesCount = 740000L,
        lyrics = listOf(
          "इश्क़ में दिल बना है आशिक तेरा...",
          "चलिया तेरी ओर चलेया...",
          "तेरे पीछे पीछे मन भागे रे..."
        ),
        releaseYear = 2025,
        isTrendingWorldwide = true,
      ),
      MediaTrack(
        id = "trk_hi_3",
        title = "Excuses (Punjab Beat)",
        artist = "AP Dhillon & Gurinder Gill",
        album = "Hidden Gems",
        durationSeconds = 176,
        streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-7.mp3",
        coverArtUrl = "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?auto=format&fit=crop&w=400&q=80",
        category = "Hindi",
        language = "hi",
        playsCount = 11200000L,
        likesCount = 950000L,
        lyrics = listOf(
          "Kehndi hundi si chan tak raah bana de...",
          "Tareyan de vich jab tu manu vekhegi..."
        ),
        releaseYear = 2025,
        isTrendingWorldwide = true,
      ),

      // English Global Hits
      MediaTrack(
        id = "trk_en_1",
        title = "Blinding Lights",
        artist = "The Weeknd",
        album = "After Hours",
        durationSeconds = 200,
        streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3",
        coverArtUrl = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?auto=format&fit=crop&w=400&q=80",
        category = "English",
        language = "en",
        playsCount = 18500000L,
        likesCount = 1450000L,
        lyrics = listOf(
          "I said, ooh, I'm blinded by the lights",
          "No, I can't sleep until I feel your touch",
          "I said, ooh, I'm drowning in the night",
          "Oh, when I'm like this, you're the one I trust..."
        ),
        releaseYear = 2025,
        isFeatured = true,
        isTrendingWorldwide = true,
      ),
      MediaTrack(
        id = "trk_en_2",
        title = "Cruel Summer",
        artist = "Taylor Swift",
        album = "Lover",
        durationSeconds = 178,
        streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-9.mp3",
        coverArtUrl = "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?auto=format&fit=crop&w=400&q=80",
        category = "English",
        language = "en",
        playsCount = 14200000L,
        likesCount = 1180000L,
        lyrics = listOf(
          "Fever dream high in the quiet of the night",
          "You know that I caught it...",
          "And it's new, the shape of your body",
          "It's blue, the feeling I've got..."
        ),
        releaseYear = 2025,
        isTrendingWorldwide = true,
      ),
      MediaTrack(
        id = "trk_en_3",
        title = "Levitating (Club Mix)",
        artist = "Dua Lipa",
        album = "Future Nostalgia",
        durationSeconds = 203,
        streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-10.mp3",
        coverArtUrl = "https://images.unsplash.com/photo-1501386761578-eac5c94b800a?auto=format&fit=crop&w=400&q=80",
        category = "English",
        language = "en",
        playsCount = 12900000L,
        likesCount = 980000L,
        lyrics = listOf(
          "If you wanna run away with me, I know a galaxy",
          "And I can take you for a ride",
          "I had a premonition that we fell into a rhythm..."
        ),
        releaseYear = 2026,
        isTrendingWorldwide = true,
      ),

      // Arabic & Islamic / Spiritual
      MediaTrack(
        id = "trk_ar_1",
        title = "Kun Anta (Be Yourself)",
        artist = "Humood AlKhudher",
        album = "Aseer Ahsan",
        durationSeconds = 237,
        streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-11.mp3",
        coverArtUrl = "https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=400&q=80",
        category = "Arabic",
        language = "ar",
        playsCount = 6800000L,
        likesCount = 610000L,
        lyrics = listOf(
          "لأجاريهم، قلدت ظاهر ما فيهم",
          "فبدوتُ شخصاً آخراً، كي أتفاخر",
          "كن أنت تزدد جمالاً..."
        ),
        releaseYear = 2025,
        isFeatured = true,
        isTrendingWorldwide = true,
      ),
      MediaTrack(
        id = "trk_ar_2",
        title = "Rahmatun Lil'Alameen",
        artist = "Maher Zain",
        album = "Baraka",
        durationSeconds = 250,
        streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-12.mp3",
        coverArtUrl = "https://images.unsplash.com/photo-1564769625905-50e93615e769?auto=format&fit=crop&w=400&q=80",
        category = "Arabic",
        language = "ar",
        playsCount = 9500000L,
        likesCount = 890000L,
        lyrics = listOf(
          "يا رحمةً للعالمين",
          "يا خاتم المرسلين",
          "صلاتنا وسلامنا عليك يا حبيب الله..."
        ),
        releaseYear = 2026,
        isTrendingWorldwide = true,
      ),

      // DJ Remix & Lo-Fi / Instrumental
      MediaTrack(
        id = "trk_dj_1",
        title = "Midnight Neon Cyber Drop",
        artist = "DJ Wave",
        album = "Future Bass 2026",
        durationSeconds = 188,
        streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-13.mp3",
        coverArtUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=400&q=80",
        category = "DJ Remix",
        language = "en",
        playsCount = 4100000L,
        likesCount = 370000L,
        lyrics = listOf(
          "[Heavy Synth Intro]",
          "[Bass Drop 140 BPM]",
          "[Laser Wave Melodic Echo]"
        ),
        releaseYear = 2026,
        isFeatured = true,
      ),
      MediaTrack(
        id = "trk_in_1",
        title = "Tokyo Rain Lo-Fi Sunset",
        artist = "ChillWave Collective",
        album = "Midnight Coffee",
        durationSeconds = 165,
        streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-14.mp3",
        coverArtUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=400&q=80",
        category = "Instrumental",
        language = "en",
        playsCount = 3200000L,
        likesCount = 290000L,
        lyrics = listOf(
          "[Gentle Vinyl Crackle]",
          "[Warm Rhodes Piano Chords]",
          "[Raindrops on Windowpane]"
        ),
        releaseYear = 2026,
      )
    )
  }

  private fun createInitialVideos(): List<MediaVideo> {
    return listOf(
      // Viral Short Videos (Vertical 9:16)
      MediaVideo(
        id = "vid_sh_1",
        title = "Insane 60s Bengali Dotara & Electric Bass Fusion Solo! 🔥",
        creator = "@dhakafusion",
        durationSeconds = 45,
        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
        thumbnailUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=400&q=80",
        category = "Bengali",
        viewsCount = 1420000L,
        likesCount = 115000L,
        commentsCount = 1420L,
        description = "Live jam session at Dhanmondi Lake studio! Dotara meets cyber bass. Drop your thoughts below!",
        isShort = true,
        tags = listOf("#BengaliFusion", "#DotaraSolo", "#TrendWaveLive"),
        associatedTrackId = "trk_bn_2",
        isFeatured = true
      ),
      MediaVideo(
        id = "vid_sh_2",
        title = "Piano Tutorial: Learn the hook of 'Apna Bana Le' in 30 Seconds 🎹",
        creator = "@keysandbeats",
        durationSeconds = 38,
        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
        thumbnailUrl = "https://images.unsplash.com/photo-1520523839898-507127042a22?auto=format&fit=crop&w=400&q=80",
        category = "Hindi",
        viewsCount = 980000L,
        likesCount = 89000L,
        commentsCount = 850L,
        description = "Simple chords in D major. Save this reel to practice tonight!",
        isShort = true,
        tags = listOf("#PianoTutorial", "#ArijitSingh", "#BollywoodChords"),
        associatedTrackId = "trk_hi_1"
      ),
      MediaVideo(
        id = "vid_sh_3",
        title = "Crowd Goes Wild: Bass Drop at Tomorrowland Asia 2026! 🎧⚡",
        creator = "@djwave_official",
        durationSeconds = 52,
        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyblazes.mp4",
        thumbnailUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=400&q=80",
        category = "DJ Remix",
        viewsCount = 2890000L,
        likesCount = 310000L,
        commentsCount = 3200L,
        description = "40,000 people jumping together in sync! Pure adrenaline energy.",
        isShort = true,
        tags = listOf("#Tomorrowland", "#DJRemix", "#FestivalEnergy"),
        associatedTrackId = "trk_dj_1",
        isFeatured = true
      ),
      MediaVideo(
        id = "vid_sh_4",
        title = "Beautiful Acoustic Oud Instrumental - Sunset Cairo Echoes 🌅",
        creator = "@oudmastery",
        durationSeconds = 58,
        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4",
        thumbnailUrl = "https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=400&q=80",
        category = "Arabic",
        viewsCount = 760000L,
        likesCount = 68000L,
        commentsCount = 610L,
        description = "Peaceful evening vibes from Old Cairo with traditional handcrafted Oud.",
        isShort = true,
        tags = listOf("#OudInstrumental", "#ArabicMusic", "#ChillVibes"),
        associatedTrackId = "trk_ar_1"
      ),

      // Popular Music Videos (Widescreen 16:9)
      MediaVideo(
        id = "vid_mv_1",
        title = "TrendWave Anthem 2026: Global Neon Symphony (Official 4K Video)",
        creator = "TrendWave Originals",
        durationSeconds = 245,
        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
        thumbnailUrl = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?auto=format&fit=crop&w=600&q=80",
        category = "English",
        viewsCount = 5400000L,
        likesCount = 460000L,
        commentsCount = 5120L,
        description = "Shot across Dhaka, Mumbai, London, and Tokyo. The visual representation of cross-cultural sound waves.",
        isShort = false,
        tags = listOf("#OfficialMusicVideo", "#4K", "#TrendWaveAnthem"),
        isFeatured = true
      ),
      MediaVideo(
        id = "vid_mv_2",
        title = "Tahsan - 'Tumi Ashbe Boley' (Official Live Acoustic Concert in Dhaka)",
        creator = "Tahsan Official",
        durationSeconds = 280,
        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
        thumbnailUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=600&q=80",
        category = "Bengali",
        viewsCount = 3800000L,
        likesCount = 390000L,
        commentsCount = 3400L,
        description = "Live at Army Stadium, Dhaka. An unforgettable night of soulful Bengali music with full orchestra.",
        isShort = false,
        tags = listOf("#BengaliLive", "#TahsanLive", "#DhakaConcert"),
        associatedTrackId = "trk_bn_1"
      ),
      MediaVideo(
        id = "vid_mv_3",
        title = "Arijit Singh - 'Kesariya & Apna Bana Le' Medley Live in London",
        creator = "Sony Music India",
        durationSeconds = 310,
        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
        thumbnailUrl = "https://images.unsplash.com/photo-1459749411175-04bf5292ceea?auto=format&fit=crop&w=600&q=80",
        category = "Hindi",
        viewsCount = 7200000L,
        likesCount = 680000L,
        commentsCount = 7800L,
        description = "A breathtaking soulful rendition by the king of melody at the O2 Arena.",
        isShort = false,
        tags = listOf("#ArijitSinghLive", "#BollywoodConcert", "#O2Arena"),
        associatedTrackId = "trk_hi_1"
      )
    )
  }

  private fun createInitialArtists(): List<Artist> {
    return listOf(
      Artist(
        id = "art_tahsan",
        name = "Tahsan Khan",
        bio = "Celebrated Bangladeshi singer-songwriter, composer, and actor known for his soulful romantic ballads.",
        imageUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=300&q=80",
        monthlyListeners = 2400000L,
        genres = listOf("Bengali Pop", "Acoustic", "Romantic"),
        isFollowed = true
      ),
      Artist(
        id = "art_arijit",
        name = "Arijit Singh",
        bio = "Globally renowned playback vocalist and musician touching billions with heart-rending melodies.",
        imageUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=300&q=80",
        monthlyListeners = 38500000L,
        genres = listOf("Bollywood", "Bengali Classical", "Romantic Pop"),
        isFollowed = true
      ),
      Artist(
        id = "art_weeknd",
        name = "The Weeknd",
        bio = "Canadian megastar redefining synth-pop, R&B, and futuristic stadium soundscapes.",
        imageUrl = "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?auto=format&fit=crop&w=300&q=80",
        monthlyListeners = 98000000L,
        genres = listOf("Synthwave", "R&B", "Global Pop")
      ),
      Artist(
        id = "art_maher",
        name = "Maher Zain",
        bio = "Swedish-Lebanese singer, songwriter, and music producer known for uplifting spiritual music.",
        imageUrl = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&w=300&q=80",
        monthlyListeners = 6100000L,
        genres = listOf("Nasheed", "Acoustic Pop", "Arabic")
      ),
      Artist(
        id = "art_pritom",
        name = "Pritom Hasan",
        bio = "Trailblazing Bangladeshi music producer known for vibrant folk-electronic fusion and chartbusters.",
        imageUrl = "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?auto=format&fit=crop&w=300&q=80",
        monthlyListeners = 3200000L,
        genres = listOf("Coke Studio Bangla", "Folk Fusion", "Electronic")
      )
    )
  }

  private fun createInitialPlaylists(): List<Playlist> {
    return listOf(
      Playlist(
        id = "pl_bengali_top",
        title = "Bengali Golden Wave 🇧🇩",
        description = "The finest blend of modern Bangla pop, Coke Studio fusions, and evergreen melodies.",
        coverUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=400&q=80",
        trackIds = listOf("trk_bn_1", "trk_bn_2", "trk_bn_3", "trk_bn_4"),
        createdBy = "TrendWave Editorial"
      ),
      Playlist(
        id = "pl_bollywood_fire",
        title = "Bollywood High Voltage 🔥",
        description = "Top trending Hindi chartbusters, romantic favorites, and dance floor anthems.",
        coverUrl = "https://images.unsplash.com/photo-1459749411175-04bf5292ceea?auto=format&fit=crop&w=400&q=80",
        trackIds = listOf("trk_hi_1", "trk_hi_2", "trk_hi_3"),
        createdBy = "TrendWave Editorial"
      ),
      Playlist(
        id = "pl_global_pop",
        title = "Global Pop Universe 🌐",
        description = "Worldwide #1 hits featuring The Weeknd, Taylor Swift, Dua Lipa, and viral sounds.",
        coverUrl = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?auto=format&fit=crop&w=400&q=80",
        trackIds = listOf("trk_en_1", "trk_en_2", "trk_en_3"),
        createdBy = "TrendWave Editorial"
      ),
      Playlist(
        id = "pl_relaxing_lofi",
        title = "Midnight Lo-Fi & Rain ☕",
        description = "Soothing acoustic chords, relaxing synth melodies, and ambient study beats.",
        coverUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=400&q=80",
        trackIds = listOf("trk_in_1", "trk_bn_3", "trk_ar_1"),
        createdBy = "TrendWave Editorial"
      ),
      Playlist(
        id = "pl_dj_remix",
        title = "Cyber Club DJ Drop ⚡",
        description = "High energy EDM, pumping basslines, and festival remixes.",
        coverUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=400&q=80",
        trackIds = listOf("trk_dj_1", "trk_bn_2", "trk_en_3"),
        createdBy = "TrendWave Editorial"
      )
    )
  }

  private fun createInitialComments(): Map<String, List<VideoComment>> {
    return mapOf(
      "vid_sh_1" to listOf(
        VideoComment("c1", "Tanvir Ahmed", 0xFF06B6D4, "The drop with the Dotara is unmatched! Pure magic 🇧🇩🔥", "2 hours ago", 48),
        VideoComment("c2", "Subrata Roy", 0xFF8B5CF6, "Need this full track on TrendWave now!", "5 hours ago", 24),
        VideoComment("c3", "Nabila Rahman", 0xFFEC4899, "Bengali instruments sound unbelievable in modern electronic music.", "1 day ago", 82)
      ),
      "vid_sh_2" to listOf(
        VideoComment("c4", "Pooja Sharma", 0xFF10B981, "Finally a tutorial that explains the chord inversion properly. Thank you!", "3 hours ago", 19),
        VideoComment("c5", "Rohan Mehta", 0xFFF59E0B, "Arijit's melody is forever classic.", "6 hours ago", 33)
      ),
      "vid_mv_1" to listOf(
        VideoComment("c6", "Alex Rivera", 0xFF8B5CF6, "The cinematography in 4K is breathtaking! Incredible sound production.", "1 day ago", 152),
        VideoComment("c7", "Farhan Kabir", 0xFF06B6D4, "Proud to see TrendWave bringing artists together across borders.", "2 days ago", 89)
      )
    )
  }
}
