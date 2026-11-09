# OBRA

**A personal Android studio assistant for freelance artists.**

OBRA brings commission requests, reference images, deadlines, creative progress, client payments, and earnings summaries into one place. It is designed for artists who receive requests through platforms such as X and Discord and need a clear view of their work and income.

The name comes from the Filipino word *obra*, meaning a work of art.

> **Current phase: planning and static wireframing.**
> This repository currently contains the project plan. The dashboard and four-step Add commission direction have been reviewed; Commission Details, payment views, and the Commissions list have static drafts. An Android app, working navigation, transitions, database, and payment processing have not been implemented.

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

## Planning rules

- **Plan first:** agree on content, layout, terminology, and screen hierarchy before implementation.
- **Static wireframes:** use a board of simple phone screens, placeholders, and labels. Working buttons and transitions are outside the current phase.
- **Dashboard first:** settle its structure before detailing the remaining screens.
- **Medium Phone reference:** portrait, 1080 x 2400 physical pixels at 420 dpi, approximately **411 x 914 dp**, based on the local Android Studio emulator profile.
- **Visual direction:** use [Guhit](https://app.guhit.app/) as inspiration for typography, rounded shapes, warmth, and simplicity. Build OBRA's own interface and assets.
- **Accepted font pairing:** DynaPuff for playful headings, summary labels such as Monthly earnings, task titles such as Finish lineart, and primary actions; Mali for supporting details, dates, links, and figures.
- **Latest scope takes priority:** the feature list recorded in these documents reflects the planning conversation; the original proposal provides background and an intended technical direction.

## Proposed application structure

| Section | Purpose |
| --- | --- |
| Home | Dashboard with earnings, workload, tasks, and queue previews |
| Commissions | Artwork records under Current, Queued, and Completed; individual details. Cancelled access remains a later proposal |
| History | Individual payment transactions across commissions, linked to their artwork records |
| Insights | Total/monthly earnings, balances, and data graphs |

This four-section navigation and the transaction-only History split are **proposals for review**, not approved final layouts. Add/edit commission screens open from these sections. Package management remains optional; extra task-detail/custom-entry screens are deferred under the simplified checklist direction.

## Documentation

| Document | Contents |
| --- | --- |
| [Layout and interaction plan](layout.md) | DynaPuff usage, proposed color palette, dashboard actions, and step-by-step Next/Back button flows |
| [Feature requirements](docs/requirements.md) | Requested features, supporting suggestions, boundaries, and expected behavior |
| [Dashboard plan](docs/dashboard.md) | Dashboard sections, reading order, terminology, and states to design |
| [Commissions list](docs/commissions-list.md) | Current, Queued, Completed, and empty states; the distinction between artwork records and payment History |
| [Add commission wireframe](docs/add-commission.md) | Four static intake steps, reference images, dates, optional initial payment, and final review |
| [Commission Details wireframe](docs/commission-details.md) | Current, queued, and completed records; scroll continuation; tasks, stages, dates, and balances |
| [Commission payment history](docs/payment-history.md) | Initial deposit and later received payments for one commission, separate from earnings graphs |
| [Record payment wireframe](docs/record-payment.md) | Amount/date/method/note entry, balance preview, Save behavior, and updated history |
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
2. Review the detail/payment drafts, then draw Update progress and image views.
3. Extend the accepted visual direction to the main transaction History and Insights.
4. Review important empty, overdue, partially paid, and completed states together.
5. Finalize the data model and Android implementation plan afterward.

## Repository status

Documentation only. There is no application build or automated test suite yet. Example clients and money amounts in planning materials must remain fictional.

No software license has been selected. No third-party branding, illustrations, font files, proposal PDF, personal client records, or credentials are included.
