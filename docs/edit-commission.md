# Edit commission — static wireframe draft

## Purpose and board

Edit commission updates an existing artwork record using the accepted intake's four groups: client, artwork, price/dates, and review. It keeps the DynaPuff/Mali pairing, warm palette, soft shapes, and Android Studio Medium Phone proportions. This is a new static proposal for review; controls and transitions are not implemented.

Enter from WF-07 Commission Details → Edit. The board reads **top left → top right → bottom left → bottom right**:

| Position | Number | Step | Primary action |
| --- | --- | --- | --- |
| Top left | WF-14 | Client details | Next: Artwork → WF-15 |
| Top right | WF-15 | Artwork brief | Next: Price & dates → WF-16 |
| Bottom left | WF-16 | Price & dates | Review changes → WF-17 |
| Bottom right | WF-17 | Review changes | Save changes → the same WF-07 record |

These are four separate form steps, not scroll positions. Each can scroll with the keyboard, longer text, or larger type; its continuation action stays reachable. Main navigation is omitted. See [layout.md](../layout.md#wf14) and the [saved board](wireframes/edit-commission.jpg).

## Example and change summary

The fictional Fantasy portrait for Mika, `mika.draws` on Discord, is In progress at Lineart. It has a Portrait brief, Fantasy/Soft light tags, and two saved reference placeholders. Commission date is Nov 1, planned start Nov 3, original due date Nov 14, 2026. Agreed total is ₱1,800; a ₱900 deposit received Nov 2 leaves ₱900 outstanding.

The only edit shown is **due date Nov 14 → Nov 16, 2026**. WF-16 labels the field Changed and shows its previous value. WF-17 shows one change with the before/after dates above a full review. Other values are prefilled rather than re-entered.

This follows the earlier partly paid Details snapshot. The main History/Insights examples show a later/alternative fully paid moment; those drawings are not live synchronized records. Editing a deadline changes neither receipts nor monthly earnings.

## WF-14: client details

Prefill contact platform, username, and optional display name. The small status strip identifies the existing artwork's In progress / Lineart context. It is display-only, not a status editor.

Next checks the same applicable username/platform requirements as intake and retains a draft for this record. Client changes apply to this commission only; no mass update of other artwork records is proposed. Historical display-name/username snapshots in payment reporting remain an open design choice.

## WF-15: artwork brief

Prefill project title, type, genre/style tags, pose/details, and saved references. The pictured two references are image placeholders. Add image opens the eventual device picker; a reference thumbnail opens [WF-18, the larger viewer](image-viewer.md). Back preserves this edit draft. The remove cross stages removal from this commission's draft; it does not delete an image immediately or affect other records.

Changing a type does not silently replace the saved agreed price or reset creative progress/checklists. Next preserves the draft and opens WF-16. The viewer is now drawn; device-picker and image-removal states remain to draw.

## WF-16: price and dates

Show the saved Agreed total, a read-only Already received value, remaining balance preview, commission date, optional planned start, and due date. Keep the saved agreed amount when opening edit; do not recalculate it from a package's current base price.

There is **no Record initial payment toggle** in this edit form. Existing receipt amounts, dates, methods, and notes remain in the separate payment flows. Received total is read-only here; an agreed-price edit changes the remaining-balance preview, not the received amount or earnings.

Proposed validation: retain applicable intake amount/date-order checks and prevent saving an agreed total below money already received. Handling refunds or correcting receipts remains a separate unresolved flow; editing must not invent a negative balance or erase history to make a new quote fit. A price change equal to received money may make the payment state Fully paid without creating another receipt or completing artwork. The below-received error state and price-change review example are still undrawn.

Planned start remains a planning date; editing it does not start/restart work or rewrite an actual started/finished date. Date-change restrictions for records with existing receipts or finished work need review before implementation.

## WF-17: review and Save changes

Review lists the changed values followed by Client, Artwork, Schedule, and Price & payment. Use these section links:

| Edit link | Destination |
| --- | --- |
| Client | WF-14 with the existing draft |
| Artwork | WF-15 with the existing draft |
| Schedule | WF-16 with the existing draft |
| Price & payment | WF-16; received-payment data remains read-only |

Continue through the remaining steps to return to Review changes. Changed fields are compared with the saved record, so changing a value back removes that change. Multiple-change and no-change review variants remain undrawn; with no effective change, disable Save changes.

Save changes is proposed to validate the full draft and update the **existing commission once**, then return to WF-07 with brief Commission updated feedback. It does not create another commission, put it back in Queued, change its creative stage, check tasks, create payments, or alter received earnings. Preserve its actual finish date if present. An explicitly changed agreed price can affect its calculated payment status/balance; existing receipts stay intact.

The saved result is the existing Commission Details page with updated values, not a new success page. If saving fails, retain the review/draft without changing the saved record or showing success. Prevent duplicate submission while saving. The board depicts an In-progress record; completed/cancelled editing restrictions and variants remain open for review.

## Back, Close, and discard

- Back on WF-15/WF-16/WF-17 returns to the preceding edit step while keeping changes.
- Back on WF-14, or Close on any step, returns directly to the originating WF-07 if no effective edits exist.
- With edits, show Discard changes? with Keep editing and Discard. This supporting dialog is still undrawn.
- Keep editing preserves the current step and its values. Discard abandons only this draft; the saved commission, payments, and references remain intact.
- Next and Review changes never commit part of the edit.

## Remaining states

Discard dialog, missing/invalid inputs, a quote below received money, changing price/type, reference replacement/removal, multiple changes, no changes, save failure, and completed/cancelled edit variants remain to draw. Actual Android forms, persistence, validation code, and transitions are outside this planning task.
