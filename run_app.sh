#!/bin/bash
cd ~/dev/twC3

echo "=== Starting twC3 with full logging ==="

# Clear logs
adb logcat -c

# Start the app
adb shell am start -n com.taskwarriormobile/.MainActivity

echo ""
echo "=== Waiting 5 seconds for initialization ==="
sleep 5

echo ""
echo "=== App Logs ==="
echo "Looking for TW_ tags and errors:"
adb logcat -d | grep -E "(TW_|Taskwarrior|mainactivity|AndroidRuntime)" | head -50

echo ""
echo "=== Checking if binary was extracted ==="
adb shell "run-as com.taskwarriormobile sh -c 'echo \"Files directory:\" && ls -la /data/data/com.taskwarriormobile/files/ 2>/dev/null && echo \"--- Binary test ---\" && /data/data/com.taskwarriormobile/files/task --version 2>&1 || echo \"Cannot access or binary not working\"'"

echo ""
echo "=== Checking app process ==="
adb shell "ps -A | grep taskwarrior || echo 'App process not found'"

echo ""
echo "=== Testing manual binary execution ==="
# First push to temp location
adb push app/src/main/assets/bin/arm64-v8a/task /data/local/tmp/ 2>/dev/null
adb shell "chmod 755 /data/local/tmp/task && echo 'Direct binary test:' && /data/local/tmp/task --version"

echo ""
echo "=== If app didn't start, try launching manually from phone ==="
echo "Look for 'Taskwarrior Mobile' in app drawer and tap it!"
