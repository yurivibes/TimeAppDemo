#!/usr/bin/env bash
# MonkeyTester Build & Test: TimeAppDemo iOS build (signed .ipa for a real iPhone).
#
# build-local runs this from the root of a clean checkout of the branch being
# built, on the plugin's Mac, with VT_OUTPUT set to the path the built app must
# be written to.
set -euo pipefail

: "${VT_OUTPUT:?VT_OUTPUT is not set (this script is run by build-local)}"

if [ ! -d iosApp/iosApp.xcodeproj ]; then
  echo "error: iosApp/iosApp.xcodeproj is missing from this branch" >&2
  exit 1
fi
if [ ! -s iosApp/ExportOptions.plist ]; then
  echo "error: iosApp/ExportOptions.plist is missing (the export needs the signing options)" >&2
  exit 1
fi

out=build/visual-test
mkdir -p "$out"

# iosApp's Xcode project compiles the shared Kotlin/Native framework itself
# (its build phase calls ./gradlew), so no separate Gradle step is needed here.
xcodebuild archive \
  -project iosApp/iosApp.xcodeproj \
  -scheme iosApp \
  -configuration Debug \
  -destination "generic/platform=iOS" \
  -archivePath "$out/app.xcarchive" \
  -allowProvisioningUpdates

xcodebuild -exportArchive \
  -archivePath "$out/app.xcarchive" \
  -exportOptionsPlist iosApp/ExportOptions.plist \
  -exportPath "$out/export" \
  -allowProvisioningUpdates

ipa=$(find "$out/export" -name '*.ipa' | head -1)
if [ -z "$ipa" ]; then
  echo "error: xcodebuild exported no .ipa" >&2
  exit 1
fi
cp "$ipa" "$VT_OUTPUT"
echo "built: $VT_OUTPUT ($(wc -c < "$VT_OUTPUT") bytes)"
