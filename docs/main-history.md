# Main History — static wireframe draft

## Purpose and scope

History is the chronological list of individual received payments across all commissions. It includes initial deposits, partial payments, and later balance payments. Completed artwork itself stays under Commissions → Completed; graphs and broader financial summaries belong in Insights.

This is a new draft for review, using the accepted DynaPuff/Mali pairing, warm palette, soft shapes, and Android Studio Medium Phone proportions. The controls are static drawings with no working transitions, filters, or saving.

## Board and entry

- **WF-12A, left:** November 2026 with payments; first four of five receipts visible.
- **WF-12B, right:** January 2026 with no receipts; a state of the same page.
- **Enter:** History in the main navigation.
- **Scroll:** the payment timeline scrolls vertically; the bottom navigation stays reachable. Do not fit an unlimited history into one viewport.

See [the numbered guide](../layout.md#wf12) and [saved preview](wireframes/main-history.jpg).

## Information order

1. History title and short supporting line.
2. Selected month and year, with a period-picker affordance.
3. Compact received-total summary for that selected month.
4. Count of individual receipts and fixed Newest first ordering.
5. Entries grouped under their received-date headings.
6. Main navigation: Home, Commissions, History selected, Insights.

Each receipt displays its artwork title, amount received, client username/platform, payment purpose, and manually recorded method. Multiple payments on the same date share one date heading. Long titles wrap; keep amounts readable without truncation. Optional notes and the full balance remain available in the commission-specific payment history.

This draft has one month selector and no work-status tabs or extra search control. Month selection is enough for the initial history layout; additional filters can be considered later.

## Proposed button behavior

| Control | Destination or result |
| --- | --- |
| Month selector | Open a month/year chooser; apply a selection to the same History page |
| Choose another month, empty state | Open that same chooser |
| Payment row / chevron | WF-07 for the related commission, using its current lifecycle state |
| Back from the commission | Return to WF-12 with the selected month and timeline position restored |
| View payment history from WF-07 | WF-08, all receipts and optional notes for that one commission |
| Record payment from WF-07 or WF-08 | Existing WF-09 payment form |
| Home navigation | WF-01 |
| Commissions navigation | WF-02 |
| History navigation | Remain here; retain selected month and position |
| Insights navigation | P-04 Insights, still undrawn |

Opening a transaction does not create a separate receipt-details page in this draft. Methods and payment purposes are already visible in the list. The global page does not offer an unscoped Record payment action: select its commission first. A successful later payment appears here once when its received date falls in the selected month.

The picker itself is still undrawn. Proposed behavior: open History on the current calendar month on first use, remember the month while navigating within the app, and cancel the chooser without changing the selection. This draft filters by a single calendar month, with no All time view yet.

## Example receipts and arithmetic

The November snapshot contains the following fictional payments:

| Received date, 2026 | Artwork / client | Purpose | Method | Received |
| --- | --- | --- | --- | ---: |
| Nov 9 | Fantasy portrait / mika.draws, Discord | Balance payment | GCash | ₱900 |
| Nov 7 | Cozy café illustration / bea.sketch, X | Partial payment | Bank transfer | ₱1,000 |
| Nov 5 | Starlight chibi / ren.art, Discord | Final payment | GCash | ₱900 |
| Nov 3 | Half-body illustration / @kai.inks, X | Initial payment | GCash | ₱750 |
| Nov 2 | Fantasy portrait / mika.draws, Discord | Initial payment | GCash | ₱900 |
| **Total: five receipts** | | | | **₱4,450** |

The Nov 2 receipt is below the first viewport, as indicated by the scroll cue. Its amount is included in the full-month summary. Fantasy portrait therefore appears twice in History but remains one artwork in Commissions. Its Nov 9 balance payment follows the saved example on the Record payment board; the completion board illustrates a separate, still-partly-paid workflow moment.

The café has ₱3,500 received overall, including an earlier ₱2,500 outside this month. Starlight chibi has ₱1,200 received overall, including an earlier ₱300 outside this month. Their current artwork balances are not the amounts of these individual receipts. Examples are planning snapshots, not live synchronized records.

## Totals and empty states

Sum actual received amounts whose received dates belong to the selected month; count payment records, not clients or artworks. Include payments for Queued, In progress, and Completed work. A quote, outstanding balance, due date, stage change, or work completion is not a received-payment entry. Recording an initial payment during intake and viewing it here must reference the same receipt, without double counting.

The January empty state keeps the month selector, zero received total, zero-payment count, and main navigation. Choose another month is the only primary action. Opening an empty period changes no records. With no receipts anywhere, use a distinct future empty-state message and a View commissions route so the artist can select a project before recording a payment.

Refunds, corrections, deletion/retention, and historical client/title edits remain open decisions. Do not invent refund or cancelled-receipt behavior in this positive-receipts draft.

## Remaining visual states

Month/year chooser, scrolling to older entries, multiple receipts on one date, no receipts anywhere, long text/larger type, and data-load failure remain to draw. A failed load must show an error/retry state rather than a misleading zero-income result.
