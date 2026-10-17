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

## 5. Native-Only Playback Stability & Redirect Isolation
- [ ] **100% Native Media3 Surface**: Video plays exclusively through AndroidX Media3 / ExoPlayer. Zero WebViews present in the playback screen.
- [ ] **Web-Embed Rejection**: Selecting an unsupported web embed (VidSrc, AutoEmbed) gracefully displays the Incompatible Source card (*"Native playback is unavailable for this source. Try another source."*).
- [ ] **In-Player Source Switching**: Switching between native streams works smoothly via the TV source modal dialog without crashing.
- [ ] **Remote D-Pad Transport**: Up/Down toggles HUD, Left/Right seeks 10s with visual indicator, Center/OK toggles Play/Pause.
- [ ] **Series Progression**: Previous and Next Episode buttons advance or decrement episodes properly.
- [ ] **Auto-Next Episode Prompt**: 30-second prompt appears non-intrusively and triggers next episode upon clicking "Play Now".
- [ ] **Redirect Loop & Protocol Defense**: Insecure HTTP to non-local hosts and redirects exceeding 5 hops are blocked safely.
- [ ] **Playback Resume Persistence**: Millisecond playback position correctly recorded and restored from Room database.

## 6. Persistence & Offline Resilience
- [ ] **Watchlist Persistence**: Items added to Watchlist remain saved across process death and reboot.
- [ ] **Continue Watching Persistence**: Active titles persist with accurate progress and episode markers.
- [ ] **OMDb Cache**: In-memory and SQLite cache operate to avoid hitting daily API limits.
- [ ] **Network Outage Grace**: App handles network disconnect without crashing; displays friendly retry state.

## 7. Privacy & Content Blocking Verification
- [ ] **Bundled Filter Assets**: Verify `filters_adblock.txt` and `filters_tracking.txt` are bundled in `assets/filters/` and load within <20ms on startup.
- [ ] **Network Interception Active**: Confirm `ProtectedWebViewClient` intercepts ad and tracker requests without breaking embed player video decoders.
- [ ] **Media Fragment Exemption**: Verify `.m3u8`, `.ts`, `.mpd`, `.m4s`, and `.mp4` requests unconditionally bypass blocking.
- [ ] **Pop-up Suppression**: Verify `PopupProtection` suppresses unsolicited `window.open` calls and dialogs.
- [ ] **Player HUD Shield Badge**: Confirm HUD displays active shield status (`🛡️ Shields: ON (N blocked)`) and remote toggle functions properly.
- [ ] **Settings Screen Toggles**: Verify user can toggle Ad Blocking, Tracker Blocking, Pop-up Blocking, and Third-Party Cookie restrictions with persistent state.
- [ ] **Filter List Online Update**: Verify "Update Filter Rules" button in Settings downloads and saves updated rulesets when network is available without crashing when offline.
- [ ] **Local-Only Telemetry**: Verify zero remote telemetry; all blocked counts remain strictly in local memory and private app storage.

