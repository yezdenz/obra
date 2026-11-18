# OBRA SQLite Database Initialization

## Scope

The Android app initializes a local SQLite database through `ObraDatabaseHelper`, a native `SQLiteOpenHelper`. `ObraApplication` opens the helper during application process startup, then closes it after initialization and logs the database filename, schema version, private file path, file size, foreign-key status, and initialized table names. Initialization failures are logged and rethrown. This layer owns schema creation, lookup seeding, foreign-key configuration, and schema-version handling. It does not implement application repositories, feature CRUD operations, application queries, business workflows, or frontend integration.

The database is named `obra.db` and starts at schema version 1. No database file existed before this implementation, so version 1 is a fresh schema. Future upgrades must add explicit, ordered, non-destructive migrations. Unsupported migrations and downgrades fail rather than dropping tables or data.

## Tables

Version 1 creates these tables:

- `clients`: platform, username, optional display name and notes.
- `commission_types`: stable lookup IDs, names, optional default amount, and active flag.
- `genres`: stable lookup IDs, names, and active flag. No genre labels are seeded because the approved vocabulary is not defined.
- `commissions`: client/type/optional genre relations, request details, agreed minor-unit amount, lifecycle/stage labels, dates, optional idempotency token, and metadata timestamps.
- `commission_tags`: style or other labels associated with a commission.
- `attachments`: reference/final attachment metadata and a content-URI or app-file location. Image bytes are not stored in SQLite.
- `payments`: individual received amounts, dates, methods, optional purpose/note, and optional idempotency token.
- `checklist_items`: commission task labels, order, and completion state.

`quote_lines` is intentionally omitted. The UI currently records an agreed total and a detailed-background flag, but does not provide structured quote line data. Add that table in a later migration if package/add-on snapshots are approved and the backend contract supplies them.

## Lookup seeds and assumptions

The helper seeds the seven commission-type labels currently offered by the native form: Full body, Half body, Chibi, Portrait, Full illustration, Character sheet, and Icon. Default prices are null because the form’s suggested prices are not approved package-price records. Inserts ignore an existing name so reopening/initializing the database does not duplicate lookup values.

No genres are seeded. `Fantasy` appears in examples but is not a complete approved list. The backend must resolve text entered by the current UI to an approved genre row or coordinate a UI/data-contract change.

The schema stores amounts as integer minor units, matching `Validation.minor()` and the current PHP display. It does not impose a currency column because the current product proposes one workspace and PHP only; confirm that before expanding to multiple currencies.

## Deliberately deferred policy

No triggers or financial views are created. In particular, the initializer does not decide payment-purpose derivation, overpayment rules, receipt corrections/refunds, cancellation workflows, or destructive deletion. The database has ordinary relational and value-shape constraints, and foreign keys use `RESTRICT` to avoid orphaning related data. Business validation and transactional operations belong to the backend teammate.

Dates use ISO `YYYY-MM-DD` text and basic shape checks. SQLite shape checks cannot reject every invalid calendar date, and this helper does not validate “today” or enforce business date transitions. The backend remains responsible for those checks.

Client `(platform, username)` identity normalization is not enforced as a unique rule because exact identity and case handling are still open. An index supports lookups without committing the product to a uniqueness policy.

## Verification

`app/src/androidTest/java/com/obra/studio/data/local/ObraDatabaseHelperTest.kt` checks schema creation and persistence in a uniquely named disposable database, performs temporary client create/read/update/delete checks, and confirms an invalid foreign key and invalid platform value are rejected. It separately checks that the production database created by application startup exists, is nonempty, has the expected schema and foreign-key setting, and opens again after closing, without inserting or deleting production records. Disposable test databases are deleted afterward.

Run it on an emulator with:

```text
gradlew.bat :app:connectedDebugAndroidTest
```

The test does not use or delete the production `obra.db` file.
