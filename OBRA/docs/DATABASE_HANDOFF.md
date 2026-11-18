# OBRA SQLite Database Handoff

This document describes the database initialization work currently implemented in the OBRA Android app and the backend work that remains. The implementation details below are based on `app/src/main/java/com/obra/studio/data/local/ObraDatabaseHelper.kt`, `app/src/main/java/com/obra/studio/ObraApplication.kt`, the instrumentation tests, and the project handoff documents. The implementation is narrower than the proposed schema in the repository root `database-guide.md`; differences are called out below.

## 1. Overview

OBRA has a local SQLite database initialized with Android's native `SQLiteOpenHelper`. The implemented layer creates and versions the schema, enforces foreign keys, seeds established commission-type lookups, and opens the database during application startup so the schema is initialized.

This is an initialization layer only. It does not implement production repositories, feature CRUD operations, application queries, business workflows, or screen integration. The frontend continues to use fixtures and pending-integration feedback. The backend teammate owns those operations and the connection from the frontend to persistent data.

## 2. Architecture

- `app/src/main/java/com/obra/studio/data/local/ObraDatabaseHelper.kt` is the only database helper. It extends `SQLiteOpenHelper`, uses the application context, configures foreign-key enforcement, creates the schema, seeds commission types, and owns the schema version callbacks.
- `app/src/main/java/com/obra/studio/ObraApplication.kt` creates the helper and opens `writableDatabase` during `Application.onCreate()`. The helper is closed after initialization completes. Initialization logs the filename, schema version, private path, file size, foreign-key state, and table names. On initialization error it closes the helper, logs the failure, and rethrows.
- `app/src/main/AndroidManifest.xml` registers `.ObraApplication` as the app class.

The helper currently has no repository-facing accessor. Agree on how repositories will obtain/use the application-owned helper or database before implementing backend operations. Avoid introducing a second competing database or helper.

## 3. Database schema

Database filename: `obra.db`. Schema version: `1`.

### Tables and columns

| Table | Columns and keys |
| --- | --- |
| `clients` | `id INTEGER PRIMARY KEY`; `platform TEXT NOT NULL`; `username TEXT NOT NULL`; nullable `display_name`, `notes`. |
| `commission_types` | `id INTEGER PRIMARY KEY`; `name TEXT NOT NULL UNIQUE`; nullable `default_price_minor INTEGER`; `is_active INTEGER NOT NULL DEFAULT 1`. |
| `genres` | `id INTEGER PRIMARY KEY`; `name TEXT NOT NULL UNIQUE`; `is_active INTEGER NOT NULL DEFAULT 1`. No genre rows are seeded. |
| `commissions` | `id INTEGER PRIMARY KEY`; nullable unique `creation_token`; required `client_id`, `type_id`; nullable `primary_genre_id`; nullable `client_display_name`; required `title`; nullable `brief`, `pose_notes`, `notes`; required `agreed_total_minor`; `status TEXT NOT NULL DEFAULT 'QUEUED'`; nullable `stage`; required `commission_on`, `due_on`; nullable `planned_start_on`, `started_on`, `finished_on`, `cancelled_on`, `cancellation_note`; `created_at`, `updated_at` default to UTC SQLite timestamps. |
| `commission_tags` | Required `commission_id`, `label`; composite primary key `(commission_id, label)`. |
| `attachments` | `id INTEGER PRIMARY KEY`; required `commission_id`, `kind`, `storage_kind`, `location`, `mime_type`; nullable `display_name`, `caption`; `sort_order INTEGER NOT NULL DEFAULT 0`. |
| `payments` | `id INTEGER PRIMARY KEY`; required `commission_id`, `amount_minor`, `received_on`, `method`; nullable unique `submission_token`, nullable `purpose`, `note`; `created_at` defaults to a UTC SQLite timestamp. |
| `checklist_items` | `id INTEGER PRIMARY KEY`; required `commission_id`, `label`; `is_done INTEGER NOT NULL DEFAULT 0`; `sort_order INTEGER NOT NULL DEFAULT 0`. |

### Relationships

- `commissions.client_id` references `clients.id` (required, `ON DELETE RESTRICT`).
- `commissions.type_id` references `commission_types.id` (required, `ON DELETE RESTRICT`).
- `commissions.primary_genre_id` references `genres.id` (optional, `ON DELETE RESTRICT`).
- `commission_tags.commission_id`, `attachments.commission_id`, `payments.commission_id`, and `checklist_items.commission_id` each reference `commissions.id` with `ON DELETE RESTRICT`.
- Foreign-key enforcement is enabled for each configured connection in `ObraDatabaseHelper.onConfigure()`.

### Constraints and indexes

- Nonblank checks apply to client usernames, lookup names, commission titles, tag/checklist labels, attachment locations, and applicable text values.
- `clients.platform` is restricted to `X` or `DISCORD`. `(platform, username)` has a non-unique lookup index; duplicate identity is currently allowed by the schema.
- Commission-type and genre names are unique. Their active flags and checklist `is_done` are restricted to `0` or `1`.
- Commission totals and payment amounts must be stored as positive integers. Commission status is one of `QUEUED`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`; stage is null or one of `SKETCH`, `LINEART`, `COLORS`, `FINISHED`. The current schema does not enforce lifecycle consistency between status, stage, and actual dates.
- Payment method is one of `GCASH`, `BANK_TRANSFER`, `PAYPAL`, `CASH`, `OTHER`. Payment received date, commission date, and due date have ISO date-shape checks; due date cannot precede commission date. Planned start and finish checks enforce basic ordering against other dates.
- Attachment kind is `REFERENCE` or `FINAL`; storage kind is `CONTENT_URI` or `APP_FILE`; MIME type must start with `image/`; attachment order is nonnegative and unique per commission/kind/order.
- Payment and commission submission tokens are unique when non-null. The schema currently permits null tokens.
- Named indexes: `idx_clients_identity`; `idx_commissions_status_due`; `idx_commissions_queue`; `idx_commissions_client`; `idx_commissions_reconnect`; `idx_payments_month`; `idx_payments_commission`; `idx_checklist_commission_state`; `idx_attachments_commission_kind`.

### Lookup data

The helper seeds `commission_types` with `Full body`, `Half body`, `Chibi`, `Portrait`, `Full illustration`, `Character sheet`, and `Icon`. Seed inserts ignore an existing name, and `default_price_minor` is null. No `genres` are seeded because the approved vocabulary is not established.

## 4. Initialization and persistence

On application process startup, `ObraApplication.onCreate()` opens the existing helper's writable database. `SQLiteOpenHelper` creates the file and runs `onCreate()` only for a new database; opening a compatible existing database does not recreate it. The current version is 1. Future schema changes need explicit ordered migrations in `onUpgrade()`. Unknown upgrade paths and downgrades throw rather than dropping tables or recreating data.

The app-private database path is:

```text
/data/user/0/com.obra.studio/databases/obra.db
```

The equivalent path commonly shown by Android tools is `/data/data/com.obra.studio/databases/obra.db`. App startup emits `ObraDatabase` log entries for initialization attempt and result. These report database metadata and schema table names, not user records.

## 5. Testing and verification

`app/src/androidTest/java/com/obra/studio/data/local/ObraDatabaseHelperTest.kt` contains three Android instrumentation tests:

1. Creates a uniquely named disposable database, verifies version 1, foreign-key enforcement, the eight expected tables and seven commission-type seeds; inserts a client and commission; closes/reopens; and verifies the commission and lookup data persist.
2. Uses a separate disposable database for client Create, Read, Update, and Delete checks. It confirms an invalid foreign-key reference and an invalid platform value are rejected. The disposable database is removed in test cleanup.
3. Checks that app startup created a nonempty production `obra.db`, opens it for verification, checks version, schema, and foreign-key setting, closes/reopens it, and checks persistence without inserting or deleting production records.

The instrumentation suite was executed on the `Small_Phone(AVD) - 17` emulator using:

```text
gradlew.bat :app:connectedDebugAndroidTest
```

The task completed successfully after the application-lifetime connection was added. This verifies the file and helper behavior; the test does not automate interaction with Android Studio's Database Inspector UI.

## 6. Database inspection

With OBRA running on an emulator, open **View ? Tool Windows ? App Inspection ? Database Inspector**, select the `com.obra.studio` process, and select `obra.db`. The startup initializer closes its helper after creating or opening the database. Database Inspector requires an open app connection; Commit 2 adds the application-lifetime connection for live inspection. If it is not listed, confirm the debug app process is running and refresh the inspector.

Device Explorer location:

```text
data/data/com.obra.studio/databases/obra.db
```

Useful inspection queries:

```sql
SELECT type, name, sql
FROM sqlite_master
WHERE type IN ('table', 'index', 'view', 'trigger')
  AND name NOT LIKE 'sqlite_%'
  AND name != 'android_metadata'
ORDER BY type, name;

PRAGMA foreign_keys;
PRAGMA foreign_key_check;
PRAGMA integrity_check;

SELECT * FROM commission_types ORDER BY id;
```

The initialization layer currently creates no views or triggers, so only tables and indexes should appear as application schema objects.

## 7. Backend integration responsibilities

The frontend remains fixture-backed: `Studio.repository` points to `Fixtures`, and `StudioWriter` is declared but not implemented or connected. Production integration still needs to:

- Implement database-backed reads for commissions, payments, and recommendations, then replace the fixture repository.
- Implement write operations for save/update commission, record payment, start work, update progress, and toggle tasks using the existing `StudioWriter` contract in `app/src/main/java/com/obra/studio/data/Models.kt`.
- Run database operations away from the UI thread; add loading, success, error, retry, and screen refresh behavior without losing drafts on failure.
- Resolve client and lookup labels to stable IDs and map frontend labels to agreed database values.
- Implement transactional Add/Edit/payment workflows and retry-token handling where the final backend contract requires them.
- Connect Activities at the documented action points in `docs/function-reference.md`; replace pending feedback only at actual write call sites.
- Decide how repositories access the initialized SQLite database. Keep schema initialization separate from feature-specific operations.

Suggested entry points from the frontend documentation are:

```text
StudioRepository: commissions(), payments(), recommendations(selectedCommissionId)
StudioWriter: saveCommission(draft, submissionToken): Long
              updateCommission(id, draft)
              recordPayment(commissionId, amountMinor, receivedOn, method, note, submissionToken)
              startCommission(id, startedOn)
              updateProgress(id, stage, finishedOn)
              setTaskDone(commissionId, taskId, done)
```

These names and signatures describe the existing frontend contract; they are not implemented database methods in `ObraDatabaseHelper`.

## 8. Important implementation notes and unresolved decisions

- Monetary columns use integer minor units (`Long` in Kotlin). The frontend's `Validation.minor()` parses positive price text exactly. Keep money out of floating-point types.
- Business dates use ISO `YYYY-MM-DD` strings. SQL checks validate basic shape/order, not every calendar date; use the existing application validation for actual calendar validity.
- SQLite field names use `snake_case`; Kotlin class/function names use the existing project conventions. IDs are internal keys, not titles or usernames.
- The implementation intentionally does not create `quote_lines`, triggers, or `commission_financials`. `database-guide.md` proposes them, but structured quote data and receipt/cancellation policies are not fully settled in the current frontend contract. Add such objects only through an agreed contract and a migration.
- The implementation differs from the proposed guide in other places: client identity is indexed but not unique; commission/payment tokens and payment purpose are nullable; payment purpose is not enum-constrained; payment guard and append-only triggers are absent; the `commission_financials` view is absent; and the helper's index names differ from the guide's sample SQL.
- No genre vocabulary is seeded. The guide describes one optional primary genre as a proposed baseline; confirm this and how UI-entered genres resolve to lookup rows.
- Cancellation, receipt correction/refunds, overpayment enforcement, quote snapshots, currency expansion, image URI durability/copying, and multi-contact client identity need backend/product decisions. Do not infer triggers or workflows from the proposed guide alone.
- The helper has no production CRUD APIs, repositories, or DAOs. Its responsibility ends at schema creation/versioning and lookup seeding.

## 9. Suggested next steps

1. Review the actual schema against `database-guide.md` and confirm the open choices above, especially quote lines, token nullability, payment purpose, and client identity.
2. Agree on one contract for lookup IDs, enum values, repository access to the application helper, and read/write transaction boundaries.
3. Implement repository-backed reads and asynchronous screen state, replacing fixture reads while preserving existing IDs and UI behavior.
4. Implement transactional Add/Edit and payment writes with validation, stable retry tokens, and draft retention after errors.
5. Connect Start, progress, completion, task toggles, reports, and Reconnect in small steps; refresh affected screens only after successful writes.
6. Add migration tests before raising the database version. Test upgrades from a populated prior-version fixture and preserve historical IDs and receipts.
7. Run the documented backend acceptance scenarios, then verify persistence and inspect schema integrity (`PRAGMA foreign_key_check`, `PRAGMA integrity_check`) on the emulator.
