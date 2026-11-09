# 📜 Auto-Commit Guidelines & Standards (`AUTO_COMMIT.md`)

This document defines the mandatory git commit standards and automated commit protocols for **OBRA** (A personal Android studio assistant for freelance artists).

> [!IMPORTANT]
> **AI Coding Assistants & Developers**: Whenever instructed to **"auto commit"**, **"commit changes"**, or **"sync to GitHub"**, you must strictly adhere to the rules in this document. Never use conventional commit prefixes (`feat:`, `fix:`, etc.) and never make repetitive blanket commits.

---

## 🤖 AI Assistant Directives (When Ordered to "Auto Commit")

When the user gives a prompt such as:
- *"auto commit"*
- *"commit changes and push to github"*
- *"commit this"*
- *"sync everything to github"*

The AI assistant must automatically execute the following sequence:

1. **Inspect Git Status**: Run `git status --porcelain -uall` to identify all untracked, modified, or deleted files.
2. **Determine File-by-File Descriptions**:
   - Match each file against the [Repository File Mapping](#-repository-file-mapping--approved-descriptions) table below.
   - For unlisted files, generate a **clear, natural sentence explanation** beginning with an active verb (e.g., *Add*, *Implement*, *Wireframe*, *Document*, *Optimize*, *Refactor*), capitalized first word, and no trailing period.
3. **Stage & Commit Individually (Per-File Rule)**:
   - Run `git add <file>` followed by `git commit -m "<Distinct Description>"` for each file or tightly coupled unit.
   - Never stage all disparate files under a single generic message.
4. **Push to Remote**: Run `git push origin main`.
5. **Report to User**: Output a clean summary table listing each committed file, its commit hash, and the distinct commit message applied.

---

## 🚫 Prohibited Commit Formats (Anti-Patterns)

The following styles are **strictly prohibited** across the entire repository:

1. **NO Conventional Commit Prefixes**:
   - ❌ `feat: initial commit - Obra mobile assistant...`
   - ❌ `fix(database): update sqlite foreign key cascades...`
   - ❌ `(docs) update README and troubleshooting`
   - ❌ `chore: update dependencies`
   - ❌ `refactor: clean up commission filter logic`

2. **NO Repetitive Blanket Commits**:
   - ❌ Staging all files together with a generic message so that every file in the GitHub file tree displays identical repeated text.
   - ❌ Commits containing generic placeholders like `"WIP"`, `"Update files"`, `"Fixes"`, `"Changes"`, `"Misc"`, or `"Updated docs"`.

---

## ✅ Approved Commit Formats (Reference Standard)

All commit messages must be formatted as **clear, natural sentence explanations** of the exact capability, architecture change, or fix introduced.

### Principles:
- **Capitalized First Word**: Always start with a capital letter.
- **Descriptive Action Verb**: Use clear verbs (*Add, Draft, Implement, Wireframe, Detail, Document, Configure, Refactor, Calibrate, Sync, Record, Establish*).
- **Concise & Meaningful**: State what the code or document actually does in plain, professional technical language.
- **No Trailing Periods**: Adhere to standard git headline convention (omit the final period).

### Direct Examples (following project history & standards):
- `Add SQLite and Reconnect handoff guides to repository front page`
- `Sync five-tab wireframes and clickable frontend handoff`
- `Draft prefilled commission editing and change review wireframes`
- `Draft Insights earnings charts and outstanding balance views`
- `Draft main transaction History and empty month wireframes`
- `Draft progress and completion wireframes with numbered routes`
- `Index all wireframes with routes, scroll states, and visual previews`
- `Draft commissions list and distinguish artwork archive from payments`
- `Draft record-payment form and saved-history state`
- `Implement Room database schema for commission intake and payment tracking`
- `Calibrate reconnection algorithm for genre and art style matching`

---

## 🎯 Per-File Distinct Descriptions Rule

In GitHub's repository view, each row displays the commit message from the **most recent commit that touched that specific file**.

To ensure that **every file row maintains a distinct, informative description**:

1. **Never commit all unrelated files in a single blanket commit.**
2. When multiple files are created or modified, stage and commit each file (or tightly coupled unit) individually with its own specific description.
3. Every file must have a description tailored to its unique role in the project.

### Example Workflow for Multi-File Changes:

```bash
# 1. Commit the layout guide
git add layout.md
git commit -m "Detail five-tab navigation hierarchy and color system in layout guide"

# 2. Commit the database guide
git add database-guide.md
git commit -m "Specify SQLite tables and foreign key constraints for commission payments"

# 3. Commit the reconnection algorithm
git add reconnect-algo.md
git commit -m "Formulate client recommendation algorithm with genre matching and cooldown rules"

# 4. Push all distinct commits to remote
git push origin main
```

---

## 📂 Repository File Mapping & Approved Descriptions

When modifying or committing repository files, use these distinct descriptive templates:

### Root Documentation & Configuration

| File / Folder | Role & Distinct Commit Description Template |
| :--- | :--- |
| `README.md` | `Document studio assistant overview, handoff guides, and feature vision` |
| `AUTO_COMMIT.md` | `Define automated commit protocols, per-file commit standards, and AI triggers` |
| `CONTRIBUTING.md` | `Establish contribution standards, wireframe review criteria, and git safety rules` |
| `database-guide.md` | `Define SQLite schema, table relationships, and payment calculation queries` |
| `layout.md` | `Detail five-tab navigation, screen inventory, and UI visual specifications` |
| `reconnect-algo.md` | `Formulate client recommendation algorithm with genre matching and cooldown rules` |
| `OBRA_Layout_and_Database_Guide.pdf` | `Export compiled layout and database guide PDF document` |
| `.gitignore` | `Exclude Android build artifacts, local SQLite databases, and sensitive keystores` |
| `.gitattributes` | `Enforce cross-platform LF line endings and proper file encodings` |

### Architecture & Specification Guides (`docs/`)

| File / Folder | Role & Distinct Commit Description Template |
| :--- | :--- |
| `docs/dashboard.md` | `Specify dashboard information hierarchy, earnings summary, and quick actions` |
| `docs/commissions-list.md` | `Specify commission filtering by status, search by client, and archive rules` |
| `docs/commission-details.md` | `Detail commission inspection view including stage progress and payment status` |
| `docs/add-commission.md` | `Define commission creation workflow, package presets, and deposit defaults` |
| `docs/edit-commission.md` | `Document commission editing rules, field updates, and historical integrity` |
| `docs/record-payment.md` | `Detail payment recording mechanics, balance updates, and receipt logging` |
| `docs/main-history.md` | `Specify payment ledger display, month-by-month grouping, and transaction links` |
| `docs/insights.md` | `Detail studio financial metrics, package popularity, and recurring revenue charts` |
| `docs/reconnect.md` | `Document client re-engagement suggestions, follow-up cooldowns, and message templates` |
| `docs/image-viewer.md` | `Specify reference image gallery, pinch-to-zoom gestures, and artwork storage` |
| `docs/update-progress.md` | `Detail creative milestone checklist and client progress update mechanics` |
| `docs/data-plan.md` | `Outline database schema evolution, migration strategy, and backup exports` |
| `docs/decisions.md` | `Log architectural decisions, UI trade-offs, and technical direction records` |
| `docs/roadmap.md` | `Outline development milestones from wireframes to native Android beta` |
| `docs/requirements.md` | `Document core user stories, functional requirements, and offline-first constraints` |
| `docs/design-direction.md` | `Establish typography, dark-mode color palette, and component visual styling` |
| `docs/screens.md` | `Catalog complete screen registry and navigation transitions for Android handoff` |

### Wireframe Assets & Prototypes (`docs/wireframes/`)

| File / Folder | Role & Distinct Commit Description Template |
| :--- | :--- |
| `docs/wireframes/CLICKABLE.md` | `Provide walkthrough instructions and route map for clickable phone prototype` |
| `docs/wireframes/clickable.html` | `Build interactive standalone five-tab mobile phone wireframe prototype` |
| `docs/wireframes/clickable.jpg` | `Update visual board preview of clickable phone wireframe prototype` |
| `docs/wireframes/dashboard.html` / `.jpg` | `Wireframe studio dashboard overview with earnings, deadlines, and active commissions` |
| `docs/wireframes/commissions-list.html` / `.jpg` | `Wireframe active, queued, and completed commission inventory views` |
| `docs/wireframes/commission-details.html` / `.jpg` | `Wireframe commission detail view with client brief, references, and stage trackers` |
| `docs/wireframes/add-commission.html` / `.jpg` | `Wireframe new commission intake modal with package tiers and tag selectors` |
| `docs/wireframes/edit-commission.html` / `.jpg` | `Wireframe commission editing screen with prefilled briefs and change validation` |
| `docs/wireframes/record-payment.html` / `.jpg` | `Wireframe payment transaction logger with deposit and final balance options` |
| `docs/wireframes/main-history.html` / `.jpg` | `Wireframe consolidated payment history ledger grouped by month` |
| `docs/wireframes/insights.html` / `.jpg` | `Wireframe studio analytics dashboard with monthly income trends and package breakdown` |
| `docs/wireframes/reconnect.html` / `.jpg` | `Wireframe client reconnect suggestions ranked by past interactions and genres` |
| `docs/wireframes/image-viewer.html` / `.jpg` | `Wireframe full-screen reference and progress art viewer with zoom controls` |
| `docs/wireframes/update-progress.html` / `.jpg` | `Wireframe progress stage updater with checklist milestones and client notes` |

### Automation & Scripts (`scripts/`)

| File / Folder | Role & Distinct Commit Description Template |
| :--- | :--- |
| `scripts/auto_commit.py` | `Automate per-file distinct git commits following project sentence standards` |
| `scripts/auto_commit.ps1` | `PowerShell wrapper script to trigger automated per-file commit pipeline` |

### Future Android Implementation (Target App Hierarchy)

| File / Folder | Role & Distinct Commit Description Template |
| :--- | :--- |
| `app/build.gradle.kts` | `Configure Android build dependencies for Room, Navigation Compose, and Material 3` |
| `app/src/main/AndroidManifest.xml` | `Declare application permissions, activities, and Android entry points` |
| `app/src/main/java/com/obra/data/local/` | `Implement Room entities, DAOs, and SQLite database instance for Obra` |
| `app/src/main/java/com/obra/data/repository/` | `Implement repository interfaces and offline-first data caching logic` |
| `app/src/main/java/com/obra/domain/algo/` | `Implement recommendation heuristic engine for client re-engagement` |
| `app/src/main/java/com/obra/ui/dashboard/` | `Build studio dashboard Compose UI and ViewModel for current work and earnings` |
| `app/src/main/java/com/obra/ui/commissions/` | `Build commission list, intake form, and detail view Compose components` |
| `app/src/main/java/com/obra/ui/history/` | `Build payment ledger and transaction history Compose views` |
| `app/src/main/java/com/obra/ui/insights/` | `Build studio revenue metrics and package breakdown analytics screens` |
| `app/src/main/java/com/obra/ui/reconnect/` | `Build client reconnection card list and follow-up composer UI` |
| `app/src/main/java/com/obra/ui/theme/` | `Define Material 3 color schemes, typography, and shape styling tokens` |

---

## 🤖 Automated Commit Helper Script (`scripts/auto_commit.py`)

An automated Python script is provided at [`scripts/auto_commit.py`](file:///scripts/auto_commit.py) to execute this entire process seamlessly.

### How Developers & AI Agents Use It:

Run the script whenever changes need to be committed:

```powershell
# Commit all modified files individually and push to GitHub:
py scripts/auto_commit.py --push

# Preview what commits would be generated without executing them:
py scripts/auto_commit.py --dry-run
```

### What It Does Automatically:
1. Runs `git status --porcelain` to inspect all modified, added, or untracked files.
2. Matches each changed file against the repository knowledge mapping in `AUTO_COMMIT.md`.
3. Stages each file or tightly coupled unit individually (`git add <file>`).
4. Generates and creates a distinct, unique sentence-style commit message for that exact file.
5. Pushes all commits to `origin main` when `--push` is supplied.
