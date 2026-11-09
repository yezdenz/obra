# Reconnect — recommendations frontend handoff

## Owner requirements

Reconnect is the fifth main section, separate from financial Insights. It recommends previous clients who may be interested in artwork similar to a current commission, using **commission type and genre only**. Examples include Portrait/Fantasy or Chibi/Fantasy. No image processing is requested. The designated teammates own the algorithm/backtracking work; this handoff covers the interface.

The existing clickable results are fixed samples. They are not a matching algorithm, ranking system, or guarantee of client interest. Recommendations never send messages automatically.

## WF-19 page and states

Use DynaPuff for the screen/card titles and actions; Mali for usernames, platforms, selected values, chips, dates, and descriptions. Retain the shared warm palette and five-tab navigation with Reconnect active.

Read top to bottom: header → explanation → Based on current work → type/genre context → result count → suggested-client cards. The selector contains In-progress commissions. Cards show username/platform, a previous completed artwork, finish date, matching type/genre labels, and View previous commission. The list scrolls while navigation stays reachable.

| State | UI response |
| --- | --- |
| Current work selected, results supplied | Show its type/genre context and returned cards/count |
| No matching clients | Keep the selector/context; invite another selection or more completed-work history |
| No current work | Explain why there are no suggestions; View commissions opens Current |
| Loading results | Retain selected context and show loading feedback; do not display stale cards as new results |
| Provider failure | Explain that suggestions could not load, retain context, and offer Retry |
| Previous artwork unavailable | Show readable feedback and remain in Reconnect rather than opening an unrelated record |

The saved static board shows matching/no-match states; loading/error/unavailable-artwork visuals remain to refine. The clickable demonstrates selection, sample/empty results, previous-record opening, and Back.

## Interaction contract

| Action | Route / state to preserve |
| --- | --- |
| Tap Reconnect in any main tab bar | WF-19, retaining session selection |
| Change current work | Update visible type/genre context and request results for that selection |
| View previous commission | WF-07 for the previous artwork, not the selected current artwork |
| Back from that artwork | WF-19 with selected work, results, and scroll position restored |
| Other main tab | The corresponding page; changing Insights' month does not filter Reconnect |

Only actual buttons/links navigate. A match chip is explanatory, not an extra filter or detail screen. Contact is manual outside the app using the displayed platform/username; do not attach a Send action to the recommendation card.

## Boundary with the algorithm/data teammates

The frontend supplies the selected current commission context and displays the returned recommendation data. It needs:

- The current commission title, type, and genre for the selector/context.
- Suggested client username and platform.
- Previous commission title, finished date, and a reference for opening its record.
- Returned matched type/genre or a short reason the frontend can label accurately.

Choose the result shape and identifiers with the implementation team; this is not a database schema. Keep genre distinct from style/lighting tags when mapping data. Image content, pose similarity, payment amount, budgets, engagement predictions, and image embeddings are not criteria in this requested feature.

Teammates must still decide the exact matching/backtracking procedure, partial-match policy, ordering, repeated-client handling, and whether clients with active work should be excluded. Do not invent percentage scores or present those policies as finalized UI requirements.

## Sample fixture

Current **Fantasy portrait / mika.draws** has Portrait/Fantasy context. Sample suggestion **@tala.arts / X** references **Autumn portrait**, finished October 30, 2026, with the displayed reason Same type & genre. Its button opens that completed record. The sample Half-body illustration selection has no results solely to demonstrate the empty state; it does not establish an algorithm rule.

See [layout.md](../layout.md#wf19), [clickable instructions](wireframes/CLICKABLE.md), and [saved board](wireframes/reconnect.jpg).
