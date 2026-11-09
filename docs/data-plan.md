# General database plan

This document names the information the app needs. It deliberately avoids a detailed schema, field IDs, SQL, and implementation choices during planning.

## Information groups

### Clients

- Display name.
- X or Discord username.
- Contact platform.
- Client notes.
- Connections to current and previous commissions.

Whether a client can have several platform usernames is an open decision.

### Commission packages

- Package name: for example full body, half body, chibi, portrait.
- Description of what is included.
- Base price.
- Estimated turnaround.
- Optional add-on descriptions and charges, if approved.

### Commissions

- Client and contact details used for the request.
- Title, package/type, brief, pose instructions, and notes.
- Genre/style/theme tags.
- Agreed total price.
- Lifecycle status and creative stage.
- Commission date.
- Planned start date, when applicable.
- Due date.
- Actual finish date, once completed.
- Cancellation information, if that feature is approved.
- Connections to references, payments, tasks, and final artwork.

### Reference images and artwork attachments

- Associated commission.
- Image location within the device/app's storage approach.
- Optional caption or purpose, such as pose, outfit, or color palette.
- Whether an attachment is a request reference or final artwork.

The future implementation must account for permissions and whether an attached image remains accessible after the original is moved or removed. That technical solution is not chosen yet.

### Payments

- Associated commission.
- Amount received.
- Date received.
- Payment method.
- Purpose, such as deposit or final balance.
- Optional note.

Refunds and correction/audit behavior are suggestions to resolve before implementation, not settled requirements.

### Tasks

- Associated commission.
- Task description.
- Completion state.

The current checklist has no per-task deadlines or additional task-detail/custom-entry screens. The overall commission due date remains part of the commission. Priority, task dates, and custom task management are deferred to keep the initial scope simple.

### Genres and tags

- Reusable labels for style, subject, genre, and theme.
- Connections between labels and commission requests.

The choice between one genre field and multiple reusable tags is still open.

## Views of commission records

**Current Queue** can show status, payments/balance, request details, references, commission date, planned start, and due date.

**Previous Commissions** can show the original request, agreed total, amounts received, payment dates, original deadline, actual finish date, and final artwork.

**Upcoming Commissions** can show accepted work, queue order, planned start, expected deadline, references, price, and deposit.

These are views of the same commission records. A commission moves between views when its status changes; its history should not be copied into a separate record.

## Calculated information

The dashboard, History, and Insights use the stored records rather than separate duplicate tables for every screen.

| Displayed information | General source |
| --- | --- |
| Monthly earnings | Payments received in the selected month |
| Total earnings | Recorded payments over the included period |
| Outstanding balance | Agreed commission price minus valid payments received |
| Current work count | Commissions marked in progress |
| Queue count | Accepted commissions waiting to start |
| Unfinished task count | Tasks not yet completed |
| Previous work | Completed/cancelled records according to the selected view |
| Earnings graphs | Payments grouped by reporting period |
| Reconnect suggestions | Current work type/genre and prior commission/client context supplied through the recommendation provider |

Reconnect adds no requirement for image analysis or duplicated commission records. Coordinate the genre/type fields, returned client/reason data, and previous-record links with the algorithm/data teammates; keep genre distinct from style tags when mapping their output. The frontend does not define the matching algorithm or database schema here.

## Rules to preserve

- Work status and payment status are independent.
- Payments keep their own dates; a project's due date is not its payment date.
- Completing a commission must not automatically create a payment.
- Changing package prices must not rewrite old agreed prices.
- Keep historical deadlines and actual finish dates distinguishable.
- Cancelling or removing a project must not silently erase the financial history; choose a clear retention rule before implementation.
- Amounts must be handled consistently with their currency. PHP is an example, and multiple-currency reporting is outside the initial plan.
- Use local SQLite as the intended storage direction from the original proposal, subject to final implementation planning.
