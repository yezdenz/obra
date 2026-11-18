# Local usability pass: final enhancements

Branch: `final-enhancements`. Based on the local `backend-integration` checkout. Requested to remain local; do not push, merge, or publish these changes without a later owner instruction.

## Why this changes the earlier wireframe

The owner found the working app confusing, especially the repeated Lineart/stage/task controls. This local pass makes the everyday workflow simpler while keeping existing saved records usable. The earlier boards and PDF remain the previous design reference; they have not been redrawn for this local experiment.

## The everyday flow

1. Add a commission. It joins **Queued**.
2. Open it and tap **Start commission** when work begins.
3. Work at your own pace. Open it and tap **Mark completed** when the artwork is ready.
4. Check the actual finish date and confirm. **Keep working** cancels completion.
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
| In-progress primary action | Mark completed, opening date confirmation directly |
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

## Data and compatibility

No database/schema or recommendation-policy changes are included. Existing commissions, references, receipts, task states, stage values, and lookup IDs remain in their existing tables. The default checklist can still exist on new commissions; its controls are now optional and hidden until requested. Checkbox changes continue to affect only that checklist row.

Home's still-to-collect amount includes completed-but-unpaid artwork and excludes cancelled artwork, matching the existing financial rules. Earnings continue to count received receipts by their received date. Opening or expanding a view never writes data.

The Add/Edit four-step flow, draft validation, receipt protections, and Reconnect section remain available. Old saved navigation state pointing to the removed Home task-list route returns safely to Home.

## Validation

Build/check commands from `OBRA/`:

```text
gradlew.bat assembleDebug testDebugUnitTest lintDebug connectedDebugAndroidTest
```

`EnhancementWorkflowTest` covers a clean Home, optional checklist persistence/recreation, the real Details-to-completion route, completed-but-unpaid artwork, and cancelling direct completion. Existing regression checks are updated to expand the optional checklist and use the renamed stage actions. Instrumentation uses the existing separate test database. Visual review should use the Medium Phone reference, including longer titles and larger text.

Verified locally on November 18, 2026: debug APK build succeeded; 13 unit tests and 25 device tests passed; lint reported zero errors and seven warnings. Home/Details and completion review were inspected on the 1080 x 2400 Medium Phone reference, including Details/completion at 1.3 font scale. The review emulator ran in a temporary read-only session so the original AVD data was not changed. Review screenshots are outside Git under LocalAppData/OBRA/enhancement-review.

Generated APKs remain outside the repository in the checkout-specific LocalAppData build folder. Do not add APKs, emulator data, or local review screenshots to Git.
