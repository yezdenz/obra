# Feature requirements

## Purpose and intended users

OBRA is a personal commission workspace for freelance visual artists. The artist receives a request elsewhere, records it in OBRA, tracks the work and payments, and reviews previous commissions and earnings.

The product should help answer four everyday questions:

1. What am I working on?
2. What needs attention next?
3. Which commissions are waiting to start?
4. How much money have I actually received?

## Requested scope

The following features come from the owner's latest planning request.

### Dashboard

- Monthly earnings summary.
- Current work overview.
- Unfinished task overview.
- Commission queue overview.
- A clear path to add a commission.

### Add a commission

- Enter the client's username and select X or Discord as the contact platform.
- Choose a commission type such as full body, half body, chibi, or portrait.
- Attach reference images, including requested poses.
- Record genre, style, theme, and additional instructions as appropriate.
- Record commission date and due date.
- Record the agreed total and payment information needed to track income.

The exact required fields and how long forms are divided remain planning decisions. Username entry is manual; it does not imply connecting to a platform account.

### Individual commission records

- View current, previous, and future/queued commissions.
- Open an individual commission to inspect its full information.
- Keep the client brief and references together.
- Track work status and creative progress.
- Keep commission date, planned dates, and actual finish date distinct.

### History

- The current layout proposal keeps previous artwork under Commissions → Completed and individual received-payment transactions under History.
- Display amounts received and their associated commissions.
- Display commission deadlines and finished dates on the artwork records, accessible from Completed cards or related payment entries.
- Preserve the difference between quoted value, paid amounts, and unpaid balances.

### Financial insights

- Total earnings.
- Monthly earnings.
- Earnings graphs over time.
- A financial summary of the artist's commission activity.

The term **Insights** is proposed for this section. It is an income/workload summary, not a physical inventory system.

## Suggested supporting features

These support the requested features but remain subject to review.

| Suggestion | Why it may help |
| --- | --- |
| Separate payment records | Deposits and balance payments can happen on different dates and in different months |
| Outstanding balance | Shows what a client still owes |
| Upcoming/overdue indicators | Makes urgent work visible without reading every record |
| Per-commission checklist | Simple task names and completion checkboxes; no task-specific deadlines or Send color preview action |
| Search and filters | Helps locate a client or project as records grow |
| Package management | Lets the artist maintain service types, base prices, and turnaround estimates |
| Add-on quotation | Supports extra characters, detailed backgrounds, or commercial usage charges |
| Cancelled status | Keeps an accurate record of requests that will not be completed |
| Queue capacity and ordering | Helps manage accepted work and the next project to start |
| Final artwork attachment | Keeps the delivered result with the completed commission |

Refund recording, advanced reporting, and previous-client recommendations should be discussed separately before becoming requirements.

The owner requested removing task-specific dates and the Send color preview row/action to keep the scope manageable. The current draft uses a plain checklist. Additional task-detail/custom-entry screens are deferred. Overall commission deadlines and payment received dates remain part of the plan.

## Proposed terminology and behavior

### Commission lifecycle

- **Queued:** accepted but not yet started.
- **In progress:** work has started and is not yet completed.
- **Completed:** the artwork has been finished/delivered according to the definition chosen during planning.
- **Cancelled:** the commission is no longer proceeding.

### Creative stage

Suggested stages are Sketch, Lineart, Flat colors, and Final rendering. Different packages may eventually use different stages. The stage list is not final.

### Payment state

Suggested labels are Unpaid, Partially paid, and Fully paid. A deposit does not have to equal a fixed percentage. Payment status is independent of work status: a completed artwork can still be unpaid, and a queued artwork can already have a deposit.

### Earnings

Monthly earnings should use payments received during the selected month. For example, a PHP 1,000 deposit received in November and a PHP 2,000 balance received in December contribute PHP 1,000 to November and PHP 2,000 to December. The PHP 3,000 quote must not be counted again as a separate payment.

The agreed price belongs to the commission. Changing a package's base price later must not change an existing commission's agreed amount.

## Boundaries

- Current work is planning and static wireframing only.
- The intended app is local to the artist's Android device.
- No client portal or client login is required by the current scope.
- X and Discord are contact labels, not integrations.
- Payments are recorded manually; real banking, GCash, or PayPal processing is outside scope.
- Cloud accounts, cross-device sync, expense accounting, and tax calculations are outside the initial plan.

## Planning review criteria

Before implementing the app, the static screens should make these journeys understandable:

1. An artist sees monthly income and the most urgent work on Home.
2. A new request can be described with a platform username, package, references, price, and deadline.
3. A queued commission can become current work without losing its original details.
4. Creative progress can change without changing payment records.
5. A deposit and a final payment remain separately visible.
6. A previous commission shows its original deadline, actual finish date, and payments received.
7. Home and Insights use consistent income definitions.
8. Empty, overdue, partially paid, completed-but-unpaid, and cancelled cases have defined presentations.
