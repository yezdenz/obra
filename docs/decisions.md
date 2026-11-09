# Decisions and open questions

## Established direction

| Topic | Direction | Basis |
| --- | --- | --- |
| Product | OBRA, a personal workspace for freelance artist commissions | Original proposal and planning conversation |
| Current task | Planning and static wireframes, dashboard first | Explicit owner correction and follow-up |
| Device | Android Studio Medium Phone portrait reference | Owner request; local profile inspected |
| Visual inspiration | Guhit's fonts, shapes, and simplicity | Explicit owner preference |
| Selected playful font | DynaPuff, including Monthly earnings, Finish lineart, headings, and primary actions | Explicit owner preference |
| Required areas | Dashboard, intake, commission records, history, financial summaries/graphs | Owner's updated feature list |
| Client contacts | Manually recorded X or Discord usernames | Owner request |
| Package examples | Full body, half body, chibi, portrait | Owner request |
| Reference material | Images, pose requests, genres, and notes | Owner request |
| Data planning level | General information groups without detailed IDs | Explicit owner request |

## Working proposals to review

- Home, Commissions, History, and Insights as the four main sections.
- Andika for detailed text and monetary figures alongside DynaPuff.
- The warm cream/yellow/sage palette and four-step commission-entry flow described in [layout.md](../layout.md).
- Lifecycle statuses: Queued, In progress, Completed, Cancelled.
- Creative stages tracked independently from lifecycle and payments.
- Payment records with received amounts and dates; monthly earnings calculated from them.
- Per-commission tasks with completion states and due dates.
- Package management, add-on quotation, search, deadline warnings, and queue ordering.

These are recommendations captured for discussion. Inclusion in this repository does not make them approved implementation requirements.

## Original proposal items requiring reconciliation

The original proposal also described a tag-based previous-client recommendation feature and artwork logging. The latest requested feature list focuses on commission management and financial summaries. Keep recommendation/artwork-library features deferred until the owner confirms whether they still belong in the project.

The intended technical stack in the proposal is Android Studio, Kotlin, XML/Material, and SQLite. Its detailed architecture and schedule have not been reconfirmed during the current planning phase.

## Open choices

1. Should Home show all unpaid balances, or only a monthly received-income summary?
2. How should History divide payment transactions from completed commission history?
3. Are tasks freely entered by the artist, generated from creative stages, or both?
4. Is queue order manual, based on deadlines, or based on planned start dates?
5. Does Completed mean artwork finished, final files sent, or client acceptance?
6. Should a client support multiple usernames/platforms?
7. Are genres a single field or multiple tags?
8. Can packages have different creative stages?
9. Which add-on pricing rules are needed in the first version?
10. How should cancellation, refunds, payment corrections, and historical record deletion work?
11. Are final artwork attachments needed in the first version?
12. What currency rules apply? PHP is used for examples; multiple-currency conversion is not planned.
13. Are the proposed color values and DynaPuff/Andika typography assignments ready to adopt?
14. Should commission intake use the proposed four Next/Back steps, or a single scrolling form?

Resolve these through planning as they become relevant. They do not block drafting the dashboard's static structure.
