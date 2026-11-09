# Record payment wireframe

## Purpose and entry

**Record payment** opens a focused form from Commission Details or that commission's Payment history. Keep the commission and client context visible so the artist knows which record is being updated.

The board shows two static phone states: the populated form and the updated Payment history after Save. It uses the accepted DynaPuff/Mali pairing, warm cream background, sage summary panel, and yellow primary action. No executable form or transitions are implemented.

## Form layout

1. Header: Back, Record payment, Close.
2. Project title and username/platform.
3. Current balance, with agreed price and amounts already received for context.
4. Amount received; required.
5. Date received; required.
6. Payment method; required in this draft.
7. Optional note.
8. Remaining balance after saving: a clearly labeled draft preview.
9. Save payment.

The amount field uses PHP in the example. The received date defaults to today and remains editable through a calendar control. Method options can include GCash, PayPal, bank transfer, cash, and Other; these are manually recorded labels, not payment-service integrations.

Keep the form short. A separate payment-purpose field is not included in this draft. Suggested automatic entry labels are Initial payment for the first receipt, Balance payment for a later receipt that clears the outstanding amount, and Additional payment for another partial receipt. These labels are a proposal for review.

## Example before saving

The fictional Fantasy portrait for `mika.draws` on Discord has:

- PHP 1,800 agreed.
- PHP 900 already received as a deposit on November 2, 2026.
- PHP 900 currently outstanding.

The form records a further PHP 900 received November 9, 2026, using GCash as the method and Remaining balance for the portrait as an optional note. The preview says the remaining balance will be PHP 0 **after saving**; it does not imply that the draft has already updated the record.

## Planned Save behavior

1. Check the amount is a positive monetary value and no greater than the current outstanding balance.
2. Check a valid received date and method are present. The received date should not be in the future for a payment already received.
3. Invalid information stays on the form with a field-specific message and entered values preserved.
4. Save one payment record against the selected commission, preserving its existing deposit/history.
5. Open the updated commission-specific Payment history with brief Payment recorded feedback.
6. Refresh received totals, remaining balance, and payment status from the saved records.
7. Update earnings for the payment's received month; do not use the commission deadline as its payment date.

Prevent repeated Save taps from creating duplicate payment records in the eventual implementation. A save failure leaves the form and entered information intact for retry.

The positive/no-overpayment rule is the proposed first-version boundary. Refunds, overpayments, currency conversion, and payment corrections are separate unresolved features rather than extra controls on this form.

## Example after saving

The history now shows two entries: the new PHP 900 Balance payment on November 9 and the original PHP 900 Initial payment on November 2. Both use GCash as the recorded method, and their notes appear directly on the entries.

The summary shows PHP 1,800 received, PHP 0 remaining, and Fully paid. The commission's work status remains **In progress**. Paying the balance does not complete a checklist item, advance a creative stage, or finish the artwork.

With no balance left, the primary action is **Back to commission**, rather than offering another ordinary payment entry. If only part of the balance is paid, keep Partially paid and retain Record payment for the remaining amount.

## Back and Close

- Back or Close returns to the view that opened the form.
- If there are unsaved edits, use the same Keep editing / Discard pattern as commission intake.
- Discard leaves the original payment history, balance, and earnings unchanged.
- After a successful save, Back to commission opens the same work record with the refreshed payment summary.

The payment form is a new draft for review. See [Payment history](payment-history.md), [Commission Details](commission-details.md), and [layout.md](../layout.md).
