#!/bin/bash
echo "=== Checking Build Configuration ==="

echo "1. Checking AndroidManifest.xml..."
if grep -q "MainActivity" app/src/main/AndroidManifest.xml; then
    echo "✓ MainActivity found in manifest"
    grep -A2 -B2 "MainActivity" app/src/main/AndroidManifest.xml
else
    echo "✗ MainActivity NOT found in manifest!"
fi

echo "2. Checking build.gradle..."
echo "Min SDK:"
grep minSdkVersion app/build.gradle
echo "Target SDK:"
grep targetSdkVersion app/build.gradle

echo "3. Checking for compilation errors..."
./gradlew compileDebugJavaWithJavac --info 2>&1 | grep -i "error\|fail\|exception" | head -10

echo "4. Checking APK structure..."
if [ -f app/build/outputs/apk/debug/app-debug.apk ]; then
    echo "APK exists, checking contents..."
    unzip -l app/build/outputs/apk/debug/app-debug.apk | grep -E "(MainActivity|TaskwarriorBundled|AndroidManifest)" | head -10
else
    echo "APK not found!"
fi

echo "5. Checking if layout file exists..."
ls -la app/src/main/res/layout/activity_main.xml 2>/dev/null || echo "Layout file missing!"
