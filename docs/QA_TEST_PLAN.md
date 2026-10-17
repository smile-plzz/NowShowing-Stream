# QA Test Plan: NowShowing TV

## 1. Scope & Test Environment
- **Target Hardware**: Android TV emulator (API 33/35, 1080p TV), Google TV Chromecast HD/4K, Nvidia Shield TV.
- **Input Devices**: Standard Android TV D-pad remote (Up, Down, Left, Right, OK/Select, Back, Play/Pause).
- **Core Objectives**: Guarantee 100% remote-navigable 10-foot UI, rock-solid OMDb metadata handling, accurate 29-mood recommendation engine, resilient multi-source playback, and persistent library state.

## 2. Test Execution Matrix

### 2.1. TV Launcher & Manifest Compliance
| Test Case ID | Objective | Expected Result | Pass/Fail Criteria |
|---|---|---|---|
| **TC-MAN-01** | Verify Android TV Banner | App displays 320x180 leanback banner on Android TV home screen. | Banner renders sharply without distortion or clipping. |
| **TC-MAN-02** | Sideload Discovery | App appears in Nook TV and default Android TV app drawer. | Icon/Banner visible; one-click launch from launcher. |
| **TC-MAN-03** | Touchscreen Independence | `android.hardware.touchscreen` marked `false` in manifest. | App installs on devices with zero touch hardware. |
| **TC-MAN-04** | Fixed Orientation | Verify application forces landscape mode. | Screen stays landscape (`16:9`) regardless of sensor events. |

### 2.2. D-pad Remote Navigation & Focus Management
| Test Case ID | Objective | Expected Result | Pass/Fail Criteria |
|---|---|---|---|
| **TC-NAV-01** | Initial Focus Acquisition | On initial launch, focus lands predictably on the Hero "Play" button or Top Nav. | No invisible focus; cyan/gold indicator clearly visible. |
| **TC-NAV-02** | D-pad Directional Matrix | Pressing Up, Down, Left, Right moves cleanly between elements. | Focus never jumps erratically or gets trapped. |
| **TC-NAV-03** | Rapid D-pad Navigation | Rapidly press Left/Right/Down across large carousel rows. | UI does not stutter or crash; images load smoothly via Coil. |
| **TC-NAV-04** | Back Key Focus Restoration | Open Detail screen from Home carousel item #5, press Back. | Focus restores precisely to item #5; scroll position maintained. |
| **TC-NAV-05** | No Off-Screen Traps | Navigate to ends of horizontal carousels. | Focus stops cleanly at row boundary; does not disappear into void. |

### 2.3. Catalog Discovery & Metadata
| Test Case ID | Objective | Expected Result | Pass/Fail Criteria |
|---|---|---|---|
| **TC-CAT-01** | Home Rows Loading | Verify Trending, Genre rows (Action, Sci-Fi, Drama, Comedy, Thriller), Popular Movies/TV. | All rows populate with posters and titles from OMDb seeds. |
| **TC-CAT-02** | Missing Metadata Grace | Load titles with missing posters (`N/A`) or missing plot summaries. | Fallback poster placeholder rendered; no crash. |
| **TC-CAT-03** | Catalog Sorting | In Browse Movies/TV, sort by Title A-Z, Newest, Top Rated. | Grid items reorder instantly according to selected criterion. |

### 2.4. Precision 29-Mood Recommendation Engine
| Test Case ID | Objective | Expected Result | Pass/Fail Criteria |
|---|---|---|---|
| **TC-MOD-01** | 29 Mood Palette Display | Navigate to Mood screen; verify all 29 moods rendered with emojis and labels. | 29 tiles present in grid with D-pad navigation. |
| **TC-MOD-02** | Precision Ranking Logic | Select mood "Happy". | Candidates fetched from mapped genres (Comedy, Musical, Family), scored by genre match count, and ranked by score + rating. |
| **TC-MOD-03** | Mood Detail Caching | Run "Happy", exit screen, run "Happy" again. | Second run uses cached detail lookups from Room; rapid result display without extra API calls. |
| **TC-MOD-04** | Surprise Me Button | Click "Surprise Me" with remote OK. | Random mood selected; recommendation pipeline executes automatically. |

### 2.5. 10-Foot Search
| Test Case ID | Objective | Expected Result | Pass/Fail Criteria |
|---|---|---|---|
| **TC-SCH-01** | On-Screen Keyboard | Use D-pad to type search term (e.g. "Dune"). | Letters input accurately; clear highlight on each virtual key. |
| **TC-SCH-02** | Search Results & Type Filter | Switch filter pill between "All", "Movies", "TV Series". | Query results update to reflect type filter. |
| **TC-SCH-03** | Search History | Enter search, exit screen, reopen Search. | Previous search query appears in recent search row; clickable via OK. |

### 2.6. Title Details & TV Series Navigation
| Test Case ID | Objective | Expected Result | Pass/Fail Criteria |
|---|---|---|---|
| **TC-DET-01** | Full Metadata Display | Open detail for a title (e.g., "Inception"). | Title, Year, Rating, Runtime, Director, Cast, Plot tags correctly displayed. |
| **TC-DET-02** | TV Series Seasons & Episodes | Open a TV show (e.g., "Breaking Bad"). | Season selector pills appear; selecting Season 2 loads Season 2 episode list. |
| **TC-DET-03** | Watchlist Toggle | Click "Add to Watchlist" button. | State toggles to "In Watchlist"; entry persists in Room database. |
| **TC-DET-04** | Recommended Titles | Verify recommendations row at bottom of Detail screen. | Similar titles in primary genre appear; clicking opens their detail. |

### 2.7. Playback & Error Recovery
| Test Case ID | Objective | Expected Result | Pass/Fail Criteria |
|---|---|---|---|
| **TC-PLY-01** | Native Media3 Playback | Launch direct video stream (MP4/HLS test stream). | Video plays smoothly; timeline scrubber works via D-pad Left/Right; Play/Pause toggles. |
| **TC-PLY-02** | TV WebView Embed Playback | Launch playback with embed source (e.g., `VidSrc.to`, `AutoEmbed`). | Embed player renders in full-screen hardware-accelerated WebView. |
| **TC-PLY-03** | Source Switching | Click "Switch Source" button in player overlay. | App rotates to next provider in `VIDEO_SOURCES` without crash. |
| **TC-PLY-04** | External Player Intent | Select "Open in External Player". | Android system chooser or VLC opens with target stream URL. |
| **TC-PLY-05** | Playback Progress Tracking | Watch media for 5 minutes, press Back, view "Continue Watching". | Continue Watching row displays title with exact 5-minute resume timestamp. |

### 2.8. Persistence & Memory Stability
| Test Case ID | Objective | Expected Result | Pass/Fail Criteria |
|---|---|---|---|
| **TC-PER-01** | Cold App Restart | Add items to Watchlist, kill app process, relaunch app. | Watchlist items load instantly from local Room SQLite database. |
| **TC-PER-02** | Low-Memory Stability | Run app continuously for 30 minutes across multiple browsing screens. | Heap remains bounded (<120MB); no `OutOfMemoryError` due to Coil bitmap pooling. |
