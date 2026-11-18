# Backend team starting guide

The native frontend now reads and writes SQLite through the implemented backend. Begin with the current integration guide and source when changing a workflow.

## Read in order

1. [Project setup](../README.md)
2. [Current implementation](backend-integration.md)
3. [Database schema and migration](DATABASE_HANDOFF.md)
4. [Screen integration contract](frontend-handoff.md)
5. [Function reference](function-reference.md)
6. [Acceptance walkthrough](manual-checks.md)

Root `database-guide.md` remains the proposed design and acceptance reference. `reconnect-algo.md` documents the matching baseline and algorithm-team contract. Their proposed product decisions should be reviewed when the team changes scope.

## Connected responsibilities

- `ObraDatabaseHelper` owns schema version 2, foreign keys, lookup seeds and the ordered upgrade.
- `ObraApplication` owns the helper, background worker and snapshot publication.
- `SqliteStudioRepository` owns consistent reads and transactional commission/payment/task/progress operations.
- `StudioSnapshot` supplies display records and the documented Reconnect baseline using saved keys.
- Activities own drafts, loading/error/retry presentation, confirmations and success navigation.

Save related records together and retain the same token on retry. Use fresh financial values inside the transaction. Preserve existing receipts and actual progress during request editing. Database work and image decoding run off the main thread.

## Scope agreed for this pass

Finish the existing backend scope. Cancellation, archive/deletion, refunds, receipt corrections and structured quote snapshots remain deferred. This was selected by the user during the backend correction pass. If the course requires deletion for full CRUD, agree on a history-preserving design with the team and instructor before expanding scope.

## Collaboration

The database owner maintains schema/migrations and reviews storage changes. The backend owner maintains workflows, validation and screen integration. Review changes to the shared models and transaction boundaries together. Test the populated upgrade whenever the schema version changes.

Use fictional fixtures in tests. Activity tests use an isolated application database and a test-only document provider. Keep user databases and reference files out of Git.
