# Add commission wireframe

## Entry and presentation

The dashboard's **Add commission** action opens **New commission → Step 1: Client details**. This is a static screen-planning draft. Draw the four steps together on the wireframe board; do not implement their buttons or transitions during this phase.

Use the accepted **DynaPuff + Mali** pairing: DynaPuff for headings and primary actions, Mali for field labels, values, usernames, dates, and helper text. Continue the dashboard's warm cream, charcoal, yellow, sage, and lavender direction.

Each step uses the Medium Phone portrait proportions, a consistent New commission header, Back and Close controls, a visible step count, and one yellow continuation action at the bottom. Main bottom navigation is omitted during entry to keep the form focused.

## Step 1: Client details

Show:

- Contact platform: X or Discord; required.
- Exact client username; required.
- Display name; optional.

The sample board selects Discord, enters the fictional username `luna.art`, and uses Luna as a display name.

**Next: Artwork** checks the platform and a nonblank username, preserves the draft, and opens Step 2. Missing information stays on this step with a field-specific helper/error message. Manual username entry does not connect an external account or contact the client.

## Step 2: Artwork brief

Show:

- Project title; required in this draft.
- Commission type: Full body, Half body, Chibi, Portrait; required.
- Genre/style tags; optional.
- Pose, instructions, and other request details.
- Reference thumbnails with filenames and an Add image area; optional.

The sample uses **Moonlit portrait**, **Half body**, Fantasy and Soft light tags, an over-the-shoulder pose, and two image placeholders named `pose.png` and `outfit.png`.

**Add image** is planned to open the Android image picker and return to the same draft. Cancelling the picker changes nothing. A selected thumbnail can open a preview with a Remove option; removing it only affects the draft.

**Next: Price & dates** checks the title/type and continues to Step 3 with the brief and references retained. Required/optional field rules remain subject to review.

## Step 3: Price and dates

Show:

- Starting package quote and any chosen add-on.
- Suggested quote, separate from the agreed total.
- Agreed total; required and positive.
- Commission date, initially today and editable.
- Due date; required in this draft.
- Planned start; optional.
- Record initial payment; optional and initially off.

The example quote is PHP 1,500 for Half body plus PHP 500 for a detailed background, producing PHP 2,000. These are fictional planning amounts, not final package rates. Rules for editable quotes and add-on prices still need agreement.

The example dates are commission date November 9, planned start November 12, and due date November 20, 2026. A calendar control eventually opens a date picker. Validate chronological order, including any optional start date.

### Initial-payment branch

With **Record initial payment** off, no payment is included. The review shows zero received and the full balance outstanding.

Turning it on should reveal:

- Amount already received.
- Date received.
- Payment method.
- Optional payment note.

Only use it for an actual payment already received. It does not process money or require a fixed deposit percentage. Validate the amount/date before continuing. If expanded fields exceed the viewport, the main content should scroll while the continuation action remains reachable in the eventual app.

The expanded state is a follow-up static wireframe to draw; the current four-screen board shows the off state.

**Review commission** checks the quote, dates, and any supplied initial-payment fields, then opens Step 4. It does not create a commission or affect dashboard income yet.

## Step 4: Review

Show four summary groups, each with an Edit action:

1. Client: display name, username, platform.
2. Artwork: title, type, tags, brief, reference count.
3. Schedule: commission date, planned start if supplied, deadline.
4. Price/payment: agreed total, amount already paid, remaining balance.

The example has PHP 2,000 agreed, PHP 0 received, and PHP 2,000 remaining. It is Unpaid and will join the queue.

An **Edit** action opens the corresponding form step with its existing values. Following the continuation actions returns through the remaining steps to Review.

**Save commission** is planned to:

1. Recheck the complete draft.
2. Save one commission, its references, and any valid initial payment together.
3. Set the lifecycle status to Queued.
4. Open that commission's details with a short Commission added confirmation.

Starting work remains a separate action. Without an initial payment, saving must not increase earnings. If saving fails, retain the review and entered values so the artist can retry without re-entering the form.

## Back and close

- Back from Steps 2-4 returns to the previous step and keeps the values.
- Back from Step 1 or Close returns to the originating screen if there are no edits.
- If edits exist, show Discard this commission? with Keep editing and Discard.
- Discard creates no commission or payment.
- Main navigation is not shown inside the form.

## Follow-up states to draw

- Missing username on Step 1.
- Artwork brief before reference images are supplied.
- Image picker return/preview.
- Invalid due/start-date order.
- Initial payment on, with amount/date/method fields.
- Review containing an initial payment and a partial remaining balance.
- Discard confirmation.
- Save failure preserving the draft.

The owner accepted the four-step static wireframe direction after reviewing the board. Detailed field requirements, validation, quote overrides, and price rules still need refinement; visual acceptance does not imply that every implementation rule is finalized.

The next board, [Commission Details](commission-details.md), shows the queued record reached after Save, along with in-progress and completed examples.

See [layout.md](../layout.md) for the full action specification and [screen plan](screens.md) for the wider application context.
