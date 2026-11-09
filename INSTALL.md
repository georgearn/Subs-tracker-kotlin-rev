# Build & install (release, signed, arm64)

## Prereqs
- JDK 17 (Eclipse Adoptium): `C:\Program Files\Eclipse Adoptium\jdk-17.0.20.8-hotspot`
- Android SDK: `D:\Android\AndroidSDK` (adb at `platform-tools\adb.exe`)
- Signing key: `C:\Users\GeorgeArn\.android-keystores\android-release.jks` (alias `androidrelease`)

## 1. Build

```powershell
. "$env:USERPROFILE\.android-keystores\set-env.ps1"
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.20.8-hotspot"
cd D:\Project\subscriptions_kotlin
./gradlew assembleRelease
```

Output APK: `app\build\outputs\apk\release\app-release.apk`

## 2. Install on device (USB debugging enabled, device authorized)

```powershell
D:\Android\AndroidSDK\platform-tools\adb.exe devices
D:\Android\AndroidSDK\platform-tools\adb.exe install -r "D:\Project\subscriptions_kotlin\app\build\outputs\apk\release\app-release.apk"
```

## Notes
- `-r` reinstalls, keeps data, only works if signing key matches what's already on device.
- If device has a build signed with a different key (e.g. old debug build), install fails with `INSTALL_FAILED_UPDATE_INCOMPATIBLE`. Fix:
  ```powershell
  D:\Android\AndroidSDK\platform-tools\adb.exe uninstall com.georgearn.subscriptiontracker
  D:\Android\AndroidSDK\platform-tools\adb.exe install "D:\Project\subscriptions_kotlin\app\build\outputs\apk\release\app-release.apk"
  ```
  This wipes app data on device.
- App config: package `com.georgearn.subscriptiontracker`, arm64-v8a only, compileSdk/targetSdk 37, minSdk 31.
