# Update progress and completion — static draft

This board continues the accepted DynaPuff/Mali direction at Android Studio Medium Phone proportions. It is a new proposal for review. Controls are drawings; no navigation or saving is implemented.

## Board and numbering

| Position | Reference | Purpose |
| --- | --- | --- |
| Top left | WF-10A | Update progress with Colors selected |
| Top right | WF-10B | Same page with Finished selected instead |
| Bottom left | WF-11 | Completion confirmation with actual finished date |
| Bottom right | WF-07C | Existing record after completion, still partly paid |

The top pair are alternative choices from the same saved Lineart stage. They are not a required two-page sequence. See [the numbered guide](../layout.md#wf10) and [saved board](wireframes/update-progress.jpg).

## Stage selection: WF-10

Open from Commission Details → Update progress. Display project/client, In progress status, commission deadline, saved stage, and four radio choices: Sketch, Lineart, Colors, Finished. Preselect the saved stage when first opened. The board illustrates a changed, unsaved choice.

- Sketch, Lineart, or Colors: Save progress stores the choice and returns to the same WF-07 record. Work remains In progress.
- Finished: the primary label becomes Review completion and opens WF-11. Merely choosing it does not complete the commission.
- Unchanged choice: Save progress is disabled; use Back to leave.
- Back/Close with changes uses the shared Keep editing / Discard pattern; without changes it returns directly to the originating record.

For this draft, an artist may select a non-final stage directly, including an earlier stage when reworking artwork. Changes apply only when saved. Queued work first uses Start commission; reopening completed work is outside this draft. Package-specific stages remain an open choice.

This is a separate page with no main navigation. Allow vertical scrolling on smaller displays or with larger text and keep the action reachable. It adds no task deadlines, task editors, or payment inputs. Checklist completion stays independent from stage changes.

## Completion confirmation: WF-11

Use a full review page showing project/client, original deadline, Finished date, agreed total, received amount, and outstanding balance. Finished date defaults to today. Proposed checks require a real date from the commission date through today; original due dates remain unchanged even for late completion.

- Mark completed saves once, records the actual finished date, changes creative stage to Finished and lifecycle to Completed, then returns to the same record as WF-07C.
- Keep working or Back returns to WF-10 with the draft Finished choice intact; nothing is committed.
- Close returns to the originating record, with shared discard handling if needed.
- Failed saves preserve the draft; do not show completed feedback or change the record.

The result is a state of Commission Details, not a new success page. Brief feedback says Commission completed. The artwork moves from Current to Commissions → Completed. Original references, brief, notes, checklist states, deadline, and received payments remain available. Finishing work does not automatically complete unfinished checklist items.

An uploaded final image is not required by this proposal. Whether completion should require delivery or client acceptance, whether final attachments are needed, and reopening finished artwork remain review questions.

## Example and payment separation

Fantasy portrait, mika.draws on Discord, has an agreed price of ₱1,800 and a ₱900 deposit received Nov 2. Its original deadline is Nov 14, 2026; this board records completion Nov 9. The remaining ₱900 is visible both before and after completion. Record payment remains available afterward.

This is a fictional alternative workflow snapshot to the Fully paid result on the Record payment board. Completing work creates no transaction and changes no received-earnings totals. Payment history opens WF-08; Record payment opens WF-09. Completing fully paid work would keep the balance at zero.

## Remaining visual states

Unchanged-stage disabled action, invalid/future finish date, discard dialog, save failure, fully paid completion, and longer titles/larger text remain to be drawn. No animation or functioning prototype is included.
