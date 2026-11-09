# Commissions list wireframe

## Purpose and section boundaries

The current proposal separates **artwork records** from **payment transactions**:

- **Commissions:** each accepted artwork request, including current work, queued work, and completed/previous work.
- **History:** individual received-payment entries across commissions, such as deposits and later balance payments.
- **Insights:** earnings totals, outstanding-balance summaries, and graphs derived from those payments.

An artwork appears once as a commission record, but can have several payment entries. For example, one artwork with a deposit and a balance payment is one commission card and two transaction entries. Opening a transaction's related commission returns to that same record.

Completed artwork belongs under **Commissions → Completed**. The main History list does not need to duplicate the artwork archive. This split is the working recommendation in response to the owner's question; the [main transaction History draft](main-history.md) now shows received-payment entries and links back to their artwork records.

## Static board

The board shows four states of the same Commissions screen:

1. Current.
2. Queued.
3. Completed.
4. New-artist empty state.

Use DynaPuff for the screen/project titles and New/Add actions, with Mali for usernames, dates, prices, payment labels, filters, and navigation. Keep Medium Phone proportions, the warm cream background, rounded cards, and yellow add action.

## Shared layout

- Commissions title and a small **+ New** action.
- A short subtitle.
- Search client or artwork field.
- Three filters: **Current**, **Queued**, **Completed**.
- Matching commission count and current ordering label.
- Scrollable commission cards.
- Main navigation: Home, Commissions, History, Insights, Reconnect; Commissions selected.

New and the empty-state Add commission action both open the accepted intake flow at Client details. There are no working controls in this static draft.

## Current

Current maps to the **In progress** lifecycle status. Cards show the title, username/platform, creative stage, commission deadline, agreed price, and remaining amount or Fully paid.

The example has four current projects with the first three visible, ordered by nearest deadline:

- Half-body illustration: Lineart, due tomorrow, PHP 1,500 agreed, PHP 750 outstanding.
- Fantasy portrait: Lineart, due November 14, PHP 1,800 agreed, Fully paid. This continues the recent Record payment example: the payment is complete while artwork remains in progress.
- Character sheet: Sketch, due November 16, PHP 3,200 agreed and outstanding.

The count represents all matching records, not just cards visible in one viewport. Let longer lists scroll rather than shrinking the cards. These dates belong to whole commissions; per-task deadlines remain removed.

## Queued

Queued contains accepted work that has not started. Cards emphasize the commission type, planned start when available, deadline, agreed price, and outstanding balance.

The examples are Moonlit portrait, continuing the accepted intake example, and Forest chibi. The latter demonstrates that a queued commission can already have a deposit.

The board proposes ordering by planned start. Queue ordering and handling projects without a planned start still need agreement. Opening a card does not start the commission; Start commission stays an explicit action in its details.

## Completed

Completed is the archive of finished artwork. Cards show the title, client, type, actual finish date, agreed price, and payment state. The first viewport shows the newest three of six fictional completed commissions.

Cozy café illustration demonstrates a completed artwork with PHP 1,000 still outstanding. Starlight chibi and Autumn portrait are fully paid. Do not move a finished artwork back into Current simply because a payment is still owed.

Original deadlines, commission dates, final artwork, brief, references, and full payment records remain accessible in Commission Details. Completion does not create a duplicate commission or a payment transaction.

## Search, selection, and return

- **Search:** match the title or client username within the selected filter in this draft.
- **Change filter:** show that lifecycle group; do not change any record's status.
- **Card/chevron:** open that record's Commission Details, regardless of payment state.
- **Back from details:** restore the list's filter, query, and position in the eventual app.
- **History navigation:** open the broader payment timeline across commissions.
- **Insights navigation:** open financial summaries and graphs.

The visual uses agreed-price/balance summaries on artwork cards for quick context. It does not show a separate card for every deposit; those belong in payment history.

## Empty and deferred states

The empty state shown is for a new artist with no commission records, with a clear Add commission action. A category with no matches and a search with no results need distinct later messages so they do not imply that all data is missing.

Cancellation remains a separate proposal. Cancelled records should be retained if that feature is adopted; their additional-filter/archive access is not represented by the three main tabs in this first draft. The absence of a Cancelled tab does not authorize deleting those records.

This board is a draft for review. See [Commission Details](commission-details.md), [Record payment](record-payment.md), and the [screen inventory](screens.md).
