# Backend verification walkthrough

Use fictional records on the running backend build. A fresh workspace starts empty. Existing emulator records remain saved. The instrumentation suite uses a separate database for its generated fixtures.

## Intake and persistence

- [ ] Add a commission with a unique fictional username, title, type, optional primary genre, tags, brief and references.
- [ ] Move Back/Next through all four steps and confirm input remains intact.
- [ ] Rotate during a step and confirm values and the current step return.
- [ ] Set an agreed PHP 2,000 price and optionally record PHP 500 already received.
- [ ] Save and confirm the same artwork opens in Details under Queued.
- [ ] Close and reopen OBRA. Verify request details, tags, references, dates, tasks and receipts remain.
- [ ] With initial payment disabled, confirm zero received and the full balance.
- [ ] Try invalid money, impossible dates and a due date before the commission date. Confirm correction messages and retained input.

## Edit and historical categories

- [ ] Edit contact/display name, title, genre, tags, brief and dates. Save to the same artwork ID.
- [ ] Confirm existing task states, actual progress and receipts survive the edit.
- [ ] Reject an agreed price below money already received or a commission date after an existing receipt.
- [ ] Verify the form keeps the historical category when that lookup is retired.
- [ ] Verify new requests omit retired types and retired genre suggestions. Direct new writes using retired categories are rejected.
- [ ] Open two editing sessions, save a request change in one, then verify the stale request draft is rejected in the other.

## References

- [ ] Pick a real supported image and preview it. Cancel another picker operation and confirm the draft remains.
- [ ] Save, reopen the app, and preview the saved reference from Details.
- [ ] Open Edit, change only the title, save and confirm references still display.
- [ ] Remove a reference in Edit. Back/Discard should preserve the saved reference. Confirmed Save should remove its attachment metadata.
- [ ] Test a large image. Confirm the screen remains responsive and zoom/fit/previous/next work.
- [ ] Make a test reference inaccessible. Confirm Image unavailable, Retry image and safe Back behavior. The artwork and receipts must remain readable.

## Work and tasks

- [ ] Start queued work. Confirm In progress, Sketch and actual start date are saved together.
- [ ] Toggle a task from Home/All tasks and Details. Confirm the same saved state and count refresh.
- [ ] Change Sketch to Lineart or Colors. Reopen Details and confirm the change.
- [ ] Select Finished and confirm the finish date in Review completion.
- [ ] Reject a finish date before actual start or in the future.
- [ ] Complete artwork with an unpaid balance. Confirm the balance and unfinished tasks remain.

## Payments and reports

- [ ] Save a positive PHP 500 receipt against the PHP 2,000 artwork. Confirm PHP 1,500 remains.
- [ ] Reject zero, fractional-cent, negative, future-dated and excessive payments.
- [ ] Change only the received date or method and press Back. Confirm the discard/keep-editing choice.
- [ ] Record PHP 1,500 later. Verify two receipts, PHP 2,000 received and zero remaining.
- [ ] Confirm Record payment is disabled when fully paid.
- [ ] Verify fully paid current work remains In progress until completion is confirmed.
- [ ] Use receipts in different months and compare monthly History and Insights totals.
- [ ] Confirm all-time totals and current balances retain their scopes when selecting another reporting month.
- [ ] Open View payments from Insights and confirm the selected month carries into History.
- [ ] Open a payment's commission and return. Preserve the month/filter and scroll position.
- [ ] Rotate a scrolled report. Confirm its scroll position is restored after saved records load.

## Reconnect

- [ ] Create completed history with matching type/genre for a different client.
- [ ] Select a current commission in Reconnect. Confirm one latest matching artwork per client, ordered by recent finish date.
- [ ] Test no current work, missing genre and no matching completed history.
- [ ] Open a suggestion and return. Preserve selection and scroll position.
- [ ] Finish the previously selected current artwork elsewhere and return. Confirm the previous selection is unavailable and choose another explicitly.

## Failures and interruptions

The automated suite exercises read failures and queued writes in its isolated database.

- [ ] Verify a failed initial read provides Retry.
- [ ] Verify a later read failure retains existing information and offers Retry refresh.
- [ ] Verify a successful write followed by read failure is labelled Saved successfully with refresh recovery.
- [ ] Recreate Add/payment while a save is queued. Verify recovery opens the saved record or closes the completed payment form with exactly one write.
- [ ] Verify the populated version-one upgrade preserves clients, lookups, commission IDs, receipts, attachments/captions, tags and task states.
- [ ] Run foreign-key and integrity diagnostics.

## Device review

- [ ] Check keyboard/insets, narrow-screen labels, larger text, Back behavior and image gestures on supported phones.
- [ ] Record device, Android version, exact input/action, expected/actual result and a screenshot for any issue.

The user selected the existing backend scope. Cancellation/archive, destructive deletion, refunds, receipt corrections and structured quote snapshots remain deferred product decisions.

## Connected-test installation retention

`android.injected.androidTest.leaveApksInstalledAfterRun=true` keeps the debug app and normal workspace after connected tests. This setting was checked with the three database instrumentation tests, followed by checks that the app package and ordinary `databases/obra.db` still existed. Activity test operations continue to use their separate database.
