# Changelog

All notable changes to the NowShowing project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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
