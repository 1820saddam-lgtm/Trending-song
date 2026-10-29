# TrendWave — Trending Music & Video Streaming App

TrendWave is a complete, modern Android music and video streaming application built with Kotlin and Jetpack Compose. It features trending songs, viral short videos (9:16 vertical reels), widescreen music videos, multi-region music discovery (Bengali, Hindi, English, Arabic/Islamic, DJ Remixes, Instrumental), resilient real-time audio playback, interactive queues, synchronized lyrics, and a secure administration portal.

---

## 🌟 Key Highlights & Features

### 1. Home Screen & Discovery
- **Hero Spotlight Release**: Highlighting featured song releases with instant play capability.
- **Trending Songs of the Day**: Real ranked tracks with play counts, category pills, and quick favorites.
- **Viral Short Videos**: Horizontal preview reel connecting seamlessly to the vertical full-screen swipeable shorts player.
- **Popular Music Videos**: 16:9 widescreen video feed with category filters, view counts, and video modal viewer.
- **Regional & Genre Categorization**:
  - 🇧🇩 **Bengali**: Modern Bangla pop (Tahsan), folk fusions (Pritom Hasan / Coke Studio Bangla), film anthems (Meghdol), and Rabindra Sangeet fusions (Arijit Singh).
  - 🇮🇳 **Hindi**: Bollywood chartbusters, romantic favorites, and Punjabi hip-hop (Arijit Singh, Shilpa Rao, AP Dhillon).
  - 🌐 **English**: Global pop and synthwave megahits (The Weeknd, Taylor Swift, Dua Lipa).
  - 🌙 **Arabic & Islamic**: Uplifting spiritual anthems (Maher Zain, Humood AlKhudher) and traditional Oud instrumentals.
  - ⚡ **DJ Remix & Lo-Fi**: Pumping bass drops, midnight cyber mixes, and relaxing vinyl study beats.
- **Featured Artists**: Follow/Unfollow artist profiles (Tahsan, Arijit Singh, The Weeknd, Maher Zain, Pritom Hasan).
- **Mood Playlists**: Bengali Golden Wave, Bollywood Fire, Global Pop Universe, Midnight Lo-Fi & Rain, Cyber Club DJ Drop.

### 2. Music Player & Resilient Playback
- **Floating Mini-Player**: Fixed above bottom navigation with rotating artwork, animated neon wave visualizer, song title/artist, favorite toggle, play/pause, next track, and bottom seek progress line.
- **Full-Screen Player**:
  - Vinyl record simulation with smooth 360° rotation animation and glowing border.
  - Real-time animated audio visualizer reacting to track playback.
  - Interactive seek slider with scrub time formatting (`mm:ss`).
  - Shuffle mode and 3-stage Repeat modes (Off, Repeat All, Repeat One).
  - Volume slider and playback speed selector (0.75x, 1.0x, 1.25x, 1.5x).
  - Synchronized Lyrics tab.
  - "Up Next" Queue tab with live index indicator and direct track hopping.
- **Dual Playback Architecture**:
  - Remote streaming with Android `MediaPlayer` for online MP3s.
  - Integrated harmonic tone synthesizer using Android `AudioTrack` producing pentatonic musical chords so music reliably plays even when offline or behind firewalls.

### 3. Video Streaming & Viral Shorts
- **Vertical Short-Video Feed (9:16)**:
  - VideoView surface with adaptive playback.
  - Right action rail: Like button with live count, interactive Comments sheet, Bookmark/Save, and rotating audio disc.
  - Bottom overlay: Creator handle, description, hashtag chips, original audio track link, quality selector (Auto, 1080p, 720p, 480p), and subtitles/captions toggle.
  - Comment Drawer: Real-time user comments with timestamp, avatar, and ability to type and post new comments.
- **Widescreen Music Videos (16:9)**:
  - 4K/HD video presentation with full playback controls, view counters, and related information.

### 4. Personal Library
- **Liked Songs**: Full collection with "Play All" and "Shuffle" actions.
- **Custom Playlists**: "+ New Playlist" dialog allowing users to create custom playlists with title and description.
- **Saved Videos**: Quick access to bookmarked shorts and music videos.
- **Listening & Watch History**: Automated chronological history tracking.

### 5. Profile & Admin Portal
- **User Profile**: Guest browsing supported out of the box with optional Google Sign-in and custom account authentication.
- **Bilingual Interface**: One-tap toggle between English and Bengali (বাংলা) for navigation, categories, and labels.
- **Audio Quality Settings**: Normal (128 kbps), High (256 kbps), and Ultra HD (320 kbps).
- **TrendWave VIP / Monetization**:
  - VIP modal with plan selector (Individual $3.99/mo, Family $6.99/mo).
  - Removes sponsored banners and enables Ultra HD master quality.
  - Native sponsored banner on Home Screen with upgrade trigger.
- **Administrator Dashboard**:
  - Secure role-based access (admin@trendwave.com / admin123).
  - Real-time metrics: Total Plays, Active Users, Catalog Counts, Revenue.
  - Content Management: Add Song dialog, Add Video dialog, Feature/Unfeature toggle, and Delete actions.
  - In-app Notification Broadcaster: Push announcements to all listeners.

---

## 🛠️ Architecture & Tech Stack

- **Framework**: Android Native with 100% Kotlin
- **UI Engine**: Jetpack Compose (Material 3)
- **Design Aesthetic**: Premium Dark Theme (`#090A10`, `#0E111B`, `#161A29`), vibrant Neon Violet (`#8B5CF6`) and Electric Cyan (`#06B6D4`) gradients.
- **Typography**: Local Google Fonts (`Outfit` display font and `Plus Jakarta Sans` body font) via `font-util`.
- **State Management**: MVVM Architecture with `ViewModel` and Kotlin Coroutines / `StateFlow`.
- **Image Loading**: Coil Compose (`io.coil-kt:coil-compose`).
- **Media Engine**: Android `MediaPlayer` + Android `AudioTrack` tone synthesis + `VideoView`.
- **Adaptive Icons**: Material You adaptive icon generated with custom neon wave branding.

---

## 🚀 Building & Running

### Gradle Commands
- **Assemble Debug APK**:
  ```bash
  gradle :app:assembleDebug
  ```
- **Run Unit & Robolectric Tests**:
  ```bash
  gradle :app:testDebugUnitTest
  ```

---

## 📦 Google Play Store Preparation Checklist

- [x] **Unique Application ID**: Configured as `com.aistudio.trendwave.kpxmqz` in `app/build.gradle.kts`.
- [x] **App Name**: Set to "TrendWave" in `strings.xml` and `metadata.json`.
- [x] **Custom Adaptive Icon**: High-resolution icon configured across all density folders (`mdpi`, `hdpi`, `xhdpi`, `xxhdpi`, `xxxhdpi`) with circular round icon variant and zero default template leftovers.
- [x] **Permissions**: Minimal required permissions declared in `AndroidManifest.xml` (`INTERNET`, `ACCESS_NETWORK_STATE`, `WAKE_LOCK`).
- [x] **Target SDK**: Configured for modern Android standards (`targetSdk = 36`, `compileSdk = 36`).
- [x] **Edge-to-Edge**: `enableEdgeToEdge()` implemented with safe drawing and navigation bar insets.
- [x] **Content Rights Compliance**: Public domain, royalty-free audio streams and licensed sample videos.
