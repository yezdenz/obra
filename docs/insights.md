# Insights — static wireframe draft

## Purpose and board

Insights brings together received earnings, their monthly trend, a commission-type breakdown, and the balances clients still owe. It continues the accepted DynaPuff/Mali pairing, warm palette, simple shapes, and Android Studio Medium Phone proportions. This is a new proposal for review, not a working report.

The two phones show **one vertically scrolling page**, WF-13:

- **WF-13A, left:** first viewport: month selector, monthly earnings, all-time received total, current outstanding balance, and six-month earnings graph.
- **WF-13B, right:** further down that same page: received earnings by commission type and the current outstanding commissions.

There is no Next button between these views. Scroll down, or use See balances to scroll directly to the balance section. Main navigation stays reachable. See [the numbered guide](../layout.md#wf13) and [saved preview](wireframes/insights.jpg).

## Entry and information order

Enter from Insights in main navigation or Home → View insights. The Home shortcut carries its selected month. First entry through main navigation defaults to the current calendar month; subsequent navigation preserves the selection and position.

Read top to bottom: title → selected reporting month → Monthly earnings and received-payment count → Total earnings / Still to receive → six-month trend → commission-type breakdown → current outstanding commissions → main navigation, Insights selected.

## Metric definitions

| Element | Meaning and scope |
| --- | --- |
| Monthly earnings | Sum actual payments received in the selected calendar month; current month includes only receipts recorded through today |
| Payment count | Number of individual received-payment records in that month; two receipts for one artwork count twice |
| Total earnings | Sum received payments across all recorded dates, as of today; independent of the selected month |
| Still to receive | Current positive balances for Queued, In progress, and Completed work; sum each commission's agreed price less all its received payments |
| Six-month trend | Received payments in each of six calendar months ending with the selected month; chronological order, zero baseline, PHP units |
| By commission type | Selected month's received payments grouped by their parent artwork's type; not quoted value or commission counts |
| Outstanding list | The same current balances contributing to Still to receive, highest amount first; each row opens its commission |

These are received-income summaries, not profit or tax accounting. Quotes, balances, work completion, and creative-stage changes do not create earnings. A deposit and later balance payment contribute to their actual received months without counting the quote again. A queued deposit contributes to earnings just like a payment on current or finished work.

**Selecting a past month updates monthly earnings/count, the six-month chart window, and the type breakdown. Total earnings and Still to receive retain their current all-time/today scopes.** The latter is explicitly dated in its lower section; it is not a reconstructed historical balance. Cancellation/refund handling remains open and is outside this draft's eligible artwork states.

## Planned controls and routes

| Control | Destination or result |
| --- | --- |
| Month/year selector | Shared period chooser, still undrawn; apply changes to this same page |
| View payments on Monthly earnings | WF-12 History with this month selected; its received total must match |
| See balances | Scroll to Still to receive on this page; no extra filtered-list screen |
| Outstanding commission row / chevron | WF-07 for that record; Back restores Insights month and scroll position |
| Home navigation | WF-01 |
| Commissions navigation | WF-02 |
| History navigation | WF-12 with its previously selected month; use View payments to explicitly carry the Insights month |
| Insights navigation | Stay here and retain context |

Graph values are labeled directly. Bars, type rows, and the Total earnings value add no new navigation or record-editing actions in this simple draft. A payment can be recorded using the existing commission flow, not by editing a report value.

## Fictional examples and reconciliation

The board uses a November 9, 2026 snapshot. Its **₱4,450 / five payments** matches the [main History example](main-history.md): ₱900 + ₱1,000 + ₱900 + ₱750 + ₱900.

The illustrative trend has no earlier receipts before June, so these six months also sum to the displayed all-time received total. In the eventual app, all-time includes earlier records even when they are outside the six-month chart.

| Month, 2026 | Received |
| --- | ---: |
| June | ₱1,800 |
| July | ₱3,000 |
| August | ₱4,200 |
| September | ₱5,350 |
| October | ₱5,200 |
| November 1–9 | ₱4,450 |
| **All time in this example** | **₱24,000** |

The chart uses a common zero-to-₱6,000 scale and visibly marks November as incomplete. Future dates are not plotted as zero-income observations. Do not add a month-over-month percentage implying a complete November comparison.

| November artwork type | Received |
| --- | ---: |
| Portrait | ₱1,800 |
| Full illustration | ₱1,000 |
| Chibi | ₱900 |
| Half body | ₱750 |
| **Total** | **₱4,450** |

Type bars use one shared scale and explicit PHP values. Both payments for Fantasy portrait contribute to Portrait. Classification changes for edited artwork remain a historical-reporting decision; no extra package-management UI is added here.

| Current outstanding commission | State | Remaining |
| --- | --- | ---: |
| Character sheet / jules.draws | In progress | ₱3,200 |
| Moonlit portrait / luna.art | Queued | ₱2,000 |
| Cozy café illustration / bea.sketch | Completed | ₱1,000 |
| Half-body illustration / @kai.inks | In progress | ₱750 |
| Forest chibi / ren.art | Queued | ₱600 |
| **Five commissions** | | **₱7,550** |

Fantasy portrait is fully paid in this workflow snapshot and therefore absent from the outstanding list. The separate completion board illustrates an earlier/alternative partly paid moment. Completing an artwork does not remove an owed balance; receiving a payment lowers it without changing work status.

## Empty, error, and scaling states still to draw

- No receipts in a selected month: show zero monthly earnings/count and an empty type breakdown; keep valid prior-month trend data and current balances.
- No received payments anywhere: show an explicit no-earnings state, not invented example bars.
- No outstanding balances: show zero and a simple all-settled message without placeholder commissions.
- Failed loading: show retry/error feedback rather than substituting zero income.
- Longer titles, larger text, more commission types, and longer outstanding lists: let content grow and scroll; do not shrink essential labels.
- Period chooser and cancelling a selection: retain current filters if dismissed; exact picker layout remains pending.

The graph marks and controls are static. No report calculations, click handling, transitions, database, export, or payment processing are implemented by these drawings.
