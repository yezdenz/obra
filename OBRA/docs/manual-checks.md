# OBRA Frontend Verification Walkthrough

Use this guide to check the app's screens, buttons, navigation, forms, and feedback. Follow the sections in order. Mark each check Pass or Fail and capture a screenshot when something does not behave as expected.

The app uses sample commissions and payments. Saving, starting work, completing work, and checking tasks stop at a **Ready for backend integration** message. The expected result is that your input remains available and the sample records stay unchanged.

## 1. Launch and main navigation

- [ ] Open OBRA. Confirm Home appears without a crash.
- [ ] Check that text is readable, cards and buttons fit, and system bars do not cover content.
- [ ] Open **Work > History > Insights > Reconnect > Home** using the bottom navigation.
- [ ] Confirm each destination opens and its tab is highlighted. **Work** is the navigation label for Commissions.
- [ ] Scroll each page and confirm lower content and actions remain reachable.

## 2. Home shortcuts and artwork links

- [ ] Tap **Current work**: Work opens with In progress selected. Return to Home.
- [ ] Tap **In queue**: Work opens with Queued selected. Return to Home.
- [ ] Tap **Unfinished tasks**: All tasks opens. Use **Back to Home**.
- [ ] Tap a current artwork card: Commission details opens for that artwork. Use Back to return Home.
- [ ] Open the artwork under **Next in queue** and return with Back.
- [ ] Open **All tasks**, then return Home.
- [ ] Tap **View insights**: Insights opens. Return Home using its tab.

## 3. Work filters and search

- [ ] Open **Work** and select **In progress**, **Queued**, and **Completed** in turn.
- [ ] Open a record from each filter, then use Back. Confirm the original filter and list position are retained.
- [ ] With In progress selected, search `mika`: Fantasy portrait should appear.
- [ ] Search for part of a visible artwork title: the corresponding record should appear.
- [ ] Search `zzzz-no-match`: a no-results message should appear.
- [ ] Clear the search: records for the selected filter should return.

## 4. Add commission - client details

- [ ] Open **Home > Add commission**.
- [ ] Leave Client username blank and tap **Next: Artwork**. A validation popup should appear; dismissing it should retain the form.
- [ ] Select **Discord**, enter `test.client`, and enter an optional display name.
- [ ] Tap **Next: Artwork**: Step 2 opens.
- [ ] Use Back: Step 1 returns with your values retained. Continue to Step 2 again.

## 5. Add commission - artwork brief

- [ ] Leave Project title blank and tap Next. Confirm validation prevents continuation.
- [ ] Enter `Frontend test portrait`, select **Portrait**, and enter **Fantasy** as the primary genre.
- [ ] Enter optional style tags and pose/details.
- [ ] Tap **Add image**, then cancel the picker: the image count should not change.
- [ ] Add two images. Confirm both appear as reference entries.
- [ ] Open a reference: the image viewer opens. Test Zoom in, pinch zoom, Fit image, and panning while zoomed.
- [ ] Test Previous and Next. Paging must be disabled at the first/last image as appropriate.
- [ ] Use Back: the artwork form and its input should remain available.
- [ ] Remove one reference: the count should decrease.
- [ ] Continue to **Price & dates**.

## 6. Add commission - price and dates

Correct each invalid value before proceeding to the next check.

- [ ] Toggle **Detailed background**. The suggested quote changes; the agreed total remains independently editable.
- [ ] Enter `0` as the agreed total and try Review: validation should reject it.
- [ ] Enter `2000.001`: validation should reject more than two decimal places.
- [ ] Enter `2000.00`.
- [ ] Set Due date before Commission date: validation should reject the sequence.
- [ ] Choose a valid Due date. Set Planned start outside the commission/due range: validation should reject it.
- [ ] Choose a valid Planned start, then test **Clear planned start**. An empty planned start is allowed.
- [ ] Enable **Record initial payment**.
- [ ] Enter `2500.00`: validation should reject a payment above the agreed total.
- [ ] Enter `500.00`. Try a received date in the future, then one before Commission date: both should be rejected.
- [ ] Set Received date to today, choose a method, and enter an optional note.
- [ ] Tap **Review commission**: Step 4 opens.

## 7. Review and Save

- [ ] Check the client, artwork, schedule, price, and initial-payment summaries.
- [ ] Tap each group's **Edit** button. Confirm it opens the relevant step with your values retained.
- [ ] Continue through the steps to Review again.
- [ ] Tap **Save commission**. Expect the integration-pending popup.
- [ ] Tap **Keep editing**: your draft should remain intact.
- [ ] Use Back through the steps. No commission should have been added to the sample lists.

## 8. Close and discard confirmation

- [ ] On a changed Add form, tap **Close form**.
- [ ] Confirm the popup has a rounded cream panel, a playful heading, readable supporting text, yellow **Keep editing**, and pale-rose **Discard changes**.
- [ ] Tap Keep editing: your input remains.
- [ ] Open the popup again and tap outside it: it should stay open.
- [ ] Use the system Back gesture: the popup closes and your form remains.
- [ ] Close again and tap Discard changes: return to the originating page.
- [ ] Repeat from **Work > New commission** to verify that entry point and its return route.

## 9. Commission details and editing

- [ ] Open **Work > In progress > Half-body illustration**.
- [ ] Check the client, status, stage, brief, reference link, tasks, dates, agreed total, received amount, and balance.
- [ ] Tap **Edit commission**. Confirm the four steps are prefilled.
- [ ] Without changing anything, reach Review: Save changes should be disabled.
- [ ] Return to an earlier step and change the title and deadline. Review should show those changes.
- [ ] Set agreed total below the amount already received: validation should reject it.
- [ ] Set Commission date after the actual start or an existing receipt: validation should reject it.
- [ ] Restore valid values and tap Save changes: expect integration-pending feedback with input retained.
- [ ] Close the changed form. Test Keep editing, then Discard changes. The original sample record should remain unchanged.

## 10. Start work and update progress

- [ ] Open **Work > Queued > Moonlit portrait > Start commission**.
- [ ] Dismiss the confirmation with Keep editing: Details remains unchanged.
- [ ] Repeat and Confirm: expect integration-pending feedback. The sample commission remains queued.
- [ ] Open **Work > In progress > Half-body illustration > Update progress**.
- [ ] Continue with its existing stage: expect a message asking for a different stage.
- [ ] Select another non-final stage and Continue: expect integration-pending feedback.
- [ ] Choose **Finished > Continue**: Review completion opens.
- [ ] Try a finish date before actual start and one in the future: both should be rejected.
- [ ] Choose a valid finish date and tap **Mark completed**: expect integration-pending feedback.
- [ ] Tap **Keep working**: return to stage selection. Payment and task states should remain unchanged.

## 11. Payments and commission payment history

- [ ] Open **Half-body illustration > Payment history**. Check the receipt and remaining balance.
- [ ] Tap **Record payment**: the form should identify the same artwork.
- [ ] Try zero, three decimal places, and an amount above the balance: each should be rejected.
- [ ] Try a future received date and a date before commission creation: both should be rejected.
- [ ] Enter a valid amount, date, method, and note. Tap Save payment: expect integration-pending feedback and retained input.
- [ ] Use Back and test Keep editing, then Discard changes.
- [ ] From payment history, tap **Back to commission**: Details opens.
- [ ] Open **Work > In progress > Fantasy portrait**: Record payment should be disabled because it is fully paid.
- [ ] Open **Work > Completed > Cozy café illustration**: it should still have a balance and allow payment entry.

## 12. Main History

- [ ] Open History and select a month with receipts. Check the displayed total and receipt count.
- [ ] Select an earlier month without receipts: an empty-state message should appear.
- [ ] Return to a populated month and tap a receipt's **View commission**.
- [ ] Confirm the correct parent artwork opens. Back should restore History and its selected month.

## 13. Insights

- [ ] Open Insights and choose different reporting months.
- [ ] Check that monthly earnings, the six-month chart, and the type breakdown reflect the selected month.
- [ ] Confirm all-time received earnings and current balances retain their own scopes.
- [ ] Tap **View payments**: History should open with the same reporting month.
- [ ] Return to Insights. Open an artwork under Current balances, then use Back.
- [ ] Confirm completed-but-unpaid artwork can appear among those balances.

## 14. Reconnect

- [ ] Open Reconnect and select **Fantasy portrait**.
- [ ] Confirm the sample suggestion identifies **@tala.arts / Autumn portrait**, with its type-and-genre explanation.
- [ ] Tap **View previous commission**: Autumn portrait Details should open.
- [ ] Use Back: the original Reconnect selection should remain.
- [ ] Select **Half-body illustration**: no-match feedback should appear.
- [ ] Visit Insights, change its month, and return to Reconnect. Its selection should remain independent.

Suggestions are fixed sample results; this walkthrough does not verify a recommendation algorithm.

## 15. Checklist and All tasks

- [ ] Open **Home > All tasks** or the unfinished-task count.
- [ ] Tap a task title: its parent commission should open. Back should return to the task list.
- [ ] Tap checkboxes in Home, All tasks, and Details.
- [ ] Expect integration-pending feedback and the checkbox returning to its original state. Counts and work/payment status should remain unchanged.
- [ ] Use Back to Home from All tasks.

## 16. Reference preview and unavailable images

- [ ] From Details, open **Reference preview**. Sample records show a placeholder because artwork files are not bundled.
- [ ] Use Back: return to the same Details page.
- [ ] Use the Add/Edit image route from section 5 for actual images.
- [ ] If an image becomes inaccessible, confirm the viewer explains the failure and offers Retry and safe Back navigation.

## 17. Screen size, keyboard and interruptions

- [ ] Open each form with the keyboard visible. Scroll to ensure fields and actions remain reachable.
- [ ] Increase the phone's font size. Check headings, labels, buttons, and popup content for clipping.
- [ ] Rotate during each form step and confirm entered values and the current step survive.
- [ ] Test Back from forms, Details, payment history, progress, and image viewing.
- [ ] Relaunch the app: sample records should return unchanged. Persistent draft storage is not implemented.

## Record any issues

For each failure, record:

- Screen and navigation route.
- Input and action performed.
- Expected result and actual result.
- Screenshot or screen recording.
- Phone model, Android version, orientation, and font-size setting.

Loading/error states for database operations, successful writes, deletion/cancellation/refunds, and persistent records require backend integration or further agreed implementation. Native date pickers, dropdowns, month lists, and the Android image picker can use system styling.
