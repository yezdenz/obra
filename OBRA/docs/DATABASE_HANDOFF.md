# OBRA database handoff

The app uses SQLite through `ObraDatabaseHelper` and the connected `SqliteStudioRepository`. The local `final-enhancements` branch uses schema version 3. The root SQL guide describes the original proposal. This handoff describes the implemented storage contract; the local follow-up has not been pushed.

## Ownership

`ObraApplication` owns one helper and one serial worker. Database initialization and operations run on that worker. Activities read a complete `StudioSnapshot` published on the main thread. Save operations use the same helper and own their transaction boundaries.

## Tables

| Table | Stored information |
| --- | --- |
| `clients` | X/Discord identity and optional client metadata |
| `commission_types` | Stable type IDs, names, optional defaults and active flags |
| `genres` | Stable primary-genre IDs, names and active flags |
| `commissions` | Request, client/type/genre links, agreed price, lifecycle, dates, background flag and retry fingerprint |
| `commission_tags` | Style labels linked to a commission |
| `attachments` | Reference/final locations, storage kind, image metadata, caption and order |
| `payments` | Received amount/date/method/purpose, note, retry token and fingerprint |
| `checklist_items` | Task label, completion state and order |
| `artwork_updates` | Artist-uploaded Sketch/Lineart/Colors/Finished versions, parent commission, location/storage/MIME, UTC creation time, and retry token/fingerprint |

Foreign keys use RESTRICT. Client identity has a lookup index. Tokens are unique when present. New backend submissions require nonblank tokens and retain their original payload fingerprints. Amounts are positive integer minor units. Basic status, stage, method and date-shape checks are in the schema. Calendar and business transition checks are in repository methods.

## Lookup behavior

The helper seeds Full body, Half body, Chibi, Portrait, Full illustration, Character sheet and Icon. Default prices are null. New forms read active types and offer active genre suggestions. New writes reject inactive lookup rows. Edit can preserve its existing inactive type/genre IDs. Reconnect and historical display retain those IDs.

Client reuse uses the exact platform and trimmed username. Existing duplicate matches resolve to the lowest ID. A request's optional display name is stored on that commission. One optional primary genre is resolved by exact trimmed label. Style tags are separate.

## Version-one upgrade

The explicit 1-to-2 migration adds three columns.

- `commissions.background_included` defaults to zero and stores the existing background checkbox.
- `commissions.creation_fingerprint` retains the original Add payload identity.
- `payments.submission_fingerprint` identifies repeated payment submissions.

The eight original tables and historical IDs are preserved. The upgrade test includes a client, type, genre, commission, receipt, attachment/caption, style tag and completed checklist item, followed by foreign-key and integrity checks.

## Transaction rules

Add resolves client/lookups and saves the commission, tags, references, default tasks and optional receipt together. Initial receipt failure rolls the whole group back. Edit updates permitted request fields and reference/tag children while preserving work state, tasks and receipts. Existing attachment captions and names survive an unchanged-reference round trip.

Record payment first resolves its retry token, then checks fresh balance and dates inside the transaction. Completing artwork updates its actual finish and work state independently of receipts and tasks. Reports aggregate the saved receipts without joining multiple child collections into a multiplied sum.

## Artwork and reference storage

Version 3 adds `artwork_updates` and its parent/newest-ID index without recreating or modifying existing financial/reference rows. `addArtwork` validates the image and durable access, then inserts one idempotent version. Reusing a token with different input is rejected. The snapshot orders versions by ID descending. Current-work previews use the newest version; receipt rows prefer the newest Finished upload and fall back to the newest version. Older FINAL attachment rows are also readable as a fallback. Request edits replace only REFERENCE attachments, never artwork versions. Uploads do not change lifecycle, stage, or payments.

The detailed-background flag remains readable for compatibility but is no longer exposed as a checkbox or a fixed surcharge. Prices are entered by the artist; no new automatic rate is imposed.

New picker references retain persistable content-URI read grants. Supported image headers are checked on the worker before saving. Existing APP_FILE references resolve strictly inside app-private files storage. Missing references remain represented so the commission stays readable. An invalid reference can be removed in Edit before saving.

The viewer decodes images on a background worker and samples large images to a maximum dimension of 2048 pixels. Unavailable images expose Retry image. Source files and potentially shared grants are retained when attachment metadata is removed.

## Inspection and tests

Use App Inspection with the running debug process and `obra.db`. Production stores data in its private database directory. Activity tests use `obra-instrumentation-ui.db`, which is separate from that workspace. Repository tests use unique disposable databases. A document provider exists only in the test APK for persisted-URI and permission-loss checks.

Build, lint, JVM and Android instrumentation commands and results are maintained in [backend-integration.md](backend-integration.md).

## Deferred scope

The user selected completion of the existing backend scope for this pass. Cancellation/archive, refunds, receipt correction/deletion, structured quote snapshots and currency expansion require separate product decisions. The current implementation has no quote-line table, financial view or payment triggers. All current application writes go through repository transactions. Review database safeguards again if another write path is introduced.

## Connected-test installation retention

`android.injected.androidTest.leaveApksInstalledAfterRun=true` keeps the debug app and normal workspace after connected tests. This setting was checked with the three database instrumentation tests, followed by checks that the app package and ordinary `databases/obra.db` still existed. Activity test operations continue to use their separate database.
