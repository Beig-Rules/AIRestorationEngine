#!/usr/bin/env bash
set -euo pipefail

echo "📦 Building release artifacts..."
./gradlew clean
./gradlew :android:app:bundleRelease :android:app:assembleRelease

echo
echo "✅ Artifacts:"
find android/app/build/outputs -name "*.aab" -o -name "*.apk" 2>/dev/null || echo "  (Run after successful Gradle sync)"
