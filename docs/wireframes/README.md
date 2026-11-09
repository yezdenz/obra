# Saved static wireframe boards

These are the latest static boards from the planning conversation, saved with their editable source fragments, standalone HTML exports, and JPEG previews. Obsolete interactive explorations and earlier font alternatives are not included.

The [numbered layout guide](../../layout.md) is the master reference for screen order, button destinations, scrolling, states, and work still to draw.

| Board | Numbered layouts / states | Preview | Standalone export | Editable source |
| --- | --- | --- | --- | --- |
| Dashboard | [WF-01](../../layout.md#wf01) | [Image](dashboard.jpg) | [HTML](dashboard.html) | [Source](source/dashboard.html) |
| Commissions list | [WF-02A–D](../../layout.md#wf02): Current, Queued, Completed, empty | [Image](commissions-list.jpg) | [HTML](commissions-list.html) | [Source](source/commissions-list.html) |
| Add commission | [WF-03–WF-06](../../layout.md#wf03): four form steps | [Image](add-commission.jpg) | [HTML](add-commission.html) | [Source](source/add-commission.html) |
| Commission Details | [WF-07](../../layout.md#wf07): first/lower views, Queued, Completed | [Image](commission-details.jpg) | [HTML](commission-details.html) | [Source](source/commission-details.html) |
| Commission payment history | [WF-08A](../../layout.md#wf08) on the right; WF-07B context on the left | [Image](payment-history.jpg) | [HTML](payment-history.html) | [Source](source/payment-history.html) |
| Record payment and saved result | [WF-09](../../layout.md#wf09) on the left; WF-08B on the right | [Image](record-payment.jpg) | [HTML](record-payment.html) | [Source](source/record-payment.html) |
| Update progress and completion | [WF-10A/B](../../layout.md#wf10), [WF-11](../../layout.md#wf11), and existing WF-07C result | [Image](update-progress.jpg) | [HTML](update-progress.html) | [Source](source/update-progress.html) |
| Main transaction History | [WF-12A/B](../../layout.md#wf12): received payments and empty month | [Image](main-history.jpg) | [HTML](main-history.html) | [Source](source/main-history.html) |
| Insights | [WF-13A/B](../../layout.md#wf13): first/lower scroll views of one report page | [Image](insights.jpg) | [HTML](insights.html) | [Source](source/insights.html) |

## How to use the files

- JPEG previews are embedded in `layout.md` and can be opened at full size.
- GitHub displays HTML as source. Open a downloaded/local standalone HTML export in a browser to inspect it visually.
- Standalone exports use external font/icon resources; JPEG previews do not need those resources to display.
- Source fragments preserve the product markup and styling for later revisions.
- Controls are represented visually. No commission, payment, navigation, or transition logic is implemented.
- The screenshots contain fictional planning data and different points in a sample workflow; they are not a live synchronized data set.

## When revising a wireframe

Update the source fragment, regenerate its standalone export and preview, and update the numbered entry in `layout.md` if its controls or destinations change. Keep current and saved-state images labeled consistently. Do not label an undrawn destination as an existing screen.

Two panels may describe one page: WF-07A/B and WF-13A/B are scroll positions, WF-02A–D are list states, WF-08A/B are payment states, WF-10A/B are alternative stage selections, and WF-12A/B are populated/empty months. The master guide explains these relationships explicitly.
