# Dashboard plan

## Goal

Home is the artist's studio overview. It should communicate income, workload, unfinished actions, and the waiting queue at a glance. It is broader than a commission list.

The current deliverable is a **static wireframe** of this screen at the Android Studio Medium Phone reference size. Illustrative amounts and fictional usernames are placeholders, not real data or final product copy.

Use the owner's selected **DynaPuff** for headings, the **Monthly earnings** label, and task titles such as **Finish lineart**. Andika is proposed for details and numeric amounts. See [layout.md](../layout.md) for the exact typography plan, proposed color palette, and the destination/result of every dashboard action.

## Proposed reading order

| Order | Area | Planned contents | Purpose |
| --- | --- | --- | --- |
| 1 | Header | OBRA name, short context, current date | Establish the workspace and date |
| 2 | Monthly earnings | Amount received and clearly named month | Give a quick income summary |
| 3 | Workload summary | Current work, unfinished task, and queue counts | Show the size of the workload |
| 4 | Current work | A small preview of active commissions | Identify the next projects to work on |
| 5 | Coming up | Unfinished tasks with deadlines | Identify specific actions needing attention |
| 6 | Queue preview | Next accepted commission and planned start, if set | Show what follows current work |
| 7 | Add commission | One prominent action | Capture a new request |
| 8 | Navigation | Home, Commissions, History, Insights | Reach the other main sections |

This order is a draft for discussion. Detailed graphs belong in Insights so the dashboard can remain readable on a phone.

## Current work preview

A compact commission card can show:

- An image/reference placeholder.
- Project title or commission type.
- Client username and contact platform.
- Current creative stage.
- Due date or overdue warning.

The initial static draft can show two cards and a planned View all action. Longer descriptions, full payment history, all reference images, and revision notes belong on Commission Details.

## What the counts mean

- **Current work:** commissions with work underway, excluding queued, completed, and cancelled records.
- **Unfinished tasks:** open checklist items attached to commissions. This is a task count, not the same as the number of unfinished commissions.
- **In queue:** accepted commissions waiting to start.

A queue preview can name the next commission, but manual ordering, date ordering, and queue capacity still need agreement.

## Earnings meaning

The monthly amount is the sum of payments received during that month. Unpaid quoted amounts and outstanding balances are not earned cash. Outstanding balances can be shown in Insights or on individual commissions.

The exact rule for refunds and how the app handles currencies remain open. PHP is used in examples; multiple currencies are not an initial requirement.

## States to draw

- New artist: no commissions, tasks, or payments yet.
- Normal activity: multiple current commissions and queued work.
- Urgent work: a deadline is approaching or has passed.
- No current work but accepted work in the queue.
- No payments received in the selected month.
- More records than fit on the screen.

For a longer dashboard, the planned app should scroll its main content while keeping navigation usable. The wireframe should distinguish the first viewport from content below it. Do not shrink text to force every record into one screen.

## Static hierarchy

```text
OBRA                                      Date

Monthly earnings                         Month
Payments received

Current work       Unfinished tasks       In queue

Current work                            View all
[ Commission preview                           ]
[ Commission preview                           ]

Coming up                               All tasks
[ Task + client + due date                     ]
[ Task + client + due date                     ]

Next in queue                       Planned start

[               Add commission                 ]

Home       Commissions       History       Insights
```

## Review checklist

- [ ] The dashboard reads as a studio overview.
- [ ] Monthly income is distinguished from unpaid project value.
- [ ] Current work, tasks, and queue have clear meanings.
- [ ] Urgent dates are readable without relying on color alone.
- [ ] The main action is obvious.
- [ ] The screen remains legible at the Medium Phone reference size.
- [ ] Layout and information hierarchy are agreed before interactions are built.
