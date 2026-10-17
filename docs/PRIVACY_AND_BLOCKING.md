# Privacy & Content Blocking: NowShowing TV

## 1. Overview & Vision
NowShowing TV incorporates a built-in, native content and tracker blocking architecture inspired by **Brave Shields**. Designed specifically for Android TV and Google TV, it runs entirely inside the native application without requiring external browser installations, browser extensions, root permissions, or VPN services.

The primary objective is to protect television viewers from intrusive pop-ups, disruptive video ad injectors, malicious redirect loops, third-party behavioral trackers, and unnecessary bandwidth consumption during embedded web streaming.

```
                     ┌────────────────────────────────────┐
                     │ WebView Request (Embedded Stream)  │
                     └─────────────────┬──────────────────┘
                                       │
                                       ▼
                     ┌────────────────────────────────────┐
                     │ ProtectedWebViewClient Interceptor │
                     └─────────────────┬──────────────────┘
                                       │
                     ┌─────────────────┴──────────────────┐
                     ▼                                    ▼
           [Is Media Segment?]                  [Is Source Exempt?]
         (.m3u8, .ts, .mpd, .mp4)               (User Disabled)
                     │                                    │
                     ├─────────────── YES ────────────────┘
                     │                                    
                     ▼ (Bypass)                           
             [Allow Request]                              
                     │                                    
                     ▼ NO                                 
       ┌───────────────────────────┐                      
       │  ContentBlockEngine       │                      
       │  - ABP Domain Match       │                      
       │  - Whitelist Rules (@@)   │                      
       │  - Path / Regex Patterns  │                      
       │  - Fast LRU Cache         │                      
       └─────────────┬─────────────┘                      
                     │                                    
             ┌───────┴───────┐                            
             ▼               ▼                            
        [MATCHED]       [CLEAN / NO MATCH]                
             │               │                            
             ▼               ▼                            
     [Block Request]    [Allow Request]                   
     - Record Local Stat                                  
     - Return Empty 200 OK                                
```

## 2. Core Architectural Components

The privacy subsystem is isolated under `com.nowshowing.tv.privacy`:

### 2.1. `ContentBlockEngine.kt`
- **Rule Parser & Evaluator**: Supports Adblock Plus (ABP) syntax including domain anchors (`||doubleclick.net^`), wildcards (`*popunder*`), path rules (`/ad/banner/*`), and whitelist exceptions (`@@||autoembed.co^`).
- **Sub-Millisecond Decision Cache**: Utilizes a synchronized, bounded LRU cache (1,000 entries) ensuring repeat asset evaluations take under 0.05ms, preventing frame drops or remote lag on television chipsets.
- **Strict Media Bypass**: Enforces an inviolable whitelist for streaming media segments (`.m3u8`, `.ts`, `.mpd`, `.m4s`, `.mp4`, `.webm`, `.vtt`, `.srt`, `.aac`) so legitimate playback is never degraded.

### 2.2. `FilterListManager.kt`
- **Offline Baseline**: Ships with curated, compact filter lists bundled directly in app assets (`filters_adblock.txt` and `filters_tracking.txt`), derived from established open-source rules (EasyList and EasyPrivacy concepts).
- **Online HTTPS Updates**: Supports on-demand and periodic background updates over HTTPS with atomic file replacement and checksum verification.
- **Fail-Safe Fallback**: Retains the last known working ruleset if an update attempt fails or times out.

### 2.3. `ProtectedWebViewClient.kt`
- **Request Interception**: Overrides `shouldInterceptRequest` to evaluate all outgoing network requests initiated by the embedded player. Blocked requests receive an immediate empty `200 OK` response to prevent script crashes.
- **Unsolicited Redirect Protection**: Overrides `shouldOverrideUrlLoading` to reject scheme hijacking (`intent://`, `market://`, `tel:`, `whatsapp:`) and unsolicited external redirects away from the streaming player.

### 2.4. `PopupProtection.kt`
- **Window Interception**: Overrides `WebChromeClient.onCreateWindow` to reject unapproved pop-ups and pop-unders from creating new windows or hijacking TV focus.
- **JavaScript Dialog Guard**: Suppresses deceptive alert loops (`onJsAlert`, `onJsConfirm`) that ad networks frequently use to trap users.
- **Fullscreen Video Passthrough**: Maintains native Android TV full-screen transitions (`onShowCustomView`).

### 2.5. `ProtectionPreferences.kt` & `BlockingStatistics.kt`
- **User Settings**: Persists toggles for Content Blocking, Tracker Blocking, Pop-up Blocking, and Third-Party Cookie Restrictions.
- **Per-Source Exceptions**: Allows users to temporarily or permanently disable protection for specific streaming providers if an embed breaks.
- **Local Diagnostics**: Tracks local counters for Ads Blocked, Trackers Blocked, and Pop-ups Suppressed. Zero telemetry or personal data is collected or transmitted externally.

## 3. Television User Experience & Controls

### 3.1. Player HUD Privacy Shield Indicator
When watching content via the embedded web player, the on-screen HUD displays a live shield badge:
- **Shields Active**: `🛡️ Shields: ON (14 blocked)`
- **Shields Disabled**: `🛡️ Shields: OFF (Exempt)`
- **Remote Action**: Remote users can press `OK` on the Shield button to toggle protection on/off for the current source in real-time.

### 3.2. 10-Second Blank Player Recovery
If an embedded player remains blank or fails to render video within 10 seconds, a TV overlay banner appears offering:
1. **Disable Shields for this source**: Instantly reloads the embed with filtering turned off for that specific provider.
2. **Switch Source**: Rotates to the next available provider in `SourceRegistry`.
3. **Dismiss**: Closes the recovery banner.

### 3.3. Dedicated Privacy Settings Screen
Accessible directly via the TV Top Bar ("Shields" tab):
- **Toggles**: Content & Ad Blocking, Tracker & Telemetry Protection, Pop-up Blocking, and Cookie Restrictions.
- **Local Statistics Dashboard**: Displays lifetime counts for Ads Blocked, Trackers Blocked, and Pop-ups Suppressed.
- **Maintenance**: "Update Filter Lists Online" and "Reset Blocking Statistics".
- **Source Exceptions Manager**: Displays all currently exempted streaming sources with a one-click "Restore Protection" button.

## 4. Privacy & Compliance Boundaries
1. **No Paywall or DRM Circumvention**: The engine does not decrypt protected media or bypass provider authentication. It operates strictly as a network and pop-up filter.
2. **Zero Remote Logging**: All blocking metrics and preference overrides remain on the local television storage.
3. **Attribution**: Bundled rulesets credit and adhere to open-source EasyList and EasyPrivacy licensing standards.
