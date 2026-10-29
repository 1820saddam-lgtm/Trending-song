package com.example.ui.components

object LanguageHelper {
  private val en = mapOf(
    "nav_home" to "Home",
    "nav_explore" to "Explore",
    "nav_videos" to "Videos",
    "nav_library" to "Library",
    "nav_profile" to "Profile",
    "search_hint" to "Search songs, videos, artists...",
    "trending_today" to "Trending Songs Today",
    "popular_videos" to "Popular Music Videos",
    "viral_shorts" to "Viral Short Videos",
    "new_releases" to "New Releases",
    "featured_artists" to "Featured Artists",
    "mood_playlists" to "Mood & Genre Playlists",
    "quick_play" to "Quick Play",
    "play_all" to "Play All",
    "shuffle" to "Shuffle",
    "liked_songs" to "Liked Songs",
    "saved_videos" to "Saved Videos",
    "my_playlists" to "My Playlists",
    "history" to "Listening History",
    "go_premium" to "TrendWave VIP",
    "ad_free_message" to "Enjoy ad-free music, offline listening, and Ultra HD 320kbps audio.",
    "upgrade_now" to "Upgrade Now",
    "admin_panel" to "Admin Portal",
    "admin_access" to "Content & Analytics Manager",
    "lyrics" to "Lyrics",
    "up_next" to "Up Next",
    "speed" to "Playback Speed",
    "quality" to "Video Quality",
    "comments" to "Comments",
    "add_comment" to "Add a comment...",
    "post" to "Post",
    "sponsored" to "Sponsored",
    "download_offline" to "Download for Offline",
    "all" to "All",
    "bengali" to "Bengali",
    "hindi" to "Hindi",
    "english" to "English",
    "arabic" to "Arabic",
    "dj_remix" to "DJ Remix",
    "instrumental" to "Lo-Fi / Beats"
  )

  private val bn = mapOf(
    "nav_home" to "হোম",
    "nav_explore" to "এক্সপ্লোর",
    "nav_videos" to "ভিডিও",
    "nav_library" to "লাইব্রেরি",
    "nav_profile" to "প্রোফাইল",
    "search_hint" to "গান, ভিডিও বা শিল্পী খুঁজুন...",
    "trending_today" to "আজকের ট্রেন্ডিং গান",
    "popular_videos" to "জনপ্রিয় মিউজিক ভিডিও",
    "viral_shorts" to "ভাইরাল শর্ট ভিডিও",
    "new_releases" to "নতুন রিলিজ",
    "featured_artists" to "জনপ্রিয় শিল্পী",
    "mood_playlists" to "মুড ও জেনার প্লেলিস্ট",
    "quick_play" to "দ্রুত চালান",
    "play_all" to "সব চালান",
    "shuffle" to "শাফল",
    "liked_songs" to "পছন্দের গান",
    "saved_videos" to "সংরক্ষিত ভিডিও",
    "my_playlists" to "আমার প্লেলিস্ট",
    "history" to "শোনার ইতিহাস",
    "go_premium" to "ট্রেন্ডওয়েভ ভিআইপি",
    "ad_free_message" to "বিজ্ঞাপনহীন গান, অফলাইন ডাউনলোড এবং আল্ট্রা এইচডি ৩২০kbps অডিও উপভোগ করুন।",
    "upgrade_now" to "আপগ্রেড করুন",
    "admin_panel" to "অ্যাডমিন পোর্টাল",
    "admin_access" to "কন্টেন্ট ও অ্যানালিটিক্স",
    "lyrics" to "গানের কথা (লিরিক্স)",
    "up_next" to "পরবর্তী গান",
    "speed" to "প্লেব্যাকের গতি",
    "quality" to "ভিডিও কোয়ালিটি",
    "comments" to "মন্তব্যসমূহ",
    "add_comment" to "একটি মন্তব্য লিখুন...",
    "post" to "পোস্ট করুন",
    "sponsored" to "বিজ্ঞাপন",
    "download_offline" to "অফলাইনে ডাউনলোড করুন",
    "all" to "সব",
    "bengali" to "বাংলা",
    "hindi" to "হিন্দি",
    "english" to "ইংরেজি",
    "arabic" to "আরবি",
    "dj_remix" to "ডিজে রিমিক্স",
    "instrumental" to "লো-ফাই / সুর"
  )

  fun get(key: String, language: String): String {
    return if (language == "bn") {
      bn[key] ?: en[key] ?: key
    } else {
      en[key] ?: key
    }
  }
}
