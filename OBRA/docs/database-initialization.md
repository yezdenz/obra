# OBRA database initialization

`ObraApplication` creates one `ObraDatabaseHelper` and opens it on its serial database worker. `SQLiteOpenHelper` creates the database for a fresh installation and upgrades populated version-one installations to version 2. The helper remains owned by the application process.

## Schema

The original eight tables remain in use. They store clients, commission types, genres, commissions, style tags, attachments, received payments and checklist items. Foreign keys are configured for each connection. Seven commission types are seeded for a fresh database. Primary genres are created from the form's trimmed labels as needed.

Version 2 adds `background_included` and `creation_fingerprint` to commissions and `submission_fingerprint` to payments. Existing IDs, contacts, receipts, lookup relations, tags, attachments and checklist states are preserved. Existing background values default to false and legacy fingerprints are null.

Future schema changes require ordered migrations and populated-upgrade tests. Unsupported upgrades and downgrades fail while preserving stored data.

## Operations and inspection

`SqliteStudioRepository` performs application reads and writes through this helper. It enforces payment limits, stale-edit checks, submission fingerprints and actual-date rules inside the appropriate write transactions. Detailed behavior is in [backend-integration.md](backend-integration.md).

With the debug app running, use Android Studio App Inspection and select `com.obra.studio`, then `obra.db`. The usual private path is `/data/user/0/com.obra.studio/databases/obra.db`.

```sql
PRAGMA foreign_keys;
PRAGMA foreign_key_check;
PRAGMA integrity_check;
SELECT name, is_active FROM commission_types ORDER BY id;
```

## Verification

The instrumentation suite checks initialization, foreign keys, constraints, persistence and a populated version-one upgrade. Activity tests replace only the database filename through their test Application so the developer's real emulator workspace remains intact. The ordinary app still uses `obra.db`.

Quote lines, SQL financial views and receipt/cancellation triggers remain deferred under the database handoff. Current financial and transition rules are enforced by the repository. Review these choices when adding new write paths.
