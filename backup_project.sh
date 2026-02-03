#!/bin/bash

BACKUP_DIR="$HOME/TaskwarriorAndroid_Backup_$(date +%Y%m%d_%H%M%S)"
PROJECT_DIR="$HOME/TaskwarriorAndroidComplete"

echo "=== Creating backup of Taskwarrior Android Project ==="
echo "Source: $PROJECT_DIR"
echo "Destination: $BACKUP_DIR"

# Create backup directory
mkdir -p "$BACKUP_DIR"

# Copy essential files
echo "1. Copying source code..."
cp -r "$PROJECT_DIR/app/src" "$BACKUP_DIR/"
cp -r "$PROJECT_DIR/app/build.gradle" "$BACKUP_DIR/"
cp -r "$PROJECT_DIR/app/proguard-rules.pro" "$BACKUP_DIR/" 2>/dev/null || true

echo "2. Copying project configuration..."
cp "$PROJECT_DIR/build.gradle" "$BACKUP_DIR/"
cp "$PROJECT_DIR/settings.gradle" "$BACKUP_DIR/"
cp "$PROJECT_DIR/gradle.properties" "$BACKUP_DIR/" 2>/dev/null || true
cp "$PROJECT_DIR/gradlew" "$BACKUP_DIR/"
cp "$PROJECT_DIR/gradlew.bat" "$BACKUP_DIR/" 2>/dev/null || true

echo "3. Copying documentation..."
find "$PROJECT_DIR" -name "*.md" -exec cp {} "$BACKUP_DIR/" \; 2>/dev/null || true
find "$PROJECT_DIR" -name "README*" -exec cp {} "$BACKUP_DIR/" \; 2>/dev/null || true

# Create a manifest of what was backed up
echo "4. Creating backup manifest..."
cat > "$BACKUP_DIR/BACKUP_MANIFEST.txt" << MANIFEST
Taskwarrior Android Project Backup
===================================
Backup created: $(date)
Original location: $PROJECT_DIR

Contents:
---------
$(find "$BACKUP_DIR" -type f | sort)

Project Structure:
------------------
$(tree "$BACKUP_DIR" 2>/dev/null || find "$BACKUP_DIR" -type f | sed 's|^|  |')

To restore:
-----------
1. Create new Android project in Android Studio
2. Replace files with backup contents
3. Sync Gradle

MANIFEST

echo "✅ Backup created at: $BACKUP_DIR"
echo ""
echo "📁 Backup contents:"
ls -la "$BACKUP_DIR"
echo ""
echo "📊 Total files: $(find "$BACKUP_DIR" -type f | wc -l)"
