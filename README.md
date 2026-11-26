This is a Kotlin Multiplatform project targeting Android, iOS.

* [/iosApp](./iosApp/iosApp) contains an iOS application. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

* [/shared](./shared/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./shared/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./shared/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./shared/src/jvmMain/kotlin)
    folder is the appropriate location.

### Running the apps

Use the run configurations provided by the run widget in your IDE's toolbar. You can also use these commands and options:

- Android app: `./gradlew :androidApp:assembleDebug`
- iOS app: open the [/iosApp](./iosApp) directory in Xcode and run it from there.

### MonkeyTester Build & Test

Build & Test in the MonkeyTester viewer builds this branch on the build plugin's own host by
running the two scripts committed here, one per platform:

- [.monkeytester/build-android.sh](./.monkeytester/build-android.sh) — `./gradlew :androidApp:assembleDebug`,
  then copies `androidApp/build/outputs/apk/debug/androidApp-debug.apk` to `$MT_OUTPUT`.
- [.monkeytester/build-ios.sh](./.monkeytester/build-ios.sh) — archives `iosApp/iosApp.xcodeproj` for a
  real iPhone and exports a signed `.ipa` with `iosApp/ExportOptions.plist` to `$MT_OUTPUT`. The
  Xcode project compiles the shared Kotlin/Native framework itself
  (`:shared:embedAndSignAppleFrameworkForXcode`), so no separate Gradle step runs.

Each script gets `MT_PLATFORM`, `MT_OUTPUT`, `MT_BRANCH`, `MT_CASES` and `MT_REQUEST_ID` from the
plugin; exit 0 with a non-empty file at `MT_OUTPUT` counts as built, anything else is reported as a
build error and its devices show the tests as Unavailable. The Android script needs an Android SDK
(`ANDROID_HOME`, else `~/Library/Android/sdk` on a Mac) and a JDK; the iOS script runs on the Mac and
signs with the identity in that user's login keychain, so the provisioning profile must include every
test iPhone's UDID.

### Running tests

Use the run button in your IDE's editor gutter, or run tests using Gradle tasks:

- Android tests: `./gradlew :shared:testAndroidHostTest`
- iOS tests: `./gradlew :shared:iosSimulatorArm64Test`

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…