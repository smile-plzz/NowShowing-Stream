# Remote Navigation & Focus Management: NowShowing TV

## 1. Remote Control Key Matrix

NowShowing TV is built from the ground up to be 100% operable with standard Android TV remote controls (including Google TV voice remotes, Shield TV remotes, and generic HDMI-CEC television remotes).

| Remote Key | Android KeyCode | Primary Action | Screen Context |
|------------|-----------------|----------------|----------------|
| **D-pad Up** | `KEYCODE_DPAD_UP` | Move focus up | Moves between carousels, inputs, or header tabs |
| **D-pad Down** | `KEYCODE_DPAD_DOWN` | Move focus down | Moves down to next carousel row or list item |
| **D-pad Left** | `KEYCODE_DPAD_LEFT` | Move focus left / Previous item | Navigates backwards along horizontal carousels |
| **D-pad Right** | `KEYCODE_DPAD_RIGHT` | Move focus right / Next item | Navigates forward along horizontal carousels |
| **Center / OK** | `KEYCODE_DPAD_CENTER` / `KEYCODE_ENTER` | Select / Activate | Opens detail screen, triggers search, switches source |
| **Back** | `KEYCODE_BACK` | Back / Exit / Dismiss | Closes modal/player, returns to previous screen with focus intact |
| **Play / Pause** | `KEYCODE_MEDIA_PLAY_PAUSE` | Toggle playback | Directly toggles video playback in player view |
| **Fast Forward** | `KEYCODE_MEDIA_FAST_FORWARD` | Seek forward | Seeks +10s in Media3 player |
| **Rewind** | `KEYCODE_MEDIA_REWIND` | Seek backward | Seeks -10s in Media3 player |

## 2. Visual Focus Indicator Specifications

To prevent user disorientation from 10 feet away:
1. **Focus State Styling**:
   - Every interactive card, button, and pill features an active focus border (2dp bright accent cyan `#00D2C4` or gold `#FFB800`).
   - Slight subtle scale transform (`1.05x`) with smooth 150ms interpolation.
   - Gentle drop shadow / elevation glow behind focused element.
2. **Unfocused State**:
   - Dark, matte surface with clear contrast against pure black background (`#0D0E12`).
   - Text elements remain legible with minimum 4.5:1 WCAG contrast ratio.

## 3. Screen-by-Screen Navigation Flows

### 3.1. Home Screen
- **Top Bar**: Search icon, Movies tab, TV Shows tab, Moods tab, Watchlist tab.
  - Pressing `Down` from Top Bar drops focus into Hero or the first carousel item.
- **Hero Banner**:
  - Focusable "Play" and "More Info" buttons.
  - Pressing `Down` enters the first horizontal content row ("Continue Watching" or "Trending Now").
- **Horizontal Carousels**:
  - Scrolling is horizontal (`Left`/`Right`).
  - Pressing `Down` moves focus to the vertically aligned card in the row below, maintaining horizontal scroll offset.
  - Pressing `Up` moves focus back to the row above.
  - When the first card in a row is focused, pressing `Left` stops smoothly (no wrap-around unless reaching the sidebar).

### 3.2. Title Details Screen
- **Action Row**: "Play / Resume", "Watchlist" (Add/Remove), "Sources" (Modal trigger).
- **Season Selector**:
  - Horizontal list of season chips ("Season 1", "Season 2", etc.).
  - Pressing `Down` transfers focus directly to the first episode in the episode list.
- **Episode List**:
  - Vertical list of episodes showing episode number, title, and runtime.
  - Pressing `OK` on an episode immediately launches playback for that episode.
- **Recommended Titles Carousel**:
  - Located below the episode list for one-click discovery of related titles.
- **Back Navigation**:
  - Pressing `Back` restores focus to the exact card previously selected on the Home/Catalog screen.

### 3.3. Mood Discovery Screen
- **29-Mood Grid**:
  - Arranged in a responsive TV grid (4 columns wide).
  - Clean D-pad navigation across rows and columns.
- **Quick Actions Bar**:
  - "Surprise Me" button for instant random recommendation generation.
  - Keyword input field with on-screen TV keyboard trigger.
- **Results Row**:
  - Below the mood selector, results populate dynamically. Moving `Down` enters the recommendation results list.

### 3.4. 10-Foot Search Screen
- **On-Screen Keyboard**:
  - High-visibility QWERTY layout optimized for rapid D-pad traversal.
  - Space, Backspace, Clear, and Search action buttons.
  - Type filter pills (All, Movies, TV Series) immediately accessible above the keyboard.
- **Recent Searches**:
  - One-click pill list of previous queries.
- **Results Grid**:
  - Right pane or bottom pane dynamically displays live search results.
  - Pressing `Right` or `Down` moves focus from the keyboard to the results grid.

### 3.5. Playback Screen
- **HUD (Heads-Up Display) Auto-hide**:
  - Playback controls overlay automatically disappears after 4 seconds of inactivity.
  - Any D-pad key press brings the HUD back into view.
- **Controls Layout**:
  - Center: Play/Pause, -10s, +10s.
  - Bottom: Seek timeline bar, current time / total duration.
  - Top-Right: Source switcher button ("VidSrc.to", "AutoEmbed", etc.).
- **Dismissal**:
  - Pressing `Back` pauses playback and cleanly returns to Title Details.

## 4. Focus Retention & State Restoration
1. **Back-Stack State Preservation**:
   - `rememberLazyListState` and `LazyRowState` keys are preserved per route.
   - Returning from Title Details to Home preserves the exact carousel scroll position and restores focus to the previously active poster card.
2. **Asynchronous Load Resilience**:
   - Shimmer skeleton loaders receive temporary focus placeholders if needed, but focus automatically shifts to loaded content upon data arrival without focus dropping off-screen.
   - When a row is empty or loading, D-pad navigation safely bypasses it to the next valid focus target.
