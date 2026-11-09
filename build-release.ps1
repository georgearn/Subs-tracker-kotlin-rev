Remove-Item env:\ANDROID_PREFS_ROOT -ErrorAction SilentlyContinue
. "$env:USERPROFILE\.android-keystores\set-env.ps1"
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.20.8-hotspot"
Set-Location D:\Project\subscriptions_kotlin
./gradlew assembleRelease
