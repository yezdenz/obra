# Screen and navigation plan

The screens below are a planning inventory. The dashboard's visual direction, DynaPuff/Mali pairing, and four-step intake wireframe have been reviewed and accepted as references. New screen drafts and the remaining grouping/navigation decisions still need review. Static wireframes come before executable navigation.

Use [layout.md](../layout.md) as the master numbered guide. It embeds every current board and assigns WF-01–WF-12 to existing page/step layouts, with separate labels for filters, saved states, and scroll positions. Remaining undrawn destinations start at P-04; former P-01/P-02/P-03 are now WF-10/WF-11/WF-12.

## Main sections

### Home / Dashboard

Monthly earnings, current work, unfinished tasks, queue preview, and Add commission. See [the dashboard plan](dashboard.md).

### Commissions

A list of artwork records with Current (In progress), Queued, and Completed filters. Search locates a username or project within the selected group. Selecting a commission opens its details. See the [Commissions list draft](commissions-list.md) for these states and the empty list. Cancelled-record access remains a later proposal.

Queued records represent accepted work that has not started. Planned start dates are useful when known, but their required/optional status is still to be decided.

### History

The current recommendation is a chronological view of individual payment transactions across commissions, including deposits and later balance payments. Show the client, related artwork, received amount, received date, and recorded method. Previous artwork itself belongs under Commissions → Completed. A transaction links to the same parent commission and its full dates/details.

One artwork can have multiple payment transactions. Main History should not duplicate the completed-artwork list. The [main History draft](main-history.md), WF-12, now shows month selection, received totals, dated receipt rows, and an empty month. Tapping a payment opens its commission; Back restores the selected month and list position. The commission-specific payment-history page remains WF-08.

### Insights

Total earnings, monthly earnings, outstanding balances, and graphs over time. Package/type breakdowns and completed-work counts are useful supporting ideas. Define each metric before choosing a chart.

## Main workflow screens

### Add commission

Organize the form into sensible groups:

1. **Client:** username, platform, optional display name.
2. **Request:** package/type, title, brief, pose instructions, genres/tags.
3. **References:** attached images and captions, if needed.
4. **Price and payment:** agreed total, optional add-ons, any payment received.
5. **Schedule:** commission date, planned start if known, and deadline.
6. **Notes:** additional instructions. Custom task entry is deferred under the simplified checklist direction.

The accepted intake wireframe direction in [layout.md](../layout.md) divides entry into four steps: Client details, Artwork brief, Price and dates, and Review commission. It specifies the proposed checks and destinations for Next/Back actions. Detailed validation and price rules remain open, and working transitions are outside the current phase.

The [Add commission wireframe plan](add-commission.md) describes the current four-screen draft, including reference thumbnails, optional initial-payment entry, the Review screen, and Back/Close behavior. The form omits main bottom navigation and uses the accepted DynaPuff/Mali typography pairing.

### Commission details

Show the complete client/request information, references, agreed price, payment timeline, outstanding balance, dates, lifecycle status, creative stage, and tasks. Previous records can also show the final artwork and actual finish date.

Proposed actions include Edit, Update progress, Record payment, and Cancel. Checklists use simple checkboxes. Completion is reached through Update progress → Finished → Review completion.

See [Commission Details](commission-details.md) for the current static draft: first/lower views of an in-progress record, the queued intake example, and a completed record with an outstanding balance. Record payment and work-progress controls stay separate.

### Edit commission

Reuse the intake structure with existing values. Keep previously received payments intact and independently editable through an appropriate payment-record flow. Decide how agreed-price changes should be recorded before implementation.

## Supporting screens or sheets

| View | Purpose |
| --- | --- |
| [Record payment](record-payment.md) | Enter amount, received date, method, and optional note; preview remaining balance; Save opens updated commission payment history |
| [Commission payment history](payment-history.md) | Show a commission's initial and later payments, amounts, received dates, methods, notes, and balance |
| [Update progress and completion](update-progress.md) | WF-10 stage-selection page; Finished opens WF-11 full-page review with actual finished date and unchanged payment totals |
| All tasks | A simple list of unfinished checklist items with links to their commissions; no task-specific dates or extra detail screens |
| Package manager/editor | Maintain offered services, prices, and turnaround estimates |
| Image viewer | Inspect a reference or final artwork |
| Date picker | Choose a commission, start, due, or payment date |
| Confirmation dialog | Review a cancellation, deletion, or unsaved-change decision |

Undrawn views may become full screens, dialogs, or sheets. The progress/completion draft currently uses separate full pages; it remains subject to review.

## Conceptual navigation

```mermaid
flowchart TD
    Home[Home: dashboard] --> List[Commissions]
    Home --> Add[Add commission]
    Home --> Detail[Commission details]
    Home --> Tasks[Unfinished checklist items]
    Home --> Insights[Insights]
    Home --> History[History]
    List --> Add
    List --> Detail
    History --> Detail
    Detail --> Edit[Edit commission]
    Detail --> Payment[Record payment]
    Detail --> Progress[Update progress]
    Progress --> Finish[Completion confirmation]
    Finish --> Detail
    Detail --> Image[Reference / final artwork viewer]
```

This diagram describes relationships between planned views. It is not a working prototype or a finalized Activity architecture.

## Required states across the screen set

Include empty lists, long usernames, multiple references, a long brief, invalid/missing input, partial payment, completed-but-unpaid work, overdue work, cancelled work, and large histories in planning reviews. Use fictional examples.
