# Roadmap

This roadmap describes the order of work. It does not claim that a feature is implemented. The original proposal's calendar is background; no replacement deadlines have been agreed.

## Phase 1: Product planning

- [x] Record the core commission-management idea.
- [x] Record the requested dashboard, intake, history, detail, and reporting features.
- [x] Choose Medium Phone as the sizing reference.
- [x] Record Guhit as the visual reference.
- [x] Establish static boards as the visual references; subsequently add the requested clickable wireframe for frontend handoff.
- [x] Describe the general database information groups.
- [ ] Agree on definitions for current work, queue, and unfinished tasks.
- [ ] Review suggested supporting features and explicitly accept or defer them.
- [ ] Agree on the main section names and navigation.

## Phase 2: Wireframes and frontend handoff — current

- [x] Review the refined dashboard as the visual reference.
- [x] Draw the dashboard at Medium Phone proportions.
- [x] Draw and review the four-step Add commission wireframe.
- [x] Draft the Commissions list with Current, Queued, Completed, and empty states.
- [ ] Review the Commissions list and transaction-only main History split.
- [x] Draft Commission Details with current, queued, and completed states.
- [ ] Review the Commission Details board.
- [x] Draft Update progress and completion confirmation with an unchanged outstanding balance.
- [ ] Review the progress/completion draft and completion meaning.
- [x] Draft four-step Edit commission with prefilled values, changed-date review, and preserved payment/work state.
- [ ] Review edit validation, price/history implications, and discard behavior.
- [x] Draft the image viewer with reference/final-artwork variants and return-to-origin behavior.
- [ ] Review image viewing controls, gesture behavior, and image-load errors.
- [x] Draft Record payment and its saved history state.
- [x] Draft a commission-specific payment-history list and simplify the checklist to remove task dates/actions.
- [x] Organize all existing boards in layout.md with numbered pages, button routes, scroll/state explanations, and saved previews/sources.
- [x] Draft main transaction History with received-date groups, monthly total, commission links, and an empty month.
- [ ] Review the main History draft, period selection, and return navigation.
- [x] Draft Insights with monthly/all-time received earnings, six-month trend, type breakdown, and current balances.
- [ ] Review Insights scopes, partial-month labels, scroll continuation, and links to History/commission records.
- [x] Synchronize five-tab navigation across saved main boards and the clickable file.
- [x] Add the separate Reconnect board and document the frontend/algorithm-team boundary.
- [x] Export the single-file clickable wireframe and frontend handoff guide.
- [x] Connect All tasks and core supporting dialogs in the clickable demo.
- [ ] Finish separate static All tasks and remaining loading/error/edge-state drawings.
- [ ] Review the screens together as one consistent static board.

**Exit condition:** the owner agrees on the screen contents, hierarchy, labels, and major flows. A working prototype is not required to finish this phase.

## Phase 3: Technical design — handoff draft started

- [x] Draft [database-guide.md](../database-guide.md) with a proposed SQLite model, queries, write rules, and backend acceptance cases.
- [x] Draft [reconnect-algo.md](../reconnect-algo.md) with the recommendation contract, simple baseline, and algorithm-team review points.

- [ ] Confirm the Android project setup and compatible tooling.
- [ ] Finalize Kotlin/XML/Material implementation choices.
- [ ] Review/finalize the proposed SQLite model and its marked defaults with the backend team.
- [ ] Decide image storage and access behavior.
- [ ] Define quotation, payment, and reporting rules.
- [ ] Resolve historical record retention and editing behavior.
- [ ] Choose an Activity/navigation structure consistent with the academic requirements.

## Phase 4: Android implementation — later

- [ ] Create the native layouts from the agreed wireframes.
- [ ] Add navigation and form validation.
- [ ] Implement package and commission storage and management.
- [ ] Implement references, tasks, and progress updates.
- [ ] Implement payment records and balances.
- [ ] Implement history, earnings calculations, and graphs.

These are intended implementation milestones, not authorization to begin coding while the current task remains static wireframe planning.

## Phase 5: Validation and academic documentation — later

- [ ] Verify complete commission and payment journeys.
- [ ] Verify that deposits/balances are counted in the correct month.
- [ ] Verify completed-but-unpaid, cancelled, overdue, and empty states.
- [ ] Verify persistence, image access, layout behavior, and accessibility.
- [ ] Prepare the course's required test cases and documented improvements when implementation exists.
- [ ] Document what was built, remaining limitations, and how AI assistance was reviewed.

The clickable wireframe has browser-level interaction/layout checks and fictional session-only state. No native Android application tests or Android build checks exist yet.
