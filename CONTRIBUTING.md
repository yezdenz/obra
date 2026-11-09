# Contributing to OBRA

## Current phase

Work on planning documents, static boards, and the owner-requested clickable phone wireframe for frontend handoff. The owner authorized synchronization to GitHub on November 10, 2026. Begin native Android implementation after the wireframe and technical-design phases are agreed.

## Planning changes

1. Read the README and the document relevant to the change.
2. State whether a change is an owner requirement, an agreed decision, or a suggestion.
3. Keep the dashboard, screen inventory, data plan, and roadmap consistent.
4. Record unresolved choices in `docs/decisions.md` rather than presenting guesses as settled behavior.
5. Use fictional clients, payment amounts, and reference images in examples.
6. Reference external design inspiration with a link; do not copy branding or artwork.

For wireframes, use the numbered catalogue in `layout.md` and the saved assets in `docs/wireframes/`. When a source drawing changes, refresh its standalone export and preview alongside the documented routes/states. Keep a scroll continuation or filter state under its existing page number, and label undrawn destinations as pending.

For a layout proposal, include screen name, information order, device size, relevant states, and a sketch when available. Keep static boards static; maintain the clickable source and standalone export separately. Use five-tab navigation consistently across the main boards and clickable screens. Do not implement the recommendation algorithm when changing its UI; use clearly documented fixtures.

## Review expectations

- Describe the concrete planning question or proposed change.
- Make screenshots or sketches legible at Medium Phone proportions.
- Explain any change to payment calculations or historical record handling.
- Check Markdown links and terminology before submitting.
- Verify affected clickable flows, disabled states, and shared values. Do not claim native Android build/test results for an app that does not yet exist.

## Git commit standards

All repository commits must follow the per-file distinct description rules documented in [AUTO_COMMIT.md](AUTO_COMMIT.md). Conventional commit prefixes (`feat:`, `fix:`) and blanket commits are prohibited.

## Files to keep out of Git

Do not commit credentials, personal client information, actual transaction data, private reference artwork, local databases, Android signing keys, machine-specific configuration, or generated build files.

The owner has not selected a software license. Do not add one or redistribute third-party font/image assets without checking their rights and obtaining an appropriate project decision.
