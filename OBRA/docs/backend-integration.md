# Backend integration

Implemented locally on November 18, 2026 against `frontend-handoff` at `3b74494`.

## Runtime

`ObraApplication` owns one `ObraDatabaseHelper`, `SqliteStudioRepository`, and serial database worker. Database initialization, reads, validation and writes execute on that worker. Activities consume a complete in-memory `StudioSnapshot`, published on the main thread after a consistent database read. The production repository starts empty and loads saved data. Fictional fixtures remain available to existing unit tests.

`BaseActivity` refreshes saved state when a screen resumes. It disables controls during reads/writes, exposes loading and retry for initial read failures, keeps form input on failed writes, and refreshes storage after successful writes. Write success and refresh failure are reported separately. Destroyed Activities ignore callbacks.

## Connected operations

- Add saves the resolved client, optional genre, commission, tags, reference metadata, two checklist items and optional initial receipt in one transaction.
- Add and payment tokens survive form recreation. Fingerprints reject conflicting token reuse. A successful repeated submission returns the existing result. Form recreation can recover an already committed Add/payment using the saved token.
- Edit reloads display name, tags, brief, background selection and references. It updates only request fields and reference/tag children. Revision checks reject stale request edits. Fresh receipt totals and dates are checked inside the transaction.
- Start, stage changes, completion and task checkboxes update saved records. Completion leaves payment and checklist states independent.
- Payment saves validate positive integer centavos, actual calendar dates, commission date and the latest remaining balance inside the write transaction. Concurrent writes cannot overpay through this repository.
- Home, commission lists, Details, payment history, main History and Insights consume the same saved snapshot. Receipt aggregation happens without joining attachment/task rows.
- Reconnect implements the guide's proposed exact type-and-primary-genre baseline over saved keys. It excludes the selected client, returns the latest matching completed artwork per client, and limits results to five clients in stable recent-first order. The algorithm team can replace this read policy through `StudioRepository`.
- Reference selection retains content URI read permissions. Before saving, image headers are checked on the worker. Missing grants or unreadable/invalid images reject the save while retaining the draft. Details passes saved reference locations to the existing viewer.

## Schema version two

Version-one rows and IDs are preserved by an explicit upgrade that adds three columns.

| Table | Column | Purpose |
| --- | --- | --- |
| `commissions` | `background_included` | Persist the existing form checkbox |
| `commissions` | `creation_fingerprint` | Identify the original Add payload across retries and later edits |
| `payments` | `submission_fingerprint` | Detect payment-token reuse with different input |

Both fresh creation and upgrade run this migration. Existing rows get a false background flag and null fingerprints. The backend requires nonblank tokens for new Add/payment operations. Existing nullable columns are retained for compatibility with version one. The original eight tables remain in use.

## Defaults adopted for integration

- Client reuse uses exact platform and trimmed username. Existing duplicate identities resolve to the lowest ID. Contacts on X and Discord remain distinct.
- One optional primary genre is resolved by exact trimmed label. The free-text form creates a lookup row when that label is new. Comma-separated style tags are stored independently.
- Default tasks are the existing frontend's Finish sketch and Finish lineart labels.
- The currency is PHP, stored as `Long` integer centavos. Receipt dates determine reporting periods.
- Completed means the artist confirms an actual finish date. Completed work may retain a balance and unfinished tasks.
- Unfinished-task summaries include non-cancelled work, including completed artwork, following the current native frontend and layout guide.
- Receipts have no edit/delete workflow. Cancellation, refunds, destructive deletion and structured quote snapshots still require product decisions. New payments on cancelled work are blocked pending that policy.
- References use durable content URIs. Existing private APP_FILE references also round-trip safely. Unchanged reference captions and display names are preserved. Removing a reference removes its metadata on confirmed Edit. Source files and grants shared with other records are retained.

## Verification

Run from `OBRA/` with the installed Android SDK and JDK configured.

```text
gradlew.bat assembleDebug testDebugUnitTest lintDebug connectedDebugAndroidTest
```

Verified on Medium Phone, Android 17. Build and lint passed with zero lint errors and seven warnings. All 13 JVM tests and 21 Android instrumentation tests passed. The instrumentation checks cover database creation, populated version-one upgrade, persistence after reopen, rollback, retry conflicts, concurrent payments, retired lookups, request edits, completion independence and Reconnect deduplication. An Activity test uses the actual four-step Add form, recreates it mid-draft, saves, and checks the resulting persisted commission and Details navigation. Activity tests use a separate application database, preserving the ordinary emulator workspace. The test-only reference provider runs in its own test-APK process.

Full visual acceptance, large-image gesture behavior and the complete device walkthrough remain separate from these backend checks. The existing `manual-checks.md` provides that walkthrough.

## Correction pass

The user selected completion of the existing backend scope. Cancellation/archive, refunds, receipt correction/deletion and structured quote snapshots remain deferred.

- New forms load active type choices and active genre suggestions. Repository writes reject a newly selected retired lookup and allow an existing historical lookup to remain on Edit.
- Reconnect retains an unavailable-selection state until the artist chooses another current commission explicitly.
- A loaded screen retains previous information and provides Retry refresh after a read failure. Confirmed writes followed by refresh failure are labelled Saved successfully with recovery.
- Payment method/date and completion-date changes participate in discard confirmation. Original draft baselines survive recreation.
- Main-section filter/month/selection and per-tab scroll positions survive Activity recreation and return navigation.
- The image viewer performs background sampled decoding with a maximum dimension of 2048 pixels and bounds gesture panning. Persisted-URI save/reopen, reference Edit round-trip, revoked access and private-file metadata are tested.
- The queued-write tests recreate Add and payment forms while their operations await the database worker. Both recover committed results exactly once.
- Screen tests exercise Edit, payment, progress/completion, task writes, reporting, read retry, failed-refresh recovery, stale Reconnect selection and scrolled report recreation.
- The migration fixture includes lookup/client relationships, a receipt, reference metadata/caption, style tags and completed tasks. It verifies foreign-key and integrity diagnostics after upgrade.

The app package is version 0.2.0-backend, code 3. The schema remains version 2. Build, lint and the complete test suite pass on Medium Phone with Android 17. Lint retains seven warnings and zero errors. Cross-device visual acceptance remains a team walkthrough item.

## Connected-test installation retention

`android.injected.androidTest.leaveApksInstalledAfterRun=true` keeps the debug app and normal workspace after connected tests. This setting was checked with the three database instrumentation tests, followed by checks that the app package and ordinary `databases/obra.db` still existed. Activity test operations continue to use their separate database.
