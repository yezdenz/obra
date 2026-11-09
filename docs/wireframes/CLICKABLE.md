# Open and review the clickable wireframe

Download/clone this repository, then open **[clickable.html](clickable.html)** in a browser. GitHub displays HTML source; it does not run the file inside the repository page. No build, local server, package installation, account, or website deployment is required. Fonts may need an internet connection.

The [editable source](source/clickable.html) preserves the in-chat wireframe. The standalone file is its exported wrapper, including the local click/Enter form handling used by the conversation sandbox. Use [layout.md](../../layout.md) as the frontend behavior/style specification.

## Start here

- The current opening page is **WF-19 Reconnect**. Home and all five main tabs remain accessible.
- The screen picker above the phone jumps to WF-01–WF-20 and their filter/scroll variants. Restart restores the fictional demo after confirmation.
- Use the phone's Back button to return to the originating record/list/form. The picker/Restart are review tools, not product navigation.
- Forms have real demo Next/Back/Review/Save interactions. Blank/invalid fields show errors. Closing changed input offers Keep editing / Discard.
- Reference images can be selected locally and previewed; the bundled example images are geometric placeholders.
- Payments, progress, editing, checklists, reports, and navigation change only the in-memory demo. Nothing is saved to GitHub or sent to a payment/contact service.
- Reload or Restart resets the demo. No account, backend, real persistence, or recommendation algorithm is supplied.

## Five main sections

**Home → Commissions → History → Insights → Reconnect**, in that fixed bottom-tab order. The Commissions label shortens to Work at the narrowest layout without changing its destination. Contextual detail/form/viewer pages use Back rather than adding these tabs.

Insights contains financial summaries and charts. Reconnect has its own page with a current-work selector, type/genre context, suggested-client cards, previous-artwork link, and no-match state. Suggestions are fixed UI fixtures for the algorithm team to replace, not calculated matches. No image processing or automatic messaging is included.

The All tasks destination is numbered **WF-20** in the handoff. Its separate static board has not been drawn; its clickable list and empty state are available here.

## Demo data and states

The demo clock is fixed to November 10, 2026. Baseline November received income is ₱4,450, all-time received income ₱24,000, and current unpaid balances ₱7,550. These values come from one demo data set, unlike static boards that illustrate different workflow moments.

Use Fantasy portrait for fully paid but unfinished work; Cozy café illustration for Completed work still owed money; Half-body illustration to record a later payment. Reconnect's Fantasy portrait example suggests @tala.arts and opens Autumn portrait; selecting Half-body illustration illustrates no matches.

Some buttons are intentionally disabled: Record payment at zero balance, Save progress with an unchanged stage, Save changes with no edits, and viewer paging beyond the first/last image. The empty-list picker entry is a visual scenario rather than deletion of all demo records. Image selection is limited to six local images of 1 MB each for this demonstration.

For frontend implementation, retain the routes, visible states, visual hierarchy, and work/payment separation from layout.md. Replace demo fixtures with the implementation's state/data layer, use the real device date, and have the algorithm team supply Reconnect results. The exported HTML is a review reference, not prescribed Android architecture.
