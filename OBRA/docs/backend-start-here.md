# Backend team starting guide

This is the frontend integration handoff for OBRA. The native Android source is ready for backend development against sample data; complete database-driven functionality and full frontend acceptance are still pending. Source files are authoritative. Existing GitHub design guides describe intended behavior and proposed policies.

## Read in this order

1. [Project setup and dependencies](../README.md)
2. [Frontend implementation and integration handoff](frontend-handoff.md)
3. [Function reference](function-reference.md)
4. [Checkbox verification walkthrough](manual-checks.md)
5. [Progress and outstanding work](frontend-progress.md)
6. Existing repository root guides: database-guide.md, layout.md and reconnect-algo.md. Preserve these when adding the Android project to the existing yezdenz/obra repository.

## Present state

- Kotlin native Android Views, shared XML shell, four Activities with explicit Intent-based navigation. Screen content is primarily constructed in Kotlin.
- Five main sections: Home, Work/Commissions, History, Insights and Reconnect; contextual Add/Edit, Details, tasks, payments, progress, completion and image viewing.
- Fictional data comes from Fixtures through StudioRepository. StudioWriter is a proposed interface without an implementation or an injected writer instance.
- Validated save/start/task actions show pending-integration feedback. They do not persist or mutate records. Reconnect uses fixed results.
- The user ran the earlier draft on a Xiaomi 14T and reported the system-style confirmation popup. Shared app dialogs were subsequently themed; no completed checkbox walkthrough or full device test report has been supplied.
- Earlier clean-copy verification passed assembleDebug, lintDebug and 13 JVM tests using AGP 9.4.1. Current source declares AGP 9.5.1 and Gradle 9.6.0; validate this current combination before integration. Existing deliverables are snapshots and may differ from current source.

## First integration milestone

- [ ] Agree on IDs, enum/lookup mappings and transaction boundaries using Models.kt and database-guide.md.
- [ ] Implement SQLite storage, seed only lookup values, and provide joined display data.
- [ ] Replace the fixture provider using background database operations and observable screen state. Current Activities render synchronously and will need refresh wiring.
- [ ] Introduce the real writer and replace the validated pending() call in Add with one transactional save of the commission and related records.
- [ ] Generate one submission token per draft and reuse it for retries. Disable Save while pending; keep input on failure.
- [ ] On success, open the returned commission ID and refresh the list.
- [ ] Close/reopen the app and verify the saved commission remains.
- [ ] Integrate Edit, payments, task toggles and work progress in small changes; refresh every affected screen after success.
- [ ] Connect aggregate queries and the algorithm team's Reconnect provider.

## Rules to preserve

Use Long integer centavos, stable internal record IDs and ISO calendar dates. Keep receipt dates independent from deadlines. Artwork completion must not mark receipts paid or tasks done. Prevent overpayment and a price below received money. Preserve drafts on validation/read/write failures. Do not perform DB work on the UI thread.

Deletion, cancellation, refunds and receipt correction are unresolved product policies; no corresponding writer methods currently exist. The course requires complete CRUD, so resolve the intended deletion workflow before claiming compliance. Image copying/URI durability, migrations, async error states and full changed-field summaries also remain implementation work.

## GitHub contents

Add the OBRA/ directory inside the existing repository, alongside its current planning Markdown and docs/ directory. Open OBRA/ in Android Studio. Include source, resources, manifests, Gradle scripts and wrapper, licenses, tests and these documents. Do not add local.properties, .idea/, .gradle/, generated build directories, keystores or local databases. APK/ZIP downloads can be attached to a GitHub Release rather than committed as editable source.

The separate obra-review/ folder is a local clone of the existing GitHub repository. Do not upload that folder as a nested repository. Do not upload verification-path.txt or transfer manifests as project source.
