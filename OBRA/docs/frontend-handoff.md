# OBRA frontend implementation handoff

The frontend is connected to SQLite. This document describes screen ownership and the current integration contract. The design references remain `layout.md`, `database-guide.md`, `reconnect-algo.md`, the saved boards and the partner PDF. See `backend-integration.md` for current verification and deferred scope.

## File ownership

- `ui/BaseActivity.kt`: shared shell, typography, buttons, cards, field/date controls, insets, navigation helpers, and refresh retry feedback.
- `ui/MainActivity.kt`: Home, Commissions, History, Insights, Reconnect, and All tasks.
- `ui/DetailActivity.kt`: one commission selected by stable ID, its brief, dates, checklist, money summary, and contextual actions.
- `ui/FormActivity.kt`: four-step Add/Edit, review, payment form/history, progress, and completion confirmation. Owns unsaved draft state.
- `ui/ViewerActivity.kt`: selected local-image viewing, pinch/button zoom, pan, fit, and image paging.
- `data/Models.kt`: read/write contracts, snapshots, exact money parsing and schedule validation. Fixtures support isolated JVM tests.
- `res/layout/activity_shell.xml`: header, scrolling content area, footer. Content is native Kotlin Views using Material buttons rather than a WebView.
- `res/font/`: bundled fonts. `res/values/styles.xml`: light theme. The first pass still contains English labels and color values in Kotlin; resource extraction is a follow-up before localization.

## Navigation contract

Main sections share one Activity and preserve tab scroll positions within the instance. Contextual screens use explicit Intents:

- Detail: `commissionId: Long`.
- Form: `mode: String` (`add`, `edit`, `payment`, `payments`, `progress`), `commissionId: Long` when editing an existing record.
- Viewer: `images: ArrayList<String>` of content URI strings and `index: Int`; Details supplies `commissionId` and its saved reference locations.

Back returns to the originating Activity. Form steps retain their inputs; changed forms ask before discarding. Drafts and payment/progress fields are stored in Activity saved-instance state. They are not a persistent draft database. Reconnect selection and reporting month are independent.

## Read integration

`ObraApplication.refresh()` loads a consistent `StudioSnapshot` on the database worker and publishes it on the main thread. Activities render the snapshot through `StudioRepository`. `BaseActivity` refreshes on resume and exposes Retry refresh when a later read fails while keeping existing information visible.

The snapshot contains commissions, payments and active type/genre choices. Display labels map explicitly to database values. Screens use stable record IDs. Main-section tab, filter, month, selection and scroll positions survive Activity recreation. Reconnect requires an explicit new choice when its previous source is no longer current.

## Write integration

Each confirmed action calls `StudioWriter` through `BaseActivity.write()`.

- `saveCommission(draft, submissionToken)` returns the saved commission ID.
- `updateCommission(id, draft)` preserves receipts/tasks/progress and checks the original request revision.
- `recordPayment(...)` checks the latest balance and receipt date in a transaction.
- `startCommission(...)`, `updateProgress(...)` and `setTaskDone(...)` update their corresponding saved state.

Controls are disabled while saving. Failed writes retain entered input. Add opens returned Details after success. Edit and payment return to their parent screen. Read failure after a confirmed write is presented as a refresh issue. Tokens survive draft recreation and recover committed Add/payment results after a queued write.

Drafts retain contact, display name, title, type, primary genre, style tags, brief, price, dates, background selection and references. Dirty-form checks include payment date/method and completion-date changes. Receipt and work states remain independent.

## Reference viewer

The viewer loads saved locations, decodes sampled bitmaps on its background worker and handles missing images with Retry image. It preserves the current image index on recreation. Content URI grants and app-file safety checks belong to the repository integration.

## Acceptance scope

Use `manual-checks.md` for the full user walkthrough. Device appearance, keyboard, large-text layout and gesture usability still require device review across the team's supported phones. Cancellation, refunds, receipt corrections and destructive deletion remain deferred under the selected scope.
