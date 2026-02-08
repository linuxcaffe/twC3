#!/bin/bash
echo "=== Starting Debug Session ==="

# Rebuild and install
cd ~/dev/twC3
./gradlew assembleDebug

echo "=== Installing app ==="
adb install -r app/build/outputs/apk/debug/app-debug.apk

echo "=== Clearing old app data ==="
adb shell pm clear com.taskwarriormobile

echo "=== Starting activity with logging ==="
adb shell am start -n com.taskwarriormobile/.MainActivity

echo "=== Waiting 5 seconds for app initialization ==="
sleep 5

echo "=== Checking app logs ==="
adb logcat -d -s TaskwarriorBundled:V MainActivity:V AndroidRuntime:E | head -100

echo "=== Checking extracted binary ==="
adb shell "run-as com.taskwarriormobile sh -c 'ls -la /data/data/com.taskwarriormobile/files/ 2>/dev/null && echo \"---\" && ls -la /data/data/com.taskwarriormobile/app_bin/ 2>/dev/null || echo \"Could not access app data\"'"

echo "=== Testing binary directly if it exists ==="
adb shell "run-as com.taskwarriormobile sh -c 'if [ -f /data/data/com.taskwarriormobile/files/task ]; then echo \"Binary found, testing...\" && /data/data/com.taskwarriormobile/files/task --version; else echo \"Binary not found\"; fi'"

echo "=== Checking assets directory ==="
ls -la app/src/main/assets/bin/
