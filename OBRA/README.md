# OBRA Android frontend draft

Native Kotlin / Android Views initial draft, November 14, 2026. Open this folder in Android Studio. The five main sections and contextual forms use fictional data. Database operations are deliberately unconnected: validated Save actions explain that backend integration is pending and retain input. No real payment processing or client outreach occurs.

## Open and run

1. Extract the ZIP fully to a normal local folder, preferably `C:\AndroidProjects\OBRA`. Do not open the ZIP itself. A OneDrive generated-file lock was encountered during verification; a clean local copy built successfully.
2. In Android Studio Rabbit 2 2026.2.2 Patch 1, choose File > Open and select the `OBRA` folder containing `settings.gradle.kts`.
3. Allow Gradle sync and dependency downloads. Choose Android Studio's bundled JBR as the Gradle JDK under Settings > Build, Execution, Deployment > Build Tools > Gradle.
4. In SDK Manager, install Android SDK Platform 37 and Build Tools 36.0.0 if absent. Android Studio manages your local SDK path; `local.properties` is intentionally excluded from the ZIP.
5. Select an emulator or USB-debugging-enabled phone with Android 8.0 / API 26 or newer. Click Run. For the reference layout, use the Medium Phone profile, approximately 411 x 914 dp.

No extra Android Studio plugin, Perforce, or Subversion extension is needed. First sync may need an internet connection. Font files are included, so the app does not download fonts at runtime.

## Pinned toolchain

- Current source: Android Gradle Plugin 9.5.1 with built-in Kotlin. Previous build verification used AGP 9.4.1 (resolved stdlib 2.2.10); rerun checks for the current configuration. Do not add a separate Kotlin Android plugin without reviewing AGP 9 requirements.
- Gradle wrapper 9.6.0, included with its JAR and Windows/Unix launchers.
- compileSdk / targetSdk 37; minSdk 26. Build Tools 36.0.0 were used in verification.
- Java source/target 17. Builds used Android Studio's installed JBR; the user's Studio runtime is JBR 25.0.4. The IDE runtime version and source compatibility level are different settings.
- AndroidX AppCompat 1.8.0; Material Components 1.14.0; JUnit 4.13.2 for local tests. Their transitive dependencies resolve through Google/Maven Central.
- Bundled DynaPuff and Mali fonts, with OFL licenses in `licenses/`.

## Verification

On a fresh local copy, `gradlew.bat assembleDebug lintDebug testDebugUnitTest` passed. All 13 local tests passed. Lint has no errors; remaining warnings include English strings, backup-policy guidance, newer-toolchain notices, and image gesture accessibility guidance. These are not device UI acceptance results.

No emulator system image/AVD or connected device was available in this environment. Installation, touch navigation, keyboard/insets, rotation, large-text behavior, and pixel-level comparison remain to be verified on a device. The APK is a debug frontend preview, not a finished course submission.

## Reading order

- `docs/frontend-handoff.md`: screen ownership, data contract, backend integration and open work.
- `docs/frontend-progress.md`: resume checkpoint.
- `docs/manual-checks.md`: device walkthrough and expected outcomes.
- Original design sources: https://github.com/yezdenz/obra (reviewed snapshot da93933).

The first draft uses one shared XML activity shell and native Kotlin view composition for screen content. Individual layouts are composed in the Activity files; the XML Design tab will show the shell, not every populated screen. This is a valid native Android project, but separate per-screen XML resources remain a useful follow-up if the team prefers visual-layout editing.

## Team handoff

Start with [Backend team starting guide](docs/backend-start-here.md) for the documentation index, integration checklist, current verification limits and GitHub contents. See [Function reference](docs/function-reference.md) for source entry points.
