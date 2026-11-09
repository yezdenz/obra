# Decisions and open questions

## Established direction

| Topic | Direction | Basis |
| --- | --- | --- |
| Product | OBRA, a personal workspace for freelance artist commissions | Original proposal and planning conversation |
| Current task | Synchronize saved boards, clickable phone wireframe, and frontend handoff; native Android remains later | Owner authorization, November 10, 2026 |
| Main navigation | Home, Commissions, History, Insights, Reconnect | Owner requested a separate recommendation section to keep Insights focused |
| Recommendation scope | Commission type and genre only; algorithm handled by designated teammates | Explicit owner request; no image processing |
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
| Data handoff | General overview retained; detailed SQLite and recommendation guides now requested for backend/algorithm teammates | Earlier general-only request followed by owner request for database-guide.md and reconnect-algo.md |

## Working proposals to review

- Detailed spacing and platform implementation for the five-section navigation baseline; small clickable layouts shorten Commissions to Work without changing its route.
- [Commissions list](commissions-list.md) with Current, Queued, and Completed artwork filters; main History reserved for individual payment transactions, avoiding a duplicate artwork archive.
- Exact color values, type sizes, and detailed field/pricing rules described in [layout.md](../layout.md).
- The new [Commission Details board](commission-details.md), including action placement and state-specific summaries.
- [Commission payment history](payment-history.md) as a simple per-commission receipt list, separate from Insights; additional task-detail/custom-entry controls are deferred.
- [Record payment](record-payment.md): a short amount/date/method/note form, positive amount within the remaining balance, and return to updated history after Save.
- [Update progress and completion](update-progress.md): WF-10 full-page stage selection; Finished opens WF-11 to confirm actual finished date; completion returns to the same record and retains its payment/checklist states. This is a new draft, not an accepted completion definition.
- [Main History](main-history.md): WF-12 shows individual received payments grouped by received date, a month/year selector, full-period received total/count, links to commission records, and an empty month. This is a new static draft for review; the picker remains undrawn.
- [Insights](insights.md): WF-13 is one scrolling page with monthly earnings, all-time received earnings, current balances, a six-month trend, and type breakdown. View payments carries its month to History; See balances scrolls within Insights. A new draft for review; all-time/current metrics retain their scopes when selecting a past month.
- [Edit commission](edit-commission.md): WF-14–WF-17 reuse the four-step intake with prefilled values and before/after review. Save changes updates the same record, preserves work/checklist states and receipt records, and omits initial-payment entry. A total below already received money is proposed to be blocked; historical restrictions and refund/correction flows remain open. New static draft for review.
- [Image viewer](image-viewer.md): WF-18 is one dedicated preview page with reference/final-artwork variants, fit/zoom controls, same-page image selection, and Back preserving the originating record/form. Earlier boards contain thumbnails rather than this full-size destination. New static draft for review; no final-attachment requirement added.
- Lifecycle statuses: Queued, In progress, Completed, Cancelled.
- Creative stages tracked independently from lifecycle and payments.
- Payment records with received amounts and dates; monthly earnings calculated from them.
- Simple per-commission checklist items with completion states; per-task dates and extra task-detail/custom-entry controls are excluded from the current draft.
- Package management, add-on quotation, search, deadline warnings, and queue ordering.
- [SQLite handoff](../database-guide.md): one local workspace, PHP integer-centavo amounts, exact platform/username client identity, one optional primary genre, quote snapshots, and append-only received payments. These make the proposed schema concrete; they do not resolve the open product policies automatically.
- [Reconnect algorithm handoff](../reconnect-algo.md): exact type AND genre, completed history, exclude the selected client, deduplicate by client using latest matching artwork, recent-first order, and a proposed five-client limit. Filtering alone is not backtracking; confirm any academic algorithm requirement separately.

These are recommendations captured for discussion. Inclusion in this repository does not make them approved implementation requirements.

## Original proposal items requiring reconciliation

The owner has now explicitly restored previous-client recommendations as Reconnect, using genre and commission type. The UI uses fixtures while the assigned teammates handle the algorithm. A separate artwork-library feature remains deferred.

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
14. Review the expanded initial-payment and invalid-input states demonstrated by the clickable file.
15. Recommendation teammates must decide matching/backtracking details, ordering, partial matches, duplicate clients, and clients with active work; the frontend only displays returned results/reasons.

Resolve these through planning as they become relevant. They do not block drafting the dashboard's static structure.
