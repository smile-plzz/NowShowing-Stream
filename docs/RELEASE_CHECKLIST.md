# Release Checklist: NowShowing TV

Use this checklist before distributing a new release build of NowShowing TV.

## 1. Build & Packaging Checks
- [ ] **Reproducible Gradle Build**: `./gradlew clean assembleDebug` (or `assembleRelease`) builds without errors.
- [ ] **APK Artifact Generated**: Verify output at `android-tv/app/build/outputs/apk/debug/app-debug.apk` (or release).
- [ ] **APK Size Target**: Ensure total APK size remains under 25MB (uncompressed resources pruned, Proguard/R8 shrinking enabled for release).
- [ ] **Version Code & Name**: Verify `versionCode` and `versionName` incremented in `app/build.gradle.kts`.

## 2. Android TV Manifest & OS Integration
- [ ] **Leanback Category**: `android.intent.category.LEANBACK_LAUNCHER` declared in `AndroidManifest.xml`.
- [ ] **Touchscreen Flag**: `android.hardware.touchscreen` marked `android:required="false"`.
- [ ] **Leanback Flag**: `android.software.leanback` marked `android:required="true"`.
- [ ] **TV Banner**: High-resolution 320x180 banner drawable included (`@drawable/banner`).
- [ ] **App Icon & Label**: Round/adaptive icon and clear label ("NowShowing TV") configured.
- [ ] **Fixed Orientation**: `screenOrientation="landscape"` enforced on all activities.
- [ ] **Network Permissions**: `android.permission.INTERNET` and `ACCESS_NETWORK_STATE` declared; cleartext traffic permitted where required for embed fallback.

## 3. Remote Navigation & Usability
- [ ] **Pure D-Pad Verification**: Entire application tested from cold boot to video playback using only Up, Down, Left, Right, OK, and Back.
- [ ] **Focus Glow & Visibility**: Visible cyan/gold focus border on every selectable poster, button, tab, and episode item.
- [ ] **Focus Retention**: Pressing Back from Title Details returns focus to the exact originating poster on Home/Catalog.
- [ ] **No Dead Ends**: User is never trapped on any screen without a visible, working Back exit.

## 4. Discovery & Recommendations
- [ ] **Trending & Catalog Rows**: Home page loads all seed rows (`Trending`, `Action`, `Sci-Fi`, `Drama`, `Comedy`, `Thriller`, `Popular Movies`, `Popular TV`).
- [ ] **29-Mood Engine**: All 29 mood buttons operate and return genre-verified, ranked results.
- [ ] **Search Engine**: On-screen keyboard operates smoothly; search results load with pagination.
- [ ] **TV Show Seasons**: TV shows display accurate season chips and episode lists with titles and synopses.

## 5. Playback Stability
- [ ] **Multi-Source Selector**: All 11 video sources accessible via source switcher overlay.
- [ ] **WebView Hardware Acceleration**: Video embeds render smoothly without UI freeze.
- [ ] **One-Click Fallback**: Blank player detector properly prompts to rotate to next source.
- [ ] **External Intent**: "Open in External Player" handoff works if third-party players (e.g., VLC) are installed.
- [ ] **Playback Resume**: True playback position persisted to Continue Watching for Media3 playback.

## 6. Persistence & Offline Resilience
- [ ] **Watchlist Persistence**: Items added to Watchlist remain saved across process death and reboot.
- [ ] **Continue Watching Persistence**: Active titles persist with accurate progress and episode markers.
- [ ] **OMDb Cache**: In-memory and SQLite cache operate to avoid hitting daily API limits.
- [ ] **Network Outage Grace**: App handles network disconnect without crashing; displays friendly retry state.
