#!/bin/bash
cd ~/dev/twC3

echo "=== TEST for twC3 ==="
echo "Device: $(adb shell getprop ro.product.model 2>/dev/null || echo 'emulator')"
echo ""

echo "1. Building app..."
./gradlew assembleDebug

echo ""
echo "2. Installing..."
adb install -r -g app/build/outputs/apk/debug/app-debug.apk

echo ""
echo "3. CLEARING OLD DATA..."
adb shell pm clear com.taskwarriormobile

echo ""
echo "4. Starting fresh logcat..."
adb logcat -c

echo ""
echo "5. Starting app..."
adb shell am start -n com.taskwarriormobile/.MainActivity

echo ""
echo "6. Waiting 5 seconds..."
sleep 5

echo ""
echo "7. Checking logs..."
adb logcat -d | grep -i "TW_\|taskwarrior\|mainactivity\|AndroidRuntime" | head -50

echo ""
echo "8. Checking extracted binary..."
adb shell "run-as com.taskwarriormobile sh -c 'echo \"App files:\" && ls -la /data/data/com.taskwarriormobile/files/ 2>/dev/null && echo \"--- Binary test: ---\" && /data/data/com.taskwarriormobile/files/task --version 2>&1 || echo \"Binary not found or not executable\"'"
