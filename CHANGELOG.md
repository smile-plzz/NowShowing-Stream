# Changelog

All notable changes to the NowShowing project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.1.0] - 2026-10-17

### Added
- **Native Brave-Inspired Privacy & Content Blocking (`android-tv/app/src/main/java/com/nowshowing/tv/privacy/`)**:
  - Native network request interception via `WebViewClient.shouldInterceptRequest` returning empty 200 OK responses for blocked requests.
  - High-performance Pure-Kotlin Adblock Plus (ABP) rule matcher (`ContentBlockEngine.kt`) supporting domain anchors (`||domain^`), path wildcards (`/ads/*`), and a 1000-entry LRU decision cache.
  - Automatic media bypass guaranteeing `.m3u8`, `.ts`, `.mpd`, `.m4s`, and `.mp4` video segments are never disrupted.
  - Unsolicited pop-up, background tab, and alert dialog suppression via `PopupProtection.kt` (`WebChromeClient`).
  - Bundled offline filter rules (`assets/filters/filters_adblock.txt`, `filters_tracking.txt`) with background HTTPS update capabilities.
  - Per-source shield exemption overrides with persistence in SharedPreferences.
  - Thread-safe local atomic telemetry counters (`BlockingStatistics.kt`) tracking blocked ads, trackers, and suppressed pop-ups without any remote data transmission.
- **TV Player HUD Shields Integration (`PlayerScreen.kt`)**:
  - Live HUD shield badge showing active protection state and blocked item counter (`🛡️ Shields: ON (N blocked)`).
  - Remote-friendly one-click toggle to disable shields for the active streaming source if playback fails.
  - Recovery prompt integration allowing users to disable shields or switch sources upon detecting a blank player.
- **Privacy & Content Blocking TV Settings Screen (`SettingsScreen.kt`)**:
  - Dedicated TV-first settings screen accessible via top navigation.
  - D-pad toggles for Ad Blocking, Tracker Blocking, Pop-up Blocking, and Third-Party Cookie restrictions.
  - Real-time blocking counters and one-click statistics reset.
  - Manual "Update Filter Rules" trigger for refreshing blocklists from remote repositories.
- **Privacy Documentation**:
  - Added `docs/PRIVACY_AND_BLOCKING.md` detailing architecture, network interception, rule syntax, and threat model.
  - Updated `docs/ARCHITECTURE.md`, `docs/PLAYBACK_COMPATIBILITY.md`, `docs/QA_TEST_PLAN.md`, and `docs/RELEASE_CHECKLIST.md`.

## [1.0.0] - 2026-10-17

### Added
- **Native Android TV Application (`android-tv/`)**:
  - Independent, sideloadable Android TV APK with `LEANBACK_LAUNCHER` and TV banner (`@drawable/banner`).
  - 10-foot remote-controlled UI designed with Jetpack Compose for TV & TV Material.
  - Alphanumeric D-pad on-screen keyboard with filter chips and search history.
  - Full port of the precision 29-mood recommendation engine (`MoodRecommendationEngine.kt`) with candidate deduplication, cached genre verification, and composite score ranking.
  - Multi-tier playback engine (`PlayerScreen.kt`): Native AndroidX Media3 (ExoPlayer) for direct media streams, TV-optimized hardware-accelerated WebView for third-party embed providers, and external player intent handoff (VLC / Just Player).
  - 10-second blank player detection and one-click source rotation.
  - Persistent storage in SQLite (`NowShowingDatabase.kt`) for Watchlist, Continue Watching with true playback progress, and OMDb request caching.
  - Complete test suite: `SourceRegistryTest.kt` and `MoodRecommendationEngineTest.kt` (100% pass).
- **Documentation Suite (`docs/`)**:
  - `ANDROID_TV_PRD.md` — Product requirements, TV ergonomics, and acceptance criteria.
  - `ARCHITECTURE.md` — Clean architecture, domain boundaries, and data flow.
  - `REMOTE_NAVIGATION.md` — D-pad navigation matrix, focus indicators, and state restoration.
  - `PLAYBACK_COMPATIBILITY.md` — Provider-by-provider compatibility matrix and playback tiers.
  - `QA_TEST_PLAN.md` — Comprehensive test execution matrix.
  - `INSTALLATION.md` — Sideloading via ADB (USB/Wi-Fi), flash drive, and launcher integration.
  - `RELEASE_CHECKLIST.md` — Pre-distribution readiness checklist.
- Project workflow tracking: `context.md`, `dev-log.md`, `CHANGELOG.md`.
