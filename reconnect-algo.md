# OBRA Reconnect — recommendation algorithm handoff

Updated November 10, 2026. Read with [layout.md, WF-19](layout.md#wf19), the [Reconnect screen guide](docs/reconnect.md), and [database-guide.md](database-guide.md). This document explains the feature to the teammates/agent assigned to the algorithm. It does not implement the algorithm or replace the existing wireframe fixtures.

## 1. What the owner wants

An artist selects a current commission. Reconnect looks through completed commissions and suggests previous clients who ordered similar work, so the artist can choose to approach them again outside the app.

Use **commission type and genre only**: for example, Portrait + Fantasy or Chibi + Fantasy. No image processing, embeddings, pose recognition, payment-based ranking, budgets, social-media scraping, or AI service is needed. A suggestion means a client ordered similar artwork previously; it is not evidence that the client wants another commission now.

Reconnect is its own fifth main section. Insights stays focused on financial summaries. The frontend displays returned results, their matching labels, and links to previous artwork. It never sends a message or processes money.

## 2. Proposed simple v1 policy

The owner approved the feature and its two matching criteria. The detailed rules below are a **recommended baseline for the assigned algorithm team to review**, not additional owner-approved requirements.

| Rule | Proposed behavior |
| --- | --- |
| Selected source | One existing IN_PROGRESS commission from the current-work selector |
| Historical candidates | COMPLETED commissions with an actual finished date |
| Match | Exact equality of both stored type key AND primary genre key |
| Missing genre | Explain that a genre is needed; do not treat NULL as a wildcard or guess from a style tag |
| Current client | Exclude the selected current commission's client |
| Duplicate clients | One card per client, represented by that client's most recently finished matching artwork |
| Order | Most recently finished matching artwork first; break equal dates by commission ID descending |
| Result size | Up to five clients initially; this limit is a proposal, not a wireframe requirement |
| Clients with other active work | Include if otherwise eligible; broader active-client exclusion remains a team choice |
| Partial matches | None in this baseline; no type-only/genre-only fallback |
| Persistence | Read-only, calculated on demand; no recommendation table |

Match stable lookup keys from the database. Display names come from the joined lookup rows. Two different spellings of the same intended genre should be resolved during data entry/lookup setup, not through fuzzy matching inside Reconnect. Soft light is a style label, not the Fantasy genre. A retired lookup remains meaningful for existing commissions.

The database guide proposes one optional primary genre per commission. If the team chooses multiple genres, explicitly agree whether matching means any overlap or an identical set, add the join table/migration, and update this contract before implementing. Do not quietly change the meaning of “Same type & genre.”

## 3. Input and output contract

The UI supplies `selectedCommissionId`. The provider loads the current record from SQLite so its type/genre/client/status reflect saved data. The provider can use an internal default `limit = 5`; the screen does not need another settings control.

Suggested result structure, independent of specific Kotlin class names:

```text
ReconnectResult
  selectedCommissionId
  selectedTypeId, selectedTypeName
  selectedGenreId?, selectedGenreName?
  state: READY | NO_CURRENT_WORK | MISSING_GENRE | NO_MATCHES | ERROR
  recommendations[]
    clientId
    username
    platform: X | DISCORD
    previousCommissionId
    previousCommissionTitle
    previousFinishedOn
    matchedTypeId, matchedTypeName
    matchedGenreId, matchedGenreName
    reasonCode: SAME_TYPE_AND_GENRE
```

These are internal references, not labels to show users. The frontend turns the reason code into “Same type & genre” and the two matching chips. A result count is the number of returned **unique clients**, not the number of matching commission rows. Return an empty array for ordinary no-result states; reserve ERROR for a failed read/provider operation.

If the selected record was removed or is no longer IN_PROGRESS, refresh the selector and ask the user to select current work. Do not silently return recommendations for another record. The UI owns loading/retry feedback; MISSING_GENRE can use the same calm empty-state structure with a specific explanation, without adding a new page.

Example returned card, using fictional data:

```json
{
  "clientId": 12,
  "username": "@tala.arts",
  "platform": "X",
  "previousCommissionId": 41,
  "previousCommissionTitle": "Autumn portrait",
  "previousFinishedOn": "2026-10-30",
  "matchedTypeId": 4,
  "matchedTypeName": "Portrait",
  "matchedGenreId": 1,
  "matchedGenreName": "Fantasy",
  "reasonCode": "SAME_TYPE_AND_GENRE"
}
```

IDs are illustrative; the implementation uses real local keys. Opening this card routes to commission 41, preserving the selected current work and Reconnect scroll position for Back. It does not open the client's newest unrelated commission.

## 4. Candidate query and simple algorithm

First load and validate the selected current commission. If there is no selection/current work, return the corresponding state. If its primary genre is missing, return MISSING_GENRE. Then run this query using bound parameters:

```sql
-- Bind from the saved selected commission, not arbitrary screen label text.
SELECT c.id AS previous_commission_id, c.title AS previous_title,
       c.finished_on, c.client_id, cl.username, cl.platform,
       t.id AS type_id, t.name AS type_name,
       g.id AS genre_id, g.name AS genre_name
FROM commissions c
JOIN clients cl ON cl.id = c.client_id
JOIN commission_types t ON t.id = c.type_id
JOIN genres g ON g.id = c.primary_genre_id
WHERE c.status = 'COMPLETED'
  AND c.finished_on IS NOT NULL
  AND c.type_id = :selected_type_id
  AND c.primary_genre_id = :selected_genre_id
  AND c.client_id <> :selected_client_id
ORDER BY c.finished_on DESC, c.id DESC;
```

Do not apply SQL `LIMIT 5` before deduplicating: five artworks might belong to one client and hide other eligible clients. Iterate the ordered cursor and stop after collecting five unique clients, or use another proven query that produces one row per client first.

```text
recommend(selectedCommissionId, limit = 5):
    validate limit is positive
    selected = load current commission by ID
    if selected is absent or no longer current:
        return NO_CURRENT_WORK / refresh-selection response
    if selected.primaryGenre is missing:
        return MISSING_GENRE

    candidates = query completed artwork matching both keys,
                 excluding selected.clientId,
                 ordered by finished date descending, then ID descending
    seenClients = empty set
    results = empty list

    for each candidate in candidates:
        if candidate.clientId is already in seenClients:
            continue
        add candidate.clientId to seenClients
        append card(candidate, reason = SAME_TYPE_AND_GENRE) to results
        if results.size == limit:
            stop

    if results is empty: return NO_MATCHES
    return READY with results
```

If there are `M` matching historical rows, the service loop examines at most `M` rows and stores at most the requested number of unique clients. SQLite handles filtering/ordering; this loop complexity is not a claim about the total database query cost. There is no need for a percentage score or weighted ranking for two exact-match fields.

## 5. About the requested “backtracking” term

The owner initially described this as a backtracking recommendation idea and assigned the actual algorithm work to teammates. **The simple exact-match filter and deduplication above are not backtracking.** They satisfy the product behavior with less machinery; do not label an ordinary SQL filter/loop as a backtracking implementation in code or academic documentation.

If the academic requirement specifically demands backtracking, the algorithm team must define the search problem and constraints. A genuine backtracking procedure makes a choice, explores it, detects a failed constraint/dead end, undoes the choice, and tries alternatives. Under only “same type, same genre, unique clients,” the proposed shortlist has no meaningful conflicting combinations requiring that search; adding artificial constraints would change the requested feature.

The recommended handoff is to keep this simple matching baseline unless the team confirms a course requirement. If backtracking is mandatory, document the necessary additional problem definition, stopping conditions, pruning, and tests with the academic adviser/team. Keep the frontend result contract and type/genre-only matching scope intact. Algorithm implementation remains the assigned teammates' responsibility.

## 6. Wireframe behavior and refresh rules

| Trigger | Required frontend/provider behavior |
| --- | --- |
| Open Reconnect | Show available current work; request saved selection if still valid, otherwise a clear selection/default agreed with frontend |
| Select another current artwork | Display its context, clear obsolete cards, show loading, request that ID |
| Fast selection changes | Cancel/ignore an older request's response if its selected ID/request version no longer matches |
| Receive READY | Show unique-client count, type/genre labels, username/platform, previous title/date, and View previous commission |
| No current work | Explain; View commissions opens Current |
| Missing genre or no matches | Keep useful context; explain the specific reason without fabricating a match |
| Provider/read error | Retain selection, show Retry, do not show an error as “no clients found” |
| View previous commission | Open the exact previous record in Details, even if another artwork is more recent |
| Back from Details | Restore selected current artwork, cards, and scroll; refresh if saved data changed |
| Change Insights month | No effect on Reconnect |

Refresh after commission type/genre/contact/status changes or after new completion, and when returning to the page if its history changed. Read a consistent saved snapshot, run database work off the Android UI thread, and close cursors. No cloud/network API, background outreach, image loading, or payment data is needed for matching.

The current clickable wireframe uses a fixed fixture: **Fantasy portrait / mika.draws → @tala.arts / Autumn portrait / Portrait + Fantasy / finished October 30, 2026**. The sample Half-body illustration selection shows no matches to demonstrate an empty state. These fixtures are not a rule that all half-body artwork has no matches. Replacing them with real results is later Android implementation work, not a reason to rewrite the handoff wireframe now.

## 7. Test examples for the algorithm team

With a selected **Portrait + Fantasy** current commission for client A:

| Historical row | Include? | Why |
| --- | --- | --- |
| Client B, completed Portrait + Fantasy | Yes | Both fields match |
| Client C, completed Chibi + Fantasy | No | Different type |
| Client D, completed Portrait + Romance | No | Different genre |
| Client E, current Portrait + Fantasy | No | Candidate artwork is not completed |
| Client F, cancelled Portrait + Fantasy | No | Cancelled is not completed |
| Client A, completed Portrait + Fantasy | No under proposal | Same client as selected work |
| Client B, another older completed Portrait + Fantasy | No additional card | Keep B's latest matching artwork |
| Client B, newer completed Chibi + Fantasy | No replacement | Newer artwork does not satisfy the selected type |
| Client G, completed Portrait with no primary genre | No | Missing genre cannot prove a match |
| Client H, completed Portrait + Fantasy, Soft light tag absent | Yes | Style/lighting tags are irrelevant |
| Client I, completed Portrait + Fantasy, balance unpaid | Yes | Payment status does not affect eligibility |

Also test:

- No saved commissions, no current work, no completed work, no matching work, and missing selected genre produce clear distinct conditions.
- Five recent matches from one client plus a sixth from another produce two clients, not one because of a premature SQL limit.
- Equal finish dates produce stable results using ID tie-breaking; repeated calls without data changes return the same order.
- Different clients with similar usernames remain distinct; the proposed identity is the database client record/platform pair, not display-name similarity.
- X and Discord entries are not merged automatically, even if their visible usernames match.
- Amounts, due dates, pose notes, image contents, and image-access failures do not change matching.
- A failed database read produces ERROR, not NO_MATCHES. A stale result for selection A cannot replace newer selection B's results.
- Opening a suggestion/Back preserves the current selector; deleting/unavailable previous artwork is handled without opening an unrelated record.

## 8. Handoff instructions for the implementing agent

1. Read this guide, [database-guide.md](database-guide.md), [layout.md](layout.md), and [open decisions](docs/decisions.md). State which proposed defaults the team is adopting.
2. Agree on exact-match policy, genre cardinality, contact identity, current-client/active-client exclusion, ordering, and result limit. Resolve an academic backtracking requirement explicitly rather than claiming the simple loop is that algorithm.
3. Implement a read-only recommendation provider against saved SQLite records and the agreed output contract. No new screen, scoring UI, external API, or automatic contact action is requested.
4. Test eligibility, client deduplication, stable ordering, missing data, stale selection, and links using fictional history.
5. Document the implemented procedure and its limits accurately. Update these guides and the frontend contract if a reviewed policy changes.

No recommendation can promise repeat business. The artist decides whether to contact someone manually using the displayed username/platform; OBRA only helps find relevant previous work.
