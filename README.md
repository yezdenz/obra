# OBRA

**A personal Android studio assistant for freelance artists.**

OBRA brings commission requests, reference images, deadlines, creative progress, client payments, and earnings summaries into one place. It is designed for artists who receive requests through platforms such as X and Discord and need a clear view of their work and income.

The name comes from the Filipino word *obra*, meaning a work of art.

> **Current phase: wireframe review and frontend handoff.**
> The repository includes saved visual boards and the [clickable phone wireframe](docs/wireframes/CLICKABLE.md), synchronized to **Home, Commissions, History, Insights, Reconnect**. The native Android frontend, real persistence/payment processing, and recommendation algorithm are not implemented by these review files.

Start with [layout.md](layout.md), then download/open [the clickable HTML](docs/wireframes/clickable.html) using its [instructions](docs/wireframes/CLICKABLE.md). GitHub displays HTML source; the file runs after opening locally. No website deployment, build, or installation is needed.

## What we want to build

### 1. Dashboard

A studio overview showing:

- Payments received during the selected month.
- Current commissions and their creative stages.
- Unfinished checklist items and upcoming commission deadlines.
- Queued commissions and the next project to start.
- A clear action for adding a commission.

The dashboard should show enough information to decide what to work on next. Detailed financial graphs belong in a separate Insights section.

### 2. Commission intake

Record a client's username and whether they use X or Discord, then describe their request. The form should support full body, half body, chibi, and portrait packages; genre/style tags; requested poses; reference images; notes; agreed pricing; and relevant dates.

### 3. Commission management

Review individual **queued, current, and previous commissions**. Each record should keep its client, brief, references, pricing, payment history, dates, and progress together. Updating progress must not overwrite payment information.

### 4. Payment transaction history

The current section proposal uses History for received payments across commissions, including deposits and later balance payments. Previous artwork stays under Commissions → Completed. Each payment can lead back to its related commission, including that project's original deadline and actual finish date. A completed commission may still have an unpaid balance.

### 5. Earnings and insights

Show total earnings, monthly earnings, outstanding balances, and earnings graphs. Earnings are based on recorded payments received, not the total value of all accepted requests. These are income summaries; expense tracking and profit calculations are not part of the initial plan.

### 6. Reconnect with previous clients

A separate recommendations section uses current commission type and genre to suggest previous clients and link to their completed artwork. The frontend displays supplied suggestions; the assigned teammates implement the algorithm/backtracking. The wireframe uses fixed sample results, with no image processing or automatic outreach.

## Planning rules

- **Plan first:** agree on content, layout, terminology, and screen hierarchy before implementation.
- **Wireframe handoff:** keep static boards for visual review and use the clickable phone wireframe for navigation/state examples. This does not begin native Android implementation.
- **Dashboard first:** settle its structure before detailing the remaining screens.
- **Medium Phone reference:** portrait, 1080 x 2400 physical pixels at 420 dpi, approximately **411 x 914 dp**, based on the local Android Studio emulator profile.
- **Visual direction:** use [Guhit](https://app.guhit.app/) as inspiration for typography, rounded shapes, warmth, and simplicity. Build OBRA's own interface and assets.
- **Accepted font pairing:** DynaPuff for playful headings, summary labels such as Monthly earnings, task titles such as Finish lineart, and primary actions; Mali for supporting details, dates, links, and figures.
- **Latest scope takes priority:** the feature list recorded in these documents reflects the planning conversation; the original proposal provides background and an intended technical direction.

## Proposed application structure

| Section | Purpose |
| --- | --- |
| Home | Dashboard with earnings, workload, tasks, and queue previews |
| Commissions | Artwork records under Current, Queued, and Completed; individual details. The clickable demo includes Cancelled access while final cancellation policy remains open |
| History | Individual payment transactions across commissions, linked to their artwork records |
| Insights | Total/monthly earnings, balances, and data graphs |
| Reconnect | Previous-client suggestions using commission type and genre, with links to prior artwork |

This five-section order is the current wireframe baseline. Add/edit and other contextual screens open from these sections and return using Back. Package management remains optional; extra task-detail/custom-entry screens are deferred under the simplified checklist direction.

## Documentation

| Document | Contents |
| --- | --- |
| [Frontend handoff and layout guide](layout.md) | WF-01–WF-20 catalogue, five-tab navigation, button routes, state/scroll contracts, style rules, and acceptance walkthrough |
| [Saved wireframe boards](docs/wireframes/README.md) | Current preview images, standalone static HTML exports, and editable source fragments |
| [Clickable wireframe instructions](docs/wireframes/CLICKABLE.md) | How to open the single-file wireframe, explore screens, reset samples, and distinguish demo behavior from implementation |
| [Reconnect handoff](docs/reconnect.md) | Recommendation UI, previous-artwork links, empty/loading/error states, and the boundary with the algorithm team |
| [Feature requirements](docs/requirements.md) | Requested features, supporting suggestions, boundaries, and expected behavior |
| [Dashboard plan](docs/dashboard.md) | Dashboard sections, reading order, terminology, and states to design |
| [Commissions list](docs/commissions-list.md) | Current, Queued, Completed, and empty states; the distinction between artwork records and payment History |
| [Add commission wireframe](docs/add-commission.md) | Four static intake steps, reference images, dates, optional initial payment, and final review |
| [Edit commission wireframe](docs/edit-commission.md) | Four prefilled edit steps, changed-value review, preserved receipts/work status, and return to the same artwork |
| [Image viewer](docs/image-viewer.md) | Larger reference/final-artwork preview, viewing controls, image selection, and return to the originating record/form |
| [Commission Details wireframe](docs/commission-details.md) | Current, queued, and completed records; scroll continuation; tasks, stages, dates, and balances |
| [Commission payment history](docs/payment-history.md) | Initial deposit and later received payments for one commission, separate from earnings graphs |
| [Record payment wireframe](docs/record-payment.md) | Amount/date/method/note entry, balance preview, Save behavior, and updated history |
| [Update progress and completion](docs/update-progress.md) | Stage choices, finished-date confirmation, and the completed record with payments kept independent |
| [Main History wireframe](docs/main-history.md) | Received payments across commissions, month selector, per-period totals, populated/empty states, and links to artwork records |
| [Insights wireframe](docs/insights.md) | Monthly/all-time received earnings, six-month trend, type breakdown, current balances, and scroll/button routes |
| [Screen plan](docs/screens.md) | Screen inventory and proposed navigation |
| [Data plan](docs/data-plan.md) | General database information groups without a detailed schema |
| [Design direction](docs/design-direction.md) | Guhit reference, fonts, shapes, device size, and wireframe format |
| [Roadmap](docs/roadmap.md) | Planning, wireframing, implementation, and validation phases |
| [Decisions and open questions](docs/decisions.md) | Confirmed direction, proposals, and choices still to make |
| [Contributing](CONTRIBUTING.md) | How to update the plan and distinguish decisions from suggestions |

## Intended technology direction

The original academic proposal names **Android Studio, Kotlin, XML layouts with Material components, and local SQLite storage**. These describe the intended eventual Android implementation. Specific libraries, versions, schema, and architecture will be chosen after the wireframes are agreed.

The initial concept is a workspace for the artist. It does not require a client-facing portal, live X/Discord integration, cloud synchronization, or actual money transfers. Recording a username or payment does not connect to an external platform or process a transaction.

## Immediate next steps

1. Review the Commissions list and its Current, Queued, and Completed filters.
2. Review the detail/payment, progress/completion, edit, and image-viewer journeys in the clickable file.
3. Review main History and Insights together for consistent received-income definitions.
4. Review Reconnect with the algorithm team; review All tasks and remaining loading/error/edge states before implementation.
5. Finalize the data model and Android implementation plan afterward.

## Repository status

Planning documents, saved visual boards, and a clickable in-memory phone wireframe. There is no native Android application build or Android test suite yet. Clients, artwork, and money amounts in review files are fictional.

No software license has been selected. No third-party branding, illustrations, font files, proposal PDF, personal client records, or credentials are included.
