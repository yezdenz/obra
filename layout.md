# OBRA layout and interaction plan

## Purpose and current scope

This document defines what to draw in the static wireframes, which fonts and colors to use, and what each represented button should eventually do. It is a planning specification, not an interactive prototype or implemented Android behavior.

Start with the dashboard. Use the remaining flows to keep later screens consistent with it.

**Confirmed font pairing:** the owner selected **[DynaPuff](https://fonts.google.com/specimen/DynaPuff)** for playful interface text, specifically labels such as **Monthly earnings** and task titles such as **Finish lineart**, and accepted **[Mali](https://fonts.google.com/specimen/Mali)** for dates, links, figures, and other supporting text after reviewing the dashboard.

**Proposed details:** the color values, type sizes, four-step commission form, and individual button destinations below are a coherent first proposal for review. They are not all approved design decisions.

## 1. Phone layout

| Item | Planning reference |
| --- | --- |
| Device | Android Studio Medium Phone, portrait |
| Logical canvas | Approximately 411 x 914 dp, including system areas |
| Physical reference | 1080 x 2400 pixels at the observed 420 dpi configuration |
| Side margins | 20 dp; allow 16 dp where a denser form needs room |
| Spacing | 8 dp between closely related items; 16 dp within sections; 24 dp between major sections |
| Cards | Soft, slightly irregular corners around 20-24 dp; light outlines |
| Form fields | Consistent rounded corners around 14-16 dp |
| Primary actions | Rounded, visibly labeled, about 48-52 dp tall |
| Touch areas | Plan for at least 48 x 48 dp around actionable controls |
| Long content | Scroll the content; keep navigation and important actions reachable |

Use dp for layout and sp for text in the future Android implementation. Show status/navigation areas in the wireframes without assuming that all 914 dp is available for app content. Account for bottom system insets and the keyboard in later detailed layouts.

## 2. Typography

### Primary font: DynaPuff

Use DynaPuff for the recognizable, playful parts of the interface:

| Element | Example | Proposed weight and size |
| --- | --- | --- |
| OBRA name | obra | SemiBold, 32-36 sp |
| Screen title | Commission details | SemiBold, 24-28 sp |
| Section title | Current work, Coming up | Medium/SemiBold, 20-22 sp |
| Summary label | Monthly earnings | Medium, 16-18 sp |
| Task title | Finish lineart, Confirm reference pose | Medium, 16 sp |
| Commission card title | Half-body illustration | Medium, 16 sp |
| Main action | Add commission, Save commission | Medium/SemiBold, 16-18 sp |
| Step heading | Client details, Artwork brief | Medium/SemiBold, 20-24 sp |

Use the font's normal/static appearance for these wireframes. Keep longer DynaPuff titles on multiple lines when necessary rather than squeezing them into narrow spaces.

### Supporting font: Mali — accepted joyful pairing

The owner requested a joyful supporting font for **Due tomorrow**, **Due Nov 14**, **All tasks**, **Nov 10**, and all similarly styled text, then accepted the dashboard using **Mali** alongside DynaPuff. Continue that pairing in the Add commission screens.

Use Mali consistently for:

- Client usernames and platform names.
- Project descriptions, pose instructions, and revision notes.
- Form labels, entered text, helper messages, and validation errors.
- Dates, payment amounts, balances, and graph labels.
- Status chips, step counts, and bottom-navigation labels.

Proposed sizes are 14-16 sp for ordinary details, 16 sp for editable text, and 12-13 sp for short secondary captions. Use around 28-32 sp for the main earnings amount and 24-28 sp for workload counts. Keep financial figures in Mali, even when the label above them uses DynaPuff.

Use Medium weight for small dates, links, and supporting labels, and SemiBold for prominent financial figures and counts. The dashboard uses supporting labels around 12-13 sp so the handwritten details remain legible.

**DynaPuff and Mali are the accepted pairing.** Individual sizes, spacing, and form layouts remain open to refinement.

## 3. Proposed color scheme

The palette follows the warm, simple direction of the [Guhit reference](https://app.guhit.app/). The exact values below are proposed for OBRA.

| Role | Color | Use |
| --- | --- | --- |
| Page background | Warm cream `#FFF8EF` | Main screen background |
| Card/field surface | Soft white `#FFFDF9` | Commission cards, input areas, dialogs |
| Primary text | Deep charcoal `#302C37` | Titles, body text, values, button labels |
| Secondary text | Muted plum-gray `#6B6270` | Dates, captions, short supporting information |
| Main action | Butter yellow `#FFD15B` | Add commission, Next, Save; pair with charcoal text |
| Earnings summary | Pale sage `#ECEBDC` | Monthly earnings panel |
| Selected navigation | Warm beige `#F0E7DC` | Active bottom-navigation item |
| Control outline | Muted charcoal `#746A76` | Visible boundaries for inputs and outlined controls |
| Decorative divider | Light beige `#D8CEC4` | Noninteractive separators |
| Image placeholder | Warm gray `#EDE5DC` | Reference/artwork placeholders |
| Creative progress | Lavender `#EDE4F4` + text `#5D4975` | Lineart/progress chips |
| Sketch/queued accent | Soft gold `#F6EBCB` + text `#725A20` | Sketch or waiting-stage chips |
| Completed/paid accent | Pale green `#E7EEDB` + text `#3E6040` | Completed or fully paid labels |
| Urgent deadline | Pale peach `#FBE6D7` + text `#8E452A` | Due-soon and overdue notices |
| Destructive action | Pale rose `#FAE1DE` + text `#A23D38` | Cancel/delete confirmation actions |
| Chart series | Plum `#6A507D` | Initial earnings graph series in Insights |

Use dark text on yellow buttons. Keep destructive controls visually separate from the normal primary action. Pair status colors with words such as **Overdue**, **Partially paid**, or **Completed**; color alone must not carry the meaning.

The earnings panel's sage fill is decorative and does not mean that all commissions are paid. Graph axes, dates, and values use the normal text colors. Additional graph series should be chosen only when their meanings are defined.

Draw this light palette first. A dark theme has not been requested or specified.

## 4. Dashboard: static layout and planned actions

### Top-to-bottom layout

1. **Header:** OBRA name, short studio context, current date.
2. **Monthly earnings:** DynaPuff label, received-income amount, selected month.
3. **Quick counts:** current work, unfinished tasks, queued commissions.
4. **Current work:** two commission previews with image placeholders, title, username/platform, stage, and deadline.
5. **Coming up:** a short task preview with DynaPuff task titles, related client, and due date.
6. **Next in queue:** next accepted commission and planned start, when known.
7. **Add commission:** one prominent yellow action.
8. **Bottom navigation:** Home, Commissions, History, Insights.

The dashboard's data summaries do not require every entire card to be a button. Use a clear **View insights** label on the earnings panel and explicitly labeled **View all** actions on list sections.

### What tapping each part should do

| Starting control | Planned next view or result |
| --- | --- |
| View insights on Monthly earnings | Open Insights with the same month selected |
| Current work count | Open Commissions filtered to In progress |
| Unfinished task count / All tasks | Open the unfinished task list across commissions |
| In queue count | Open Commissions filtered to Queued |
| Current work → View all | Open Commissions filtered to In progress |
| Commission card | Open that commission's details |
| Task title such as Finish lineart | Open the task's details and related commission context |
| Task checkbox | Mark only that task complete; update task counts in the eventual app |
| Next in queue preview | Open that queued commission's details; do not start it automatically |
| Add commission | Open Step 1: Client details |
| Home | Return to the dashboard |
| Commissions | Open the commission list, initially showing In progress; status filters remain available |
| History | Open the payment timeline and access to previous work |
| Insights | Open earnings summaries and graphs |

Task title and checkbox have different purposes. Completing **Finish lineart** must not automatically mark the entire commission completed or record any payment. A task-detail view should offer an explicit **View commission** action to open the parent record.

## 5. Add commission: Next button sequence

Propose four numbered steps so a long form can be reviewed in manageable groups. Show **Step 1 of 4** and a clear step heading. The four screens can be drawn side by side on the static board.

See [the Add commission wireframe plan](docs/add-commission.md) for the current four-screen draft and its follow-up states. These screens keep the New commission header and Back/Close controls; main bottom navigation is omitted during entry.

### Step 1 — Client details

**Show:** contact platform (X or Discord), username, optional display name.

**Action:** **Next: Artwork**.

1. Check that a platform and a nonblank username are provided.
2. If something is missing, stay on Step 1 and show a Mali helper/error message under the relevant field.
3. If valid, keep the entered values in the draft and move to Step 2.

This step does not sign in to X/Discord, contact the client, or create a saved commission.

### Step 2 — Artwork brief

**Show:** package/type, title, genre/style tags, requested pose, brief/notes, reference-image area.

**Actions:** **Back** and **Next: Price & dates**.

1. Choose Full body, Half body, Chibi, Portrait, or another configured package.
2. Add the brief and attach any supplied references.
3. Tapping **Add reference** should eventually open the Android image picker; selecting an image returns here with a thumbnail. Cancelling the picker leaves the draft unchanged.
4. Next checks that a commission type and a short project title are present.
5. If valid, keep the draft and move to Step 3. If invalid, remain here with field-specific messages.

For this draft, reference images and tags are optional so a request can be logged before the client supplies everything. Required fields can be revised during review.

### Step 3 — Price and dates

**Show:** base package price, proposed add-ons if retained, agreed total, commission date, optional planned start, due date, and optional initial-payment fields.

**Actions:** **Back** and **Review commission**.

1. Selecting a package supplies a starting quote; any approved add-on controls update the draft quote.
2. Record the agreed total. The rule for manually overriding a calculated quote still needs agreement.
3. The commission date defaults to today and remains editable. Calendar controls open a date picker and return the selected date here.
4. Enter a due date. If a start date is provided, it should fall between the commission date and the due date.
5. An optional initial payment has its own amount, received date, and method. Its received date must not be replaced by the commission's due date.
6. **Review commission** checks a positive agreed total and a valid date sequence. When an initial payment is included, it also checks that its amount is positive, does not exceed the agreed total, and has a received date.
7. Missing or invalid information keeps the user on this step. Valid information opens Step 4.

Selecting a payment method records a label; it does not open a payment service or transfer money. Until final save, values remain draft information and do not affect earnings or queue counts.

### Step 4 — Review commission

**Show:** grouped summaries of the client, artwork request, references, agreed quote, dates, and any initial payment. Provide an **Edit** action for each group.

**Actions:** **Back** and **Save commission**.

1. **Back** returns to Step 3 with the draft intact.
2. A group's **Edit** action opens its corresponding step; following Next returns through the remaining steps to Review.
3. **Save commission** rechecks the complete draft.
4. The planned implementation saves the commission and its attachments/initial payment together, then opens that commission's details.
5. The initial lifecycle status is **Queued**. **Start commission** is a separate action.
6. A brief **Commission added** message confirms success. Returning to Home should show the updated queue and any received-income amount.

Only final Save creates the commission. If save fails, remain on Review with the entered information preserved and a retry option. Prevent repeated taps from creating duplicate records in the eventual implementation.

### Back, close, and interrupted entry

- **Back within Steps 2-4:** go to the previous step and preserve the draft.
- **Back from Step 1 or Close:** if the draft has edits, show **Discard this commission?** with **Keep editing** and **Discard**.
- **Keep editing:** return to the same step and values.
- **Discard:** return to the screen that opened the form; create no commission or payment.
- Saving drafts across app restarts has not been requested; it remains a separate future decision.

## 6. Commission details: action map

| Control | Planned sequence and result |
| --- | --- |
| Back | Return to the originating list, dashboard, or history view with its filter preserved |
| Edit | Open the same four groups with existing values; final action reads Save changes |
| Save changes | Save the edited commission and return to its details; retain existing payment records |
| Start commission | Change a queued record to In progress after an explicit start action; show its initial creative stage |
| Update stage | Open stage choices; Save stage applies the selected stage and returns to details |
| Record payment | Open amount/date/method/note entry; Save payment records it and refreshes the balance |
| Reference thumbnail | Open a full image view; Back returns to the same commission |
| Add task | Open task entry; Save task adds a to-do to this commission |
| Task row | Open task details; View commission returns to its parent record |
| Mark task done | Complete only that task and refresh the unfinished-task count |
| Mark commission complete | Review a confirmation, record the actual finish date, then mark the work Completed |
| Cancel commission | Review a cancellation confirmation; confirmed cancellation changes lifecycle status while retaining history |

Completing a commission must not mark it paid automatically. If money is still owed, show the remaining balance on the completed record. Whether Completed means artwork finished, final files sent, or client acceptance still needs a project decision.

Editing an agreed price must not silently erase payments. The handling of an edited price below the amount already received, refunds, and corrections remains open. A permanent Delete action is deferred until historical retention rules are agreed.

## 7. History and Insights: next destinations

### History

- **Payment row:** open a payment summary showing its amount, received date, method, note, and related commission.
- **View commission:** open the parent commission, even if completed or cancelled.
- **Previous commission row:** open its complete record, including due date, actual finish date, and amounts received.
- **Month/status filter:** update the displayed history without changing the stored records.

The division between payment transactions and previous-work history is still a layout choice. Keep both concepts clearly labeled in any proposed tabs or sections.

### Insights

- **Month selector:** choose a reporting month and update monthly totals/graphs.
- **Total earnings:** show received payments over the stated all-time reporting period.
- **Monthly earnings:** show payments received in the selected month.
- **Outstanding balances → View commissions:** open commissions with an unpaid balance, including completed work that is still owed money.
- **Graph period point/bar:** reveal the labeled period and amount; do not treat it as a new payment or project.

Changing a report filter never edits commissions or payments. Detailed graphs belong here rather than filling the dashboard.

## 8. What to draw on the static board

Draw the first dashboard normally, then show important alternative states beside it as separate labeled screens:

1. **Dashboard — normal:** monthly income, current work, pending tasks, queue preview.
2. **Dashboard — empty:** zero values and a clear Add commission action.
3. **Dashboard — urgent:** overdue/approaching deadlines labeled with words as well as color.
4. **Add commission — Steps 1-4:** use arrows outside the phones to show the Next/Back sequence.
5. **Form validation example:** show one missing-field state and one valid continuation.
6. **Commission details:** show task, stage, and payment actions as distinct controls.
7. **Record payment and completed-but-unpaid example:** make the financial distinction visible.

Use DynaPuff for **Monthly earnings**, **Finish lineart**, and the other specified titles in every state. Place notes such as **Next: Artwork → Step 2** outside the phone frames when a control's destination needs explanation.

The board should communicate the planned behavior through labels, arrows, and separate states. Implementing the clicks or transitions is outside the current wireframe phase.

## Related plans

- [Dashboard](docs/dashboard.md)
- [Add commission wireframe](docs/add-commission.md)
- [Screen inventory](docs/screens.md)
- [Design direction](docs/design-direction.md)
- [Requirements](docs/requirements.md)
- [Open decisions](docs/decisions.md)
