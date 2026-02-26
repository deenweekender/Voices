# Voices

Android multi-module scaffold for a multi-model AI chat app.

## Current status

- Core modules and feature modules are scaffolded:
	- `app`
	- `core:common`, `core:network`, `core:database`, `core:ui`
	- `feature:auth`, `feature:chat`, `feature:history`, `feature:settings`, `feature:profile`
- Auth/chat/history/settings/profile have baseline Compose screens and ViewModels.
- Network and Room layers are initialized.

## Run on Android (Android Studio)

1. Open the project in Android Studio.
2. Let Gradle sync and install missing SDK components if prompted.
3. Create/select a run configuration for `app`.
4. Connect your Android phone (USB debugging enabled) or choose an emulator.
5. Click **Run**.

## Run on Android (CLI)

If this is a fresh container/machine, set up Android SDK first:

```bash
bash scripts/setup-android-sdk.sh
```

If `gradlew` is missing in your clone, generate it once:

```bash
gradle wrapper
```

Then build/install debug:

```bash
./gradlew :app:assembleDebug
./gradlew :app:installDebug
```

APK output path:

`app/build/outputs/apk/debug/app-debug.apk`

## Tests and CI

- Added unit tests:
	- `core/network` (`SseParserTest`)
	- `feature/auth` (`AuthViewModelTest`)
	- `feature/chat` (`ChatViewModelTest`)
- Added GitHub Actions workflow: `.github/workflows/android-ci.yml`

Run tests locally:

```bash
./gradlew :core:network:testDebugUnitTest :feature:auth:testDebugUnitTest :feature:chat:testDebugUnitTest
```

## iPad support

This repository is an Android app and cannot run natively on iPad.

Your options are:

- Build a separate iOS app (Swift/SwiftUI).
- Build a shared cross-platform app (KMP/Flutter/React Native) and port features.
- Expose this backend via web and use Safari on iPad.