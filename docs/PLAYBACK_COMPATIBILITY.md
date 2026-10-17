# Playback Compatibility & Source Matrix: NowShowing TV

## 1. Playback Architecture & Reality

A critical discovery during the source code audit of `NowShowing-Stream` is that the 11 streaming providers configured in `js/config.js` (`VIDEO_SOURCES`) are **third-party embed providers** designed for web browsers (`<iframe>`). They do not provide stable, unauthenticated, raw `.mp4` or `.m3u8` video stream endpoints that can be fed directly to AndroidX Media3 / ExoPlayer without session tokens, CORS headers, or anti-hotlinking referrer checks.

To address this on Android TV without compromising platform stability, NowShowing TV implements a **Three-Tier Hybrid Playback Engine**:

```
                       ┌─────────────────────────┐
                       │ Playback Request (Title)│
                       └────────────┬────────────┘
                                    │
                                    ▼
                       ┌─────────────────────────┐
                       │ Preferred Source Lookup │
                       └────────────┬────────────┘
                                    │
                  ┌─────────────────┴─────────────────┐
                  ▼                                   ▼
        [Direct Media Stream]               [Web Embed Provider]
                  │                                   │
                  ▼                                   ▼
        ┌───────────────────┐               ┌───────────────────┐
        │ Tier 1: Media3    │               │ Tier 2: TV WebView│
        │ ExoPlayer         │               │ Hardware Accel    │
        │ (HLS/DASH/MP4)    │               │ TV Remote D-Pad   │
        └───────────────────┘               └─────────┬─────────┘
                  │                                   │
                  │              Stream Error         │
                  └─────────────────┬─────────────────┘
                                    ▼
                       ┌─────────────────────────┐
                       │ Tier 3: External Player │
                       │ (VLC / Just Player)     │
                       └─────────────────────────┘
```

## 2. Playback Tiers Detailed

### Tier 1: AndroidX Media3 / ExoPlayer (Native)
- **Target**: Direct HTTP/HTTPS streams, HLS playlists (`.m3u8`), and MPEG-DASH (`.mpd`).
- **Capabilities**:
  - Full hardware decoding (MediaCodec).
  - True timeline tracking: records playback millisecond position in local database (`ContinueWatchingEntity`).
  - Automatic completion threshold: marks content completed at 90% duration.
  - Native audio track, subtitle track, and quality track selection.
  - Native TV playback controls (Play/Pause, 10s skip, timeline scrub).

### Tier 2: TV-Optimized WebView Player (Embed Fallback)
- **Target**: Web-based embed providers (`vidsrc`, `2embed`, `autoembed`, etc.).
- **Capabilities**:
  - Full-screen hardware accelerated `WebView`.
  - Configured with Chrome TV / Modern Mobile User-Agent to request HTML5 responsive video players.
  - Enabled features: `setMediaPlaybackRequiresUserGesture(false)`, WebRTC, hardware acceleration, DOM storage.
  - WebChromeClient handles full-screen mode transitions (`onShowCustomView`).
  - D-pad click emulation: maps remote `OK` button to touch/click events on focused video controls.
  - Blank player detection: monitors WebView loading progress; triggers a non-intrusive HUD if the source stays blank or fails to render within 10 seconds.

### Tier 3: External Video Player Intent (Handoff)
- **Target**: Users with specialized media player apps installed on Android TV (e.g. VLC for Android, Just Player, Nova Video Player, MX Player).
- **Action**: Launches `Intent(Intent.ACTION_VIEW)` with the stream or web URL.
- **Benefit**: Ensures uninterrupted viewing even if an embed provider has complex WebRTC or ad-blocking restrictions.

## 3. Streaming Source Evaluation Matrix

The 11 providers ported from `js/config.js` have been audited for Android TV compatibility:

| Provider Name | Base Movie URL | Series URL Structure | Native Media3 | TV WebView | External Intent | Notes & Status |
|---|---|---|:---:|:---:|:---:|---|
| **VidSrc.to** | `https://vidsrc.to/embed/movie/` | `.../embed/tv/{id}/{s}/{e}` | ❌ | ✅ Supported | ✅ Supported | High uptime; responsive iframe player; requires standard browser headers. |
| **AutoEmbed** | `https://autoembed.co/movie/imdb/` | `.../tv/imdb/{id}/{s}/{e}` | ❌ | ✅ Supported | ✅ Supported | Fast loading; clean player interface; works well in Android WebView. |
| **2Embed.cc** | `https://www.2embed.cc/embed/` | `.../embedtv/{id}&s={s}&e={e}` | ❌ | ✅ Supported | ✅ Supported | Classic aggregator; embeds HTML5 player. |
| **2Embed.stream** | `https://www.2embed.stream/embed/movie/` | `.../embed/tv/{id}/{s}/{e}` | ❌ | ✅ Supported | ✅ Supported | Alternative mirror for 2Embed. |
| **VidSrc.icu** | `https://vidsrc.icu/embed/movie/` | `.../embed/tv/{id}/{s}/{e}` | ❌ | ✅ Supported | ✅ Supported | Mirror of VidSrc. |
| **VidSrc.xyz** | `https://vidsrc.xyz/embed/movie/` | `.../embed/tv/{id}/{s}/{e}` | ❌ | ✅ Supported | ✅ Supported | Stable embed endpoint. |
| **VidSrc.me** | `https://v2.vidsrc.me/embed/` | `.../embed/{id}/{s}-{e}/` | ❌ | ⚠️ Mixed | ✅ Supported | Strict referrer verification; requires custom referrer header. |
| **MoviesAPI** | `https://moviesapi.club/movie/` | `.../tv/{id}/{s}/{e}` | ❌ | ✅ Supported | ✅ Supported | Responsive player. |
| **MultiEmbed** | `https://multiembed.mov/?video_id=` | `.../?video_id={id}&s={s}&e={e}` | ❌ | ✅ Supported | ✅ Supported | Multi-stream selector built-in. |
| **EmbedSu** | `https://embed.su/embed/movie/` | `.../embed/tv/{id}/{s}/{e}` | ❌ | ✅ Supported | ✅ Supported | Fast embed delivery. |
| **VidSrc.mov** | `https://vidsrc.mov/embed/movie/` | `.../embed/tv/{id}/{s}/{e}` | ❌ | ✅ Supported | ✅ Supported | Additional fallback mirror. |

## 4. Source Switching & Recovery Protocol
1. **Initial Selection**: The app automatically selects the user's preferred source (defaults to `VidSrc.to` or `AutoEmbed`).
2. **One-Click Rotation**: If a source fails to load, produces an error, or remains blank for more than 10 seconds:
   - A remote-accessible overlay banner prompts: *"Source not loading? Press OK to switch to AutoEmbed (or Next Source)"*.
   - Pressing `OK` automatically swaps the stream URL to the next available source in `VIDEO_SOURCES` without exiting the player.
3. **Memory**: When a user manually switches sources and playback succeeds, that source is recorded as the new preferred source in `SharedPreferences`.

## 5. Privacy Shields & Source Impact
NowShowing TV embeds a native content-filtering layer (`ProtectedWebViewClient`) into all web player sessions:
- **Advertising & Tracker Blocking**: Blocked at network request level; returning empty 200 OK responses prevents embedded player scripts from stalling.
- **Pop-up & Redirection Suppression**: Aggressive ad networks attempting to spawn pop-unders or hijack WebView URLs via `intent://` or unauthorized redirects are blocked.
- **Whitelisted Video Streams**: Direct media segments (`.m3u8`, `.ts`, `.mpd`, `.m4s`, `.mp4`) and subtitles are permanently exempted from blocking rules.
- **Source-Specific Override**: If a third-party source utilizes anti-adblock mechanisms or fails to load under active filtering, users can toggle shields off for that specific provider using the TV HUD or the 10-second recovery prompt (*"Disable Shields for this source"*).

## 6. Compliance & Security Notice
NowShowing TV does not host, index, or proxy video streams. It acts strictly as a discovery and client-side playback coordinator. No DRM mechanisms are bypassed, and no paywalls are skirted.
