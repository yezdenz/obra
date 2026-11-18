# Local usability pass: final enhancements

Branch: `final-enhancements`. Based on the local `backend-integration` checkout. Requested to remain local; do not push, merge, or publish these changes without a later owner instruction.

## Why this changes the earlier wireframe

The owner found the working app confusing, especially the repeated Lineart/stage/task controls. This local pass makes the everyday workflow simpler while keeping existing saved records usable. The earlier boards and PDF remain the previous design reference; they have not been redrawn for this local experiment.

## The everyday flow

1. Add a commission. It joins **Queued**.
2. Open it and tap **Start commission** when work begins.
3. Work at your own pace. Open it and tap **Mark completed** when the artwork is ready.
4. Confirm a small **Finish this artwork?** dialog for today's finish date. **Keep working** cancels it; there is no extra completion page in the normal route. An earlier finish date remains available through the optional stage editor.
5. Record received money separately, whenever the client pays.

Updating Sketch, Lineart, and Colors is optional. The artist can finish directly from Sketch without clicking through intermediate stages. Finishing work does not mark its checklist complete or record money.

## What changed

| Area | Local behavior |
| --- | --- |
| Home | Monthly received earnings, still-to-collect total, two workload counts, current artwork ordered by deadline, and next queued artwork |
| Home action | Add commission stays in the bottom action area above the main navigation |
| Task clutter | Remove the repeated Home checklist/task count. Per-artwork checklist records remain under More details |
| Artwork cards | Show the main work status and a readable deadline, rather than competing drawing-stage labels |
| Details | Artwork identity, money summary, brief/references, and one fixed primary action appropriate to its status |
| Queued primary action | Start commission |
| In-progress primary action | Mark completed, using one compact confirmation dialog without changing screens |
| Completed but unpaid | Record payment remains the primary action |
| Completed and fully paid | Show a simple completion/paid message rather than an unusable payment action |
| Optional controls | More details & optional checklist expands dates, style/background details, optional stage editing, and task checkboxes |
| Expansion state | Retained during refresh and screen recreation |
| Payment actions | Grouped with the balance and received total; finishing work does not change those amounts |
| Empty references | Explain that no images were added; do not offer a preview button with nothing to preview |
| Edit / Close | Small header actions instead of large repeated body buttons |
| Intake review | Compact Edit links beside section headings; omit blank lines from the summary |
| Progress editor | Remains available under More details; uses Save stage / Review completion labels |
| Dates / months | Due today, Due tomorrow, overdue-day wording, short dates, and a readable reporting-month selector |
| Confirm dialogs | Ordinary confirmations use Cancel; only discard dialogs use Keep editing |
| Date entry | Separate Itim labels and readable dates with a calendar icon; a cream/plum Material calendar uses the OBRA fonts. Internal dates remain ISO strings |
| Pricing | Remove the detailed-background checkbox, fixed surcharge, suggested package quotes, and prefilled intake price. Enter the agreed price directly. Historical background flags are retained without imposing charges |
| Return to menu | Back to Home is always visible at the bottom of Details, including after completion |
| Artist artwork | Add artwork opens a separate image upload for Sketch, Lineart, Colors, or Finished; references remain separate |
| Latest preview | Home/Work and Details show the highest saved artwork-update ID first; tap the preview to browse earlier versions |
| History density | Compact tappable payment rows replace large View commission buttons; artwork thumbnails appear on the right |
| History image | Prefer the newest Finished image; if none exists, show the latest artist upload. Client reference images are not used as finished work |

## Data and compatibility

The follow-up adds database version 3 with the new `artwork_updates` table. The ordered version 2-to-3 migration only adds that table/index; existing commissions, references, receipts, task states, stage values, and lookup IDs remain in their existing tables. Version-one databases upgrade through both existing and new migrations. Recommendations retain their previous type/genre policy. The default checklist can still exist on new commissions; its controls remain optional. Checkbox changes affect only that checklist row.

An artwork update stores its parent commission key, stage label, image location/storage type/MIME, UTC creation time, and a unique retry token/fingerprint. Save validates lasting image access and image format. A matching retry returns the existing row; conflicting reuse is rejected. Images decode off the UI thread at bounded sizes. Uploading a Finished image does not complete work or create a receipt. Uploads remain available after completion. Reference edits do not replace artist uploads.

Home's still-to-collect amount includes completed-but-unpaid artwork and excludes cancelled artwork, matching the existing financial rules. Earnings continue to count received receipts by their received date. Opening or expanding a view never writes data.

The Add/Edit four-step flow, draft validation, receipt protections, and Reconnect section remain available. Old saved navigation state pointing to the removed Home task-list route returns safely to Home.

## Validation

Build/check commands from `OBRA/`:

```text
gradlew.bat assembleDebug testDebugUnitTest lintDebug assembleDebugAndroidTest
```

The command above builds the app and instrumentation APK, runs JVM tests, and checks lint. It does not install or launch anything. Do not start the app, emulator, or device tests without a later owner instruction.

`EnhancementWorkflowTest` covers a clean Home, optional checklist persistence/recreation, the real Details-to-completion route, completed-but-unpaid artwork, and cancelling direct completion. Existing regression checks are updated to expand the optional checklist and use the renamed stage actions. Instrumentation uses the existing separate test database. Visual review should use the Medium Phone reference, including longer titles and larger text.

The initial pass passed 13 unit tests and 25 device tests on November 18, 2026. The follow-up passed **13 unit tests and 31 device tests**, with a successful debug build and lint reporting zero errors/seven warnings. `ArtworkUsabilityTest` checks version ordering, finished-image selection, decoded thumbnails, picker results, draft/calendar recreation, returning Home after completion, and migration safety. Normal-size date fields, the themed calendar, upload preview, Home artwork, and compact History were visually inspected. Device verification used a separate review AVD and the existing isolated test database, not the artist's running emulator. Review screenshots stay outside Git under LocalAppData/OBRA/enhancement-review/artwork-pass.

Generated APKs remain outside the repository in the checkout-specific LocalAppData build folder. Do not add APKs, emulator data, or local review screenshots to Git.

## Grouped History and reference-thumbnail follow-up

The owner requested these additional local refinements:

- Main History now has one card per commission with payments in the selected month. The client display name/username/platform and artwork title identify it. Deposit and balance receipts stay under the same card, not separate commission entries. Different commissions are grouped by their internal keys, never by a shared client name/title.
- Each card's main amount counts that selected month only. If earlier/later receipts exist, an explicitly labelled all-time paid total is also shown. Up to two recent receipt lines appear inside a multi-payment card. Tap it for the complete per-commission history across all dates, then use View commission to open the artwork. Saved receipts and monthly earnings calculations have not been merged or changed.
- Single-receipt cards omit a redundant breakdown. Artwork previews use a black foreground outline so the border stays visible over the image on Home, Work, History, and other shared previews.
- Add/Edit shows references as three-column thumbnail tiles with one Add references button. Tap a tile to enlarge it. The red corner cross removes it from the draft, retaining the entered form fields. The visible dot is smaller than its accessible 48 dp touch target. Final Save is still required to persist an edited reference list; original image files are not deleted.
- Home uses a month-specific heading such as November earnings, removes the repeated Payments received/month caption, outlines the sage panel in yellow, and replaces the text Insights link with a labelled, keyboard-focusable chart icon.
- The owner subsequently approved Itim in place of Mali. [supporting-font-suggestions.md](supporting-font-suggestions.md) now records that applied decision. Itim is used for supporting text and app/calendar themes; DynaPuff remains the heading font.

Verification for this follow-up: debug and Android-test APK compilation succeeded, all **21 JVM tests passed** (including eight grouping/financial-scope cases), and lint reported zero errors/seven warnings. No emulator, app, or device tests were launched, following the owner's instruction. On-device layout review is left for the owner. The earlier device-test totals above describe the previous pass, not a new run for these changes.

## Approved icon and status refinements

The owner approved this native-app refinement after selecting Itim. The five main destinations keep their labels and routes:

| Label | Rounded outline icon | Destination |
| --- | --- | --- |
| Home | House | Dashboard |
| Work | Stacked artwork | Commission list |
| History | Receipt | Received payments grouped by commission |
| Insights | Chart | Earnings and workload analysis |
| Reconnect | Sparkles | Past-client recommendations |

The selected tab has a soft yellow background. Navigation remains keyboard focusable and announces its full destination name; Work announces Commissions. Labels stay visible underneath the icons.

- The shared empty-photo placeholder now uses an OBRA outline photo icon. The placeholder fits inside its frame; a successfully loaded photo fills/crops the preview, keeping the black outline visible.
- Add artwork, Add references, and the artwork picker buttons display the same photo-plus icon beside their existing text.
- Work cards and Commission details show a small status chip: soft yellow for Queued, lavender for In progress, and green for Completed. Cancelled uses a neutral chip. Every chip includes the status word. On work cards, the deadline stays on its own line so longer dates and larger text can wrap.
- The artwork/reference viewer replaces four large stacked buttons with a fixed row of five 48 dp minimum touch targets: Previous image, Zoom out, Fit image, Zoom in, Next image. Each has an accessibility label and long-press tooltip. The image counter stays above the preview. Pinch zoom and drag remain available; Fit resets zoom and pan. Zoom ranges from 1x to 4x. Zoom controls stay disabled until an image loads, and previous/next disable at the ends of the image list. Failed images retain Retry image.
- The font pairing remains DynaPuff for headings/actions and Itim for supporting copy. This pass changes presentation only; it adds no database migration, payment rule, or recommendation logic.

Verification: debug and instrumentation APK compilation succeeded, all **21 JVM tests passed**, and lint reported **zero errors/eight warnings**. Existing image-persistence checks now select the actual preview by its accessible description rather than counting every image/icon in the window; shared UI checks support labelled icon controls. These device checks were compiled only. No app, emulator, or device test was launched. Layout review on the owner's Medium Phone remains outstanding. Changes remain local on `final-enhancements`.

## Delete commission, including completed History

The owner approved permanently removing incorrect commissions, including completed work in History. This replaces the earlier handoff's no-commission-deletion policy for the current local native app.

- From **Commission details**, scroll to the red **Delete commission** action below the optional details section. It is available whether that section is expanded or collapsed, for every commission status.
- From **History**, tap a commission card to open its payment history, then use **Delete** in the header. This removes the whole commission, including all of its receipts across all reporting months.
- Both routes open the same **Delete this commission?** dialog. It names the artwork/client and explains that payment history, reference/artwork records, and the checklist will be removed. **Cancel** closes the dialog without saving anything; the red **Delete commission** button confirms permanent removal.
- The repository removes only rows belonging to the selected commission: payments, artwork updates, reference/final attachments, tags, checklist items, then the commission itself. All deletes share one transaction. A failure rolls the whole transaction back. Existing RESTRICT foreign keys stay enabled; no schema migration is needed.
- Other commissions, shared client/category rows, and original image files are retained. A client with other completed commissions remains eligible for Reconnect based on those remaining records.
- A successful delete refreshes the snapshot and returns to the main tab the artist came from, clearing payment/detail screens for the removed commission. History entries, earnings, outstanding balances, workload counts, and recommendations use the remaining saved records. Deleting the last receipt in a reporting month shows the existing empty-History state.
- Saved deletion is permanent and has no undo. This action does not introduce standalone receipt editing/deletion, refunds, or cancellation controls.

Validation: debug and Android-test APK compilation succeeded; **21 JVM tests passed**, and lint reported **zero errors/eight warnings**. **Six isolated desktop SQLite deletion checks passed**, covering removal scope and remaining earnings, all four statuses, persistence after reopen, transaction rollback, an unknown dependent-table constraint, and an empty remaining history. Those SQL checks use the actual schema and repository child-table list but do not execute Android/Kotlin code.

`CommissionDeletionTest` adds four isolated Android repository tests: completed work with every child record and a shared image/client, deletion across statuses, rollback on failure, and rejection of a missing commission without affecting other work. The tests were compiled, not run. No app, emulator, or device test was launched, and the artist's saved data was untouched. Changes remain local on `final-enhancements`.
