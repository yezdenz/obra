# Commission Details wireframe

## Purpose and entry points

Commission Details keeps a project's client, brief, references, progress, tasks, dates, and payments together. It opens from a dashboard card, the commission list, related history, or the final Save commission action in the accepted intake flow.

The current work is a **static wireframe board** using DynaPuff for headings/actions and Mali for supporting text. Reference and final-artwork areas are placeholders. No controls or state transitions have been implemented.

## Board layout

The board contains four phone views at Medium Phone proportions:

1. **In progress: first view** — project/client, state, deadline, references, brief, creative progress.
2. **Same commission: further down** — tasks, schedule, payment summary, payment history preview, More options.
3. **Queued** — the newly saved intake example, its planned dates, unpaid balance, and Start commission.
4. **Completed** — finished artwork, original deadline, actual finish date, and a balance that is still outstanding.

The first two drawings are different scroll positions of the **same page**, not two separate screens. In the eventual app, longer content should scroll while important actions remain reachable. Do not shrink all the content into one viewport.

## Shared information hierarchy

- **Header:** Back, Commission details, Edit.
- **Identity:** project title, username/platform, lifecycle status, and deadline or finish date.
- **Images:** references for active work; a final-artwork preview for completed work, with original references still accessible.
- **Brief:** package/type, genre/style tags, pose instructions, and notes.
- **Creative progress:** current work stage, distinct from the commission's overall lifecycle status.
- **Checklist:** task names and completion checkboxes only; no per-task dates, detail arrows, or extra task actions in the simplified draft.
- **Schedule:** commission date, planned start when relevant, due date, actual finish date after completion.
- **Payments:** agreed total, amounts received, remaining balance, and access to the payment records.
- **Actions:** depend on lifecycle/payment state, as described below.

Main bottom navigation is omitted from this record view. Back returns to the screen that opened it and should preserve that screen's filter/position. The draft shows a bottom action area rather than crowding every section with large buttons.

## In-progress example

The fictional **Fantasy portrait** belongs to `mika.draws` on Discord. It is a Portrait request with Fantasy and Soft light tags, a relaxed over-the-shoulder pose, and a lilac scarf. Two reference placeholders are shown.

Creative stages in this draft are **Sketch → Lineart → Colors → Finished**. Sketch is done, Lineart is current, and the later stages are pending. The exact package-specific stage rules remain open.

The simplified checklist shows Finish sketch checked and Finish lineart unchecked. The owner requested removing task-specific deadlines and the Send color preview row/action to reduce complexity. The current draft also omits task-detail arrows and Add task; it is a plain checklist. Completing a checklist item must not automatically complete the whole commission or record a payment.

The overall commission deadline stays in Schedule. Custom task creation/detail screens and per-task scheduling are deferred rather than required by this wireframe.

Example dates: commission November 1, planned start November 3, deadline November 14, 2026.

The agreed amount is PHP 1,800. A PHP 900 deposit was received on November 2, leaving PHP 900. These are fictional planning examples, not final service rates.

The main actions are **Record payment** and **Update progress**. Keep them separate so advancing artwork never implies that money was received.

## Queued example

The **Moonlit portrait** for `luna.art` on Discord continues the accepted Add commission example:

- Half body, Fantasy/Soft light, two references.
- Commission date November 9, planned start November 12, due November 20, 2026.
- PHP 2,000 agreed, PHP 0 received, PHP 2,000 outstanding.
- Queued lifecycle status; creative work not started.

The main action is **Start commission**. Opening the record, recording a deposit, or reaching the planned start date must not silently start the project. An explicit start action moves it into In progress and its initial stage. The presentation of any start-date confirmation is a later supporting wireframe.

Record payment remains available within the payment section. An accepted request can receive a deposit before work starts.

## Completed example

The fictional **Cozy café illustration** for `bea.sketch` on X shows a final-artwork placeholder and access to its original references. The brief describes a warm café interior with afternoon light and houseplants.

- Commission date: October 24, 2026.
- Original deadline: November 7, 2026.
- Actual finish date: November 8, 2026.
- Agreed amount: PHP 4,500.
- Received: PHP 3,500.
- Still to receive: PHP 1,000.

The lifecycle status is **Completed**, and the payment status remains **Partially paid**. The primary action is **Record payment**; **Payment history** opens this commission's receipts. Previous artwork is found under Commissions → Completed; the main History section is proposed for payment transactions. Receiving the remaining balance changes the payment summary, not the completed work status.

A fully paid completed variant should later show a zero balance and payment history without suggesting another payment is required. Completed work retains its references, tasks, notes, and financial history even when the first viewport prioritizes final artwork and summary information.

## Planned control behavior

| Control | Destination or effect to depict |
| --- | --- |
| Back | Originating dashboard/list/history view |
| Edit | WF-14–WF-17 prefilled edit steps and changed-value review; Save changes updates this record and returns here |
| Reference or final-artwork image | WF-18 image viewer; Back returns to the same record/position |
| Update progress | WF-10 stage-selection page; Save progress returns to this record without editing payments |
| Finish/complete stage | WF-10 Finished → Review completion → WF-11; confirm actual finished date, then return to this record as Completed without marking paid |
| Checklist label | Display the task name without a separate task-detail destination |
| Task checkbox | Change only that task's completion state |
| Record payment | Amount, received date, method, and optional note |
| View payment history | Open the dedicated payment-history list for this commission, with initial/later payments, received dates, methods, and notes visible on the entries |
| Start commission | Explicitly move Queued work into In progress |
| Payment history | This commission's deposit and later payment records; the completed artwork remains under Commissions → Completed |
| More options | Secondary record actions, including cancellation where relevant |

Cancelling needs a confirmation and must retain payment history. Permanent deletion is deferred until retention rules are agreed. Editing prices must not erase or rewrite received payments.

## Related decisions and follow-up screens

The owner accepted the four-step Add commission wireframe and the DynaPuff/Mali direction. **Commission Details is a new draft for review.** Its exact stage controls, completion meaning, cancellation behavior, and supporting sheets are still proposals.

The [commission payment-history draft](payment-history.md) now shows the dedicated payment list reached from View payment history. It is separate from Insights and its earnings graphs.

The [Record payment draft](record-payment.md) now shows the form and updated history after saving. A Fully paid summary does not change the commission's work status.

The [Edit commission draft](edit-commission.md) now shows four prefilled steps and a due-date change reviewed before Save changes. Existing receipts remain read-only within editing, and the record's lifecycle/stage do not reset.

The [Update progress/completion draft](update-progress.md) now shows stage choices, finished-date confirmation, and this existing record after completion. The [larger image viewer](image-viewer.md), WF-18, now has reference and final-artwork variants. A fully paid completed record and overdue/empty-reference states remain to draw. Task editing/detail screens are deferred under the simplified checklist direction.

See [layout.md](../layout.md), [Add commission](add-commission.md), and the [screen inventory](screens.md).
