# Commission payment history wireframe

## Entry and purpose

**View payment history** on Commission Details opens a separate **Payment history** view for that commission. It shows the initial payment and any later amounts actually received.

This is a static planning screen with the accepted DynaPuff/Mali typography and warm palette. It is not a graph or analytics page. Back returns to the same commission and its previous position.

## Planned contents

1. Back and Payment history header.
2. Project title and client username/platform, so the scope is clear.
3. Summary: agreed total, total received, outstanding balance, payment status.
4. Received payments: each entry's amount, received date, purpose, method, and optional note.
5. Record payment action for a later payment already received.

Keep each receipt readable in the list. A separate payment-details screen is not needed in this simplified draft; the method and note appear directly on the entry.

## Example shown on the board

The fictional Fantasy portrait commission has PHP 1,800 agreed, PHP 900 received, and PHP 900 still to receive. Its list contains one **Initial payment** of PHP 900, received November 2, 2026, with GCash recorded as the method and Deposit as its purpose.

The initial payment can originate from Add commission's optional payment fields. It should appear here automatically when saved, without requiring the artist to record the same money a second time.

A later balance payment becomes another received-payment entry only after it is recorded. The outstanding PHP 900 is a summary value, not a second received payment. If no money has been received, show an empty payment list and the full balance outstanding.

Method names are manually recorded labels. This view does not connect to GCash, Discord, X, or a banking/payment service.

## Relation to other sections

| View | Scope |
| --- | --- |
| Commission payment history | Actual payments for one specific commission, including its deposit and balance payments |
| Main History section | A broader list of transactions/previous work across commissions |
| Insights | Total/monthly earnings, outstanding-balance summaries, and graphs across the selected reporting period |

Insights and the history views use the same payment records. Opening history or changing a reporting period does not create another payment.

## Planned actions

- **Back:** return to Commission Details.
- **Record payment:** open the [Record payment form](record-payment.md), with amount, received date, method, optional note, and an after-save balance preview.
- **Save payment:** validate the record, save it once, return to the list, and update its summary. The new static board shows the form and this saved result.
- **No remaining balance:** show Fully paid and a return-to-commission action; do not invite another ordinary payment when nothing is owed.

Payment received dates stay in the records because they determine monthly earnings. They are distinct from the commission's due date. Removing task-specific deadlines does not remove commission deadlines or payment dates.

See [Commission Details](commission-details.md), [layout.md](../layout.md), and the [general data plan](data-plan.md).
