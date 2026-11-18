# OBRA Android app

The Kotlin Android Views frontend is connected to local SQLite storage. Add/Edit, task checkboxes, work progress and received payments save real records. Home, History, Insights and Reconnect read those saved records. A fresh install starts with an empty workspace and seven commission types.

## Open and run

1. Open the `OBRA/` folder containing `settings.gradle.kts` in Android Studio.
2. Configure Android Studio's bundled JBR as the Gradle JDK.
3. Install SDK Platform 37 and allow Gradle dependency resolution.
4. Run on an emulator or phone with API 26 or newer. Medium Phone is the reference device.
5. Add a fictional commission, restart the app and verify it remains.

Current tooling uses AGP 9.5.1, Gradle 9.6.0, Java source/target 17, AppCompat 1.8.0 and Material Components 1.14.0. The local builds use Android Studio's JBR. Fonts and their OFL licenses are bundled.

## Data and backend

`ObraApplication` owns one helper and one serial database worker. Screens use complete in-memory snapshots and refresh after confirmed writes. `SqliteStudioRepository` validates fresh database state inside transactions. Monetary values use integer centavos. Work completion, task state and payment state are independent.

On the local `final-enhancements` branch, the database is `obra.db`, version 3. Ordered upgrades preserve existing commissions, references, and receipts, and add an artist artwork-update gallery. Sketch/Lineart/Colors/Finished uploads are separate from client references; newest uploads lead artwork previews, while payment History prefers the latest Finished image. Images use persisted content URIs; managed app-file references remain supported. Image decoding runs off the UI thread with bounded sizes. See [the local usability guide](docs/final-enhancements.md) for the revised date, pricing, completion, and History flows. These changes have not been pushed.

Retired types and genres are excluded from new choices. Historical records keep their lookup IDs. New primary genres are resolved from trimmed text. The existing PHP, single-genre and exact-contact defaults are documented in the integration guide.

## Verification

```text
gradlew.bat assembleDebug testDebugUnitTest lintDebug connectedDebugAndroidTest
```

The instrumentation runner uses a separate database for Activity tests. Gradle is configured to keep the app installed after connected tests. The test document provider is packaged only in the test APK. Tests cover transactions, migrations, financial rules, UI flows, refresh recovery, draft recreation, reference access and bounded image decoding. See the current test summary in [backend-integration.md](docs/backend-integration.md).

The shared XML shell and Kotlin view composition implement the screens. Android Studio's XML preview displays the shell. Device appearance and larger-text checks are listed in the walkthrough.

## Handoff documents

- [Backend starting guide](docs/backend-start-here.md)
- [Database handoff](docs/DATABASE_HANDOFF.md)
- [Frontend integration contract](docs/frontend-handoff.md)
- [Function reference](docs/function-reference.md)
- [Backend implementation and verification](docs/backend-integration.md)
- [Manual checks](docs/manual-checks.md)

Cancellation, refunds, receipt corrections, destructive deletion and structured quote snapshots are deferred product scope. The existing agreed backend is the implementation target. Source, resources, tests, wrapper and licenses belong in Git. APKs, local databases, machine configuration and generated output belong outside source commits.
