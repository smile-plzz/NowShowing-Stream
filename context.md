# Project Context: NowShowing TV

## 1. Project Goals
- Develop a standalone, TV-native version of NowShowing for Android TV / Google TV.
- Deliver an installable, native Android APK (.apk).
- Provide a cinematic, remote-controlled 10-foot movie and television discovery and viewing experience.
- Maintain independent installation while allowing discovery by Android TV launchers (including Nook TV).
- Port the complete feature set from the web repository (`NowShowing-Stream`):
  - Catalog browsing (Trending, Popular Movies, Popular TV, Genre rows)
  - 29-mood precision recommendation engine with genre verification & ranking
  - Search with on-screen keyboard & type filtering
  - Detailed metadata (plot, cast, director, rating, runtime, genre tags)
  - Season & episode picker for TV shows
  - Multi-tier playback engine: Native Media3 (HLS/DASH/MP4), Embedded WebView fallback, and External App handoff (Intents)
  - Local persistence for Watchlist, Continue Watching (with genuine playback progress), preferred source, and cached metadata

## 2. Technology Stack
- **Language**: Kotlin 2.0+
- **Platform**: Android TV / Google TV (Min SDK 26 / Android 8.0 Oreo, Target SDK 35 / Android 15)
- **UI Framework**: Android TV UI with Jetpack Compose for TV & TV Material3 / Leanback architecture
- **Media Playback**: AndroidX Media3 (ExoPlayer 1.4+) for HLS/DASH/MP4, custom TV WebView for embed providers, Android Intent handoff
- **Networking & Serialization**: OkHttp 4 / Retrofit 2 + Kotlinx Serialization / Gson
- **Image Loading**: Coil 2.x with aggressive bitmap downsampling and memory/disk cache
- **Persistence**: Room Database (SQLite) / SharedPreferences for Watchlist, Continue Watching, search history, and metadata cache
- **Build System**: Gradle 8.x with Android Gradle Plugin (AGP) 8.x

## 3. Constraints & Principles
- **10-foot remote navigation**: D-pad only (Up, Down, Left, Right, Center/OK, Back, Play/Pause). Zero pointer or touch dependency.
- **Predictable focus**: Explicit focus management, visible focus indicators, focus restoration on back navigation, horizontal scroll position retention.
- **Performance on low-end TV hardware**: Lazy loading of carousels/grids, memory-bounded image caches, no heavy background autoplay.
- **Playback reality**: Embedded third-party streaming providers are not guaranteed direct mp4/hls streams; graceful fallback to TV-optimized WebView and external player intents.
- **Source of truth**: Web codebase in root; Android project located in `android-tv/` without breaking the web app.
