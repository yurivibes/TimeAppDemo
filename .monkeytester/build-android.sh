#!/usr/bin/env bash
# MonkeyTester Build & Test: TimeAppDemo Android build.
#
# The build plugin (build-local or build-github) runs this from the root of a clean checkout of the branch being
# built, on the plugin's own host, with MT_OUTPUT set to the path the built
# app must be written to.
set -euo pipefail

: "${MT_OUTPUT:?MT_OUTPUT is not set (this script is run by a MonkeyTester build plugin)}"

# Gradle reads the SDK location from local.properties; the plugin host sets
# ANDROID_HOME (the Mac keeps it in ~/Library/Android/sdk).
sdk="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-}}"
if [ -z "$sdk" ]; then
  if [ -d "$HOME/Library/Android/sdk" ]; then
    sdk="$HOME/Library/Android/sdk"
  else
    sdk="$HOME/Android/Sdk"
  fi
fi
if [ ! -d "$sdk" ]; then
  echo "error: no Android SDK found (set ANDROID_HOME; looked in $sdk)" >&2
  exit 1
fi
echo "sdk.dir=$sdk" > local.properties

# The app module is androidApp (see README: ./gradlew :androidApp:assembleDebug).
# :shared builds the Kotlin/Native iOS framework; its ios targets are not needed
# for an APK and need the iOS toolchain, so exclude them on a Linux host.
./gradlew :androidApp:assembleDebug \
  --no-daemon --no-configuration-cache \
  -x :shared:compileKotlinIosArm64 \
  -x :shared:compileKotlinIosSimulatorArm64

apk=androidApp/build/outputs/apk/debug/androidApp-debug.apk
if [ ! -s "$apk" ]; then
  echo "error: Gradle produced no APK at $apk" >&2
  exit 1
fi
cp "$apk" "$MT_OUTPUT"
echo "built: $MT_OUTPUT ($(wc -c < "$MT_OUTPUT") bytes)"
