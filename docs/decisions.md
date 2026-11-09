# Decisions and open questions

## Established direction

| Topic | Direction | Basis |
| --- | --- | --- |
| Product | OBRA, a personal workspace for freelance artist commissions | Original proposal and planning conversation |
| Current task | Planning and static wireframes, dashboard first | Explicit owner correction and follow-up |
| Device | Android Studio Medium Phone portrait reference | Owner request; local profile inspected |
| Visual inspiration | Guhit's fonts, shapes, and simplicity | Explicit owner preference |
| Selected playful font | DynaPuff, including Monthly earnings, Finish lineart, headings, and primary actions | Explicit owner preference |
| Supporting font | Mali for joyful dates, links, small labels, and numeric details alongside DynaPuff | Owner accepted the updated dashboard before moving to Add commission |
| Current visual reference | Refined DynaPuff/Mali dashboard | Owner liked the result and requested the next screen |
| Add commission layout | Four steps: Client details, Artwork brief, Price and dates, Review | Owner explicitly loved the static board and moved to Commission Details |
| Checklist simplification | Remove task-specific dates and the Send color preview row/action; keep the overall commission deadline | Explicit owner request to reduce complexity |
| Required areas | Dashboard, intake, commission records, history, financial summaries/graphs | Owner's updated feature list |
| Client contacts | Manually recorded X or Discord usernames | Owner request |
| Package examples | Full body, half body, chibi, portrait | Owner request |
| Reference material | Images, pose requests, genres, and notes | Owner request |
| Data planning level | General information groups without detailed IDs | Explicit owner request |

## Working proposals to review

- Home, Commissions, History, and Insights as the four main sections.
- [Commissions list](commissions-list.md) with Current, Queued, and Completed artwork filters; main History reserved for individual payment transactions, avoiding a duplicate artwork archive.
- Exact color values, type sizes, and detailed field/pricing rules described in [layout.md](../layout.md).
- The new [Commission Details board](commission-details.md), including action placement and state-specific summaries.
- [Commission payment history](payment-history.md) as a simple per-commission receipt list, separate from Insights; additional task-detail/custom-entry controls are deferred.
- [Record payment](record-payment.md): a short amount/date/method/note form, positive amount within the remaining balance, and return to updated history after Save.
- [Update progress and completion](update-progress.md): WF-10 full-page stage selection; Finished opens WF-11 to confirm actual finished date; completion returns to the same record and retains its payment/checklist states. This is a new draft, not an accepted completion definition.
- [Main History](main-history.md): WF-12 shows individual received payments grouped by received date, a month/year selector, full-period received total/count, links to commission records, and an empty month. This is a new static draft for review; the picker remains undrawn.
- [Insights](insights.md): WF-13 is one scrolling page with monthly earnings, all-time received earnings, current balances, a six-month trend, and type breakdown. View payments carries its month to History; See balances scrolls within Insights. A new draft for review; all-time/current metrics retain their scopes when selecting a past month.
- Lifecycle statuses: Queued, In progress, Completed, Cancelled.
- Creative stages tracked independently from lifecycle and payments.
- Payment records with received amounts and dates; monthly earnings calculated from them.
- Simple per-commission checklist items with completion states; per-task dates and extra task-detail/custom-entry controls are excluded from the current draft.
- Package management, add-on quotation, search, deadline warnings, and queue ordering.

These are recommendations captured for discussion. Inclusion in this repository does not make them approved implementation requirements.

## Original proposal items requiring reconciliation

The original proposal also described a tag-based previous-client recommendation feature and artwork logging. The latest requested feature list focuses on commission management and financial summaries. Keep recommendation/artwork-library features deferred until the owner confirms whether they still belong in the project.

The intended technical stack in the proposal is Android Studio, Kotlin, XML/Material, and SQLite. Its detailed architecture and schedule have not been reconfirmed during the current planning phase.

## Open choices

1. Should Home show all unpaid balances, or only a monthly received-income summary?
2. Review the proposed split: artwork archive under Commissions → Completed, individual received-payment transactions under main History.
3. Which simple checklist labels are needed in the first version? Custom task management is deferred.
4. Is queue order manual, based on deadlines, or based on planned start dates?
5. Does Completed mean artwork finished, final files sent, or client acceptance?
6. Should a client support multiple usernames/platforms?
7. Are genres a single field or multiple tags?
8. Can packages have different creative stages?
9. Which add-on pricing rules are needed in the first version?
10. How should cancellation, refunds, payment corrections, and historical record deletion work?
11. Are final artwork attachments needed in the first version?
12. What currency rules apply? PHP is used for examples; multiple-currency conversion is not planned.
13. Should the exact color values, type sizes, and spacing be finalized beyond the accepted DynaPuff/Mali pairing?
14. How should the expanded initial-payment and invalid-input states fit within the accepted four-step intake layout?

Resolve these through planning as they become relevant. They do not block drafting the dashboard's static structure.
