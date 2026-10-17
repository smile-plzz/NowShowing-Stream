# Installation & Sideloading Guide: NowShowing TV

## 1. Overview
NowShowing TV is distributed as a standalone native Android package (`.apk`). It is designed for installation on Android TV, Google TV, and Android TV-based streaming boxes (Nvidia Shield, Chromecast with Google TV, Xiaomi Mi Box, Amazon Fire TV, smart TVs running Android TV).

No Google Play Store publication is required.

## 2. Prerequisites
1. **Target Device**: Android TV / Google TV running Android 8.0 (API level 26) or newer.
2. **Developer Options**:
   - On your TV, navigate to **Settings** > **Device Preferences** (or **System**) > **About**.
   - Scroll down to **Android TV OS build** (or **Build**) and press `OK` 7 times until you see the toast: *"You are now a developer!"*.
   - Return to **Settings** > **Developer options** and enable **USB debugging** (and **Network debugging** if installing wirelessly).

## 3. Installation Methods

### Method A: Direct ADB Sideloading via USB (Recommended for Developers)
1. Connect your computer to the Android TV device via USB cable (or ensure both are on the same local Wi-Fi network).
2. Verify connection:
   ```bash
   adb devices
   ```
3. Install the APK:
   ```bash
   adb install -r android-tv/app/build/outputs/apk/debug/app-debug.apk
   ```
4. Launch the application:
   ```bash
   adb shell am start -n com.nowshowing.tv/.ui.MainActivity
   ```

### Method B: Wireless ADB Sideloading over Wi-Fi
1. Find your TV's local IP address under **Settings** > **Network & Internet** (e.g. `192.168.1.150`).
2. Connect to the TV from your terminal:
   ```bash
   adb connect 192.168.1.150:5555
   ```
3. Authorize the debugging prompt that appears on your television screen.
4. Sideload the APK:
   ```bash
   adb install -r android-tv/app/build/outputs/apk/debug/app-debug.apk
   ```

### Method C: Sideloading via USB Drive or File Manager
1. Copy `app-debug.apk` (or `app-release.apk`) to a standard USB flash drive formatted as FAT32 or NTFS.
2. Plug the USB flash drive into your Android TV box or television port.
3. Open any file manager on the TV (e.g., *File Commander*, *AnExplorer*, or *X-plore*).
4. When prompted by the system, enable **Install unknown apps** for the file manager.
5. Select the APK and click **Install**.

### Method D: Sideloading via "Send Files to TV" or "Downloader"
1. Install the free app **Send files to TV** (SFTV) from Google Play on both your phone/PC and TV.
2. Transfer the APK file to the TV.
3. Open the APK on the TV using an APK installer app to complete installation.

## 4. Launcher & Nook TV Integration
- **Standard Android TV / Google TV**: The app declares `android.intent.category.LEANBACK_LAUNCHER` and provides a native `320x180` TV banner. It appears directly in your TV's home screen apps row.
- **Nook TV Launcher**: Nook TV discovers all apps declaring either `LEANBACK_LAUNCHER` or `LAUNCHER`. NowShowing TV is immediately detected, displays its high-resolution banner, and launches seamlessly with full remote control focus handoff.

## 5. Troubleshooting Installation Issues
- **"INSTALL_FAILED_UPDATE_INCOMPATIBLE"**: An earlier version with a differing signature exists. Uninstall the previous build first (`adb uninstall com.nowshowing.tv`) and re-run install.
- **"Parse Error / There was a problem parsing the package"**: Confirm the target Android TV is running Android 8.0 (API 26) or newer.
- **"App not installed"**: Ensure **Unknown sources** is allowed in TV Settings under **Security & Restrictions**.
