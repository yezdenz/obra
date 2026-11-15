# OBRA frontend implementation handoff

This document describes the initial frontend draft as built, not the finished product. Review it again after device testing and UI revisions before treating it as the final backend handoff. The source references are `layout.md`, `database-guide.md`, `reconnect-algo.md`, the saved boards, and the matching local PDF from repository snapshot da93933.

## File ownership

- `ui/BaseActivity.kt`: shared shell, typography, buttons, cards, field/date controls, insets, navigation helpers, and pending-save feedback.
- `ui/MainActivity.kt`: Home, Commissions, History, Insights, Reconnect, and All tasks.
- `ui/DetailActivity.kt`: one commission selected by stable ID, its brief, dates, checklist, money summary, and contextual actions.
- `ui/FormActivity.kt`: four-step Add/Edit, review, payment form/history, progress, and completion confirmation. Owns unsaved draft state.
- `ui/ViewerActivity.kt`: selected local-image viewing, pinch/button zoom, pan, fit, and image paging.
- `data/Models.kt`: read/write contracts, DTOs, fixture provider, exact money parsing, and schedule validation.
- `res/layout/activity_shell.xml`: header, scrolling content area, footer. Content is native Kotlin Views using Material buttons rather than a WebView.
- `res/font/`: bundled fonts. `res/values/styles.xml`: light theme. The first pass still contains English labels and color values in Kotlin; resource extraction is a follow-up before localization.

## Navigation contract

Main sections share one Activity and preserve tab scroll positions within the instance. Contextual screens use explicit Intents:

- Detail: `commissionId: Long`.
- Form: `mode: String` (`add`, `edit`, `payment`, `payments`, `progress`), `commissionId: Long` when editing an existing record.
- Viewer: `images: ArrayList<String>` of content URI strings and `index: Int`; sample Details supplies `commissionId` and shows a placeholder because fixtures contain no artwork files.

Back returns to the originating Activity. Form steps retain their inputs; changed forms ask before discarding. Drafts and payment/progress fields are stored in Activity saved-instance state. They are not a persistent draft database. Reconnect selection and reporting month are independent.

## Read integration

Implement `StudioRepository` and replace `Studio.repository = Fixtures` at application startup. The current interface is synchronous for fixtures. Run real DB reads off the UI thread, then expose loading, result and error states to the Activities (for example through ViewModels). Do not call disk I/O directly from the rendering functions.

- `commissions()` supplies the joined display records.
- `payments()` supplies received receipts.
- `recommendations(selectedCommissionId)` supplies the algorithm team's results. Current fixture behavior only returns a fixed example for commission 2; it is not a matching implementation.

Use `Long` keys and integer centavos throughout the backend. Business dates use ISO `YYYY-MM-DD`; the sample dates are relative to the device date. Convert the UI's friendly status/type/method labels to the agreed database enum/lookup keys explicitly. Display names are not database IDs.

## Write integration

`StudioWriter` declares the intended entry points but is not implemented or called by this draft:

- `saveCommission(draft, submissionToken): Long`
- `updateCommission(id, draft)`
- `recordPayment(commissionId, amountMinor, receivedOn, method, note, submissionToken)`
- `startCommission(id, startedOn)`
- `updateProgress(id, stage, finishedOn)`
- `setTaskDone(commissionId, taskId, done)`

Replace the validated `pending()` call at each action with the appropriate asynchronous writer operation. Disable submission while saving. Retain draft input on error. Only after confirmed success should Add open the returned ID, Edit return to that record, and payment save refresh its history. Generate submission tokens once per draft and reuse on retry. Add plus related records/optional receipt must commit in one transaction.

`CommissionDraft` carries platform, username, display name, title, type, primary genre, style tags, brief, price text, commission/due/planned-start dates, optional initial-payment fields, detailed-background selection, and reference content URIs. Parse price text with `Validation.minor()` before writing. Resolve clients/lookups and quote snapshots in the backend rather than storing display strings as keys.

No deletion or cancellation method is invented here; historical deletion/refund policy remains unresolved in the source guides. Confirm that policy before adding destructive controls. Starting, completing, and checking tasks must never insert payments automatically.

## What works in the frontend

Native navigation, filters and search, four-step forms and review, required-field/money/date checks, received-payment previews, reporting-month selection, six-month bars and type summaries, image selection and viewing, progress/completion forms, and fixed recommendation cards. Save/task/start controls deliberately stop at integration feedback. They do not mutate the fixtures or pretend to persist records.

## Remaining before final acceptance

1. Run on the reference emulator and compare each layout with the boards. Colors/fonts/rounded surfaces are implemented; exact spacing and visual fidelity are not certified. Some controls are native dropdowns and the first navigation bar uses text labels.
2. Verify image decoding for large files and bound gesture panning. Samples have placeholders. Persistable URI permissions are acquired when supported, but the backend must define copying, cleanup and long-term storage.
3. Finish granular field-local error presentation, full before/after edit comparison, per-screen loading/error/retry presentation, and durable scroll restoration through Activity recreation.
4. Add database success transitions, stale-result handling and disabled saving states during backend integration.
5. Move remaining UI strings/colors/dimensions to shared Android resources, and optionally split composed screen bodies into XML layouts for Design-tab editing.
6. Confirm the unresolved genre, cancellation/refund and completion policies before finalizing this handoff.

## Validation evidence

Clean-copy build and Android lint passed. Thirteen JVM tests cover exact centavos, invalid/overflow/nonpositive input, calendar validity, date ordering, optional planned start, completed-but-unpaid and paid-but-unfinished examples. These do not replace the course's documented device test cases. No device or emulator execution is claimed.
