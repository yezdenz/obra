# Design direction and wireframe format

## Reference

The owner selected [Guhit](https://app.guhit.app/) because of its **fonts, shapes, and simplicity**.

The owner has selected **[DynaPuff](https://fonts.google.com/specimen/DynaPuff)** for OBRA's playful interface text, including **Monthly earnings** and task titles such as **Finish lineart**. Use it for headings, summary labels, task/commission titles, and primary action labels. This is the selected font for the current draft.

**Andika** is the proposed supporting font for usernames, descriptions, input text, dates, payment figures, and other reading-heavy details. The desired direction includes warm light backgrounds, dark readable text, rounded and subtly irregular shapes, restrained accent colors, and clear spacing.

See [layout.md](../layout.md) for the typography assignments, proposed color values, spacing, and action-by-action screen behavior. The exact palette and Next-button flow are planning proposals rather than implemented behavior.

This is inspiration for OBRA's own visual system. Do not copy Guhit's branding, illustrations, mascots, or page content. Font distribution and licensing must be checked if font files are eventually bundled.

## Current fidelity

Create a **static planning board** with labeled phone screens, comparable to the multi-screen wireframe example supplied by the owner.

- Use simple containers, short labels, and image placeholders.
- Keep the phone proportions consistent across screens.
- Discuss content order and placement before polishing decoration.
- Put annotations outside the phone when needed.
- Show one recognizable state per screen.
- Use fictional names, amounts, and artwork placeholders.
- Keep represented controls static during this phase; do not build executable navigation or transitions.

The earlier interactive exploration does not define the current deliverable. A static dashboard draft has been explored in the planning conversation, but its hierarchy has not received final approval and it is not an implemented app screen.

## Device reference

Use the **Android Studio Medium Phone** portrait profile as configured on the owner's computer:

| Property | Reference |
| --- | --- |
| Physical screen | 1080 x 2400 pixels |
| Configured density | 420 dpi |
| Approximate logical size | 411 x 914 dp |
| Orientation | Portrait |

These are the observed local emulator settings, not a claim that every installation uses identical settings. Account for Android status/navigation areas and, later, the keyboard. Physical pixels are not the size unit for Android layout elements.

## Product presentation

- Favor clear labels over unexplained icon-only controls.
- Make client usernames, progress, and dates easy to scan.
- Indicate deadlines and payment states with words as well as color.
- Keep one main add action obvious.
- Give the detailed financial graphs room in Insights.
- Keep long text and forms scrollable in the eventual app rather than making type excessively small.
- Preserve a simple artist workspace as the screen set grows.

## Planned review order

1. Dashboard content and hierarchy.
2. Main navigation.
3. Commission intake form.
4. Individual commission details and progress/payment presentation.
5. History.
6. Insights.
7. Supporting forms, dialogs, and empty/edge states.
