#!/usr/bin/env python3
"""
Automated Git Commit Script for OBRA
Enforces per-file distinct commits with natural sentence descriptions.
No conventional commit prefixes (feat:, fix:, etc.) and no blanket commits.
"""

import argparse
import os
import re
import subprocess
import sys

# Approved per-file commit description mapping for OBRA
FILE_COMMIT_MAPPING = {
    # Root documentation & config
    "README.md": "Document studio assistant overview, handoff guides, and feature vision",
    "AUTO_COMMIT.md": "Define automated commit protocols, per-file commit standards, and AI triggers",
    "CONTRIBUTING.md": "Establish contribution standards, wireframe review criteria, and git safety rules",
    "database-guide.md": "Define SQLite schema, table relationships, and payment calculation queries",
    "layout.md": "Detail five-tab navigation, screen inventory, and UI visual specifications",
    "reconnect-algo.md": "Formulate client recommendation algorithm with genre matching and cooldown rules",
    "OBRA_Layout_and_Database_Guide.pdf": "Export compiled layout and database guide PDF document",
    ".gitignore": "Exclude Android build artifacts, local SQLite databases, and sensitive keystores",
    ".gitattributes": "Enforce cross-platform LF line endings and proper file encodings",

    # Architecture & specification guides (docs/)
    "docs/dashboard.md": "Specify dashboard information hierarchy, earnings summary, and quick actions",
    "docs/commissions-list.md": "Specify commission filtering by status, search by client, and archive rules",
    "docs/commission-details.md": "Detail commission inspection view including stage progress and payment status",
    "docs/add-commission.md": "Define commission creation workflow, package presets, and deposit defaults",
    "docs/edit-commission.md": "Document commission editing rules, field updates, and historical integrity",
    "docs/record-payment.md": "Detail payment recording mechanics, balance updates, and receipt logging",
    "docs/main-history.md": "Specify payment ledger display, month-by-month grouping, and transaction links",
    "docs/insights.md": "Detail studio financial metrics, package popularity, and recurring revenue charts",
    "docs/reconnect.md": "Document client re-engagement suggestions, follow-up cooldowns, and message templates",
    "docs/image-viewer.md": "Specify reference image gallery, pinch-to-zoom gestures, and artwork storage",
    "docs/update-progress.md": "Detail creative milestone checklist and client progress update mechanics",
    "docs/data-plan.md": "Outline database schema evolution, migration strategy, and backup exports",
    "docs/decisions.md": "Log architectural decisions, UI trade-offs, and technical direction records",
    "docs/roadmap.md": "Outline development milestones from wireframes to native Android beta",
    "docs/requirements.md": "Document core user stories, functional requirements, and offline-first constraints",
    "docs/design-direction.md": "Establish typography, dark-mode color palette, and component visual styling",
    "docs/screens.md": "Catalog complete screen registry and navigation transitions for Android handoff",
    "docs/wireframes/README.md": "Index wireframe boards, preview assets, and handoff navigation flows",
    "docs/wireframes/CLICKABLE.md": "Provide walkthrough instructions and route map for clickable phone prototype",

    # Wireframe interactive HTML & preview images
    "docs/wireframes/clickable.html": "Build interactive standalone five-tab mobile phone wireframe prototype",
    "docs/wireframes/clickable.jpg": "Update visual board preview of clickable phone wireframe prototype",
    "docs/wireframes/dashboard.html": "Wireframe studio dashboard overview with earnings, deadlines, and active commissions",
    "docs/wireframes/dashboard.jpg": "Update visual preview of studio dashboard overview board",
    "docs/wireframes/commissions-list.html": "Wireframe active, queued, and completed commission inventory views",
    "docs/wireframes/commissions-list.jpg": "Update visual preview of commission inventory board",
    "docs/wireframes/commission-details.html": "Wireframe commission detail view with client brief, references, and stage trackers",
    "docs/wireframes/commission-details.jpg": "Update visual preview of commission detail board",
    "docs/wireframes/add-commission.html": "Wireframe new commission intake modal with package tiers and tag selectors",
    "docs/wireframes/add-commission.jpg": "Update visual preview of new commission intake board",
    "docs/wireframes/edit-commission.html": "Wireframe commission editing screen with prefilled briefs and change validation",
    "docs/wireframes/edit-commission.jpg": "Update visual preview of commission editing board",
    "docs/wireframes/record-payment.html": "Wireframe payment transaction logger with deposit and final balance options",
    "docs/wireframes/record-payment.jpg": "Update visual preview of payment transaction logging board",
    "docs/wireframes/main-history.html": "Wireframe consolidated payment history ledger grouped by month",
    "docs/wireframes/main-history.jpg": "Update visual preview of consolidated payment history board",
    "docs/wireframes/insights.html": "Wireframe studio analytics dashboard with monthly income trends and package breakdown",
    "docs/wireframes/insights.jpg": "Update visual preview of studio analytics dashboard board",
    "docs/wireframes/reconnect.html": "Wireframe client reconnect suggestions ranked by past interactions and genres",
    "docs/wireframes/reconnect.jpg": "Update visual preview of client reconnect recommendation board",
    "docs/wireframes/image-viewer.html": "Wireframe full-screen reference and progress art viewer with zoom controls",
    "docs/wireframes/image-viewer.jpg": "Update visual preview of full-screen artwork viewer board",
    "docs/wireframes/update-progress.html": "Wireframe progress stage updater with checklist milestones and client notes",
    "docs/wireframes/update-progress.jpg": "Update visual preview of progress stage updater board",
    "docs/wireframes/payment-history.html": "Wireframe commission-specific payment history breakdown view",
    "docs/wireframes/payment-history.jpg": "Update visual preview of commission payment breakdown board",

    # Automation scripts
    "scripts/auto_commit.py": "Automate per-file distinct git commits following project sentence standards",
    "scripts/auto_commit.ps1": "PowerShell wrapper script to trigger automated per-file commit pipeline",
}


def run_git(args, check=True):
    """Run a git command as a list of args."""
    cmd = ["git"] + args
    result = subprocess.run(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
    if check and result.returncode != 0:
        raise RuntimeError(f"Git command failed ({result.returncode}): {' '.join(cmd)}\n{result.stderr}")
    return result.stdout.strip()


def normalize_path(path: str) -> str:
    """Normalize file path to use forward slashes and remove quotes."""
    clean = path.strip().strip('"').strip("'")
    return clean.replace("\\", "/")


def derive_description(file_path: str) -> str:
    """Derive a compliant, natural sentence description for unmapped files."""
    norm = normalize_path(file_path)

    # Check direct match
    if norm in FILE_COMMIT_MAPPING:
        return FILE_COMMIT_MAPPING[norm]

    # Check prefix or folder match
    for pattern, desc in FILE_COMMIT_MAPPING.items():
        if norm.startswith(pattern):
            return desc

    # Heuristic fallback based on folder and extension
    basename = os.path.basename(norm)
    name_no_ext, ext = os.path.splitext(basename)
    cleaned_name = re.sub(r"[-_]", " ", name_no_ext).title()

    if norm.startswith("docs/wireframes/source/"):
        return f"Update raw source layout template for {cleaned_name}"
    elif norm.startswith("docs/wireframes/"):
        if ext == ".html":
            return f"Wireframe interactive prototype for {cleaned_name}"
        elif ext in [".jpg", ".png", ".webp"]:
            return f"Update visual wireframe preview for {cleaned_name}"
        return f"Update wireframe asset {basename}"
    elif norm.startswith("docs/"):
        return f"Document {cleaned_name} specifications and architecture guide"
    elif norm.startswith("scripts/"):
        return f"Implement {cleaned_name} automation script for repository workflow"
    elif norm.startswith("app/src/main/java/"):
        return f"Implement {cleaned_name} Android component for Obra"
    elif norm.startswith("app/"):
        return f"Configure Android app module configuration for {cleaned_name}"
    elif ext == ".pdf":
        return f"Export compiled {cleaned_name} PDF document"
    else:
        return f"Update {basename} to reflect latest project requirements"


def get_changed_files():
    """Return list of modified, added, untracked, or deleted files from git status."""
    result = subprocess.run("git status --porcelain -uall", stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True, shell=True)
    if result.returncode != 0:
        raise RuntimeError(f"Failed to get git status: {result.stderr}")
    
    changed = []
    for line in result.stdout.splitlines():
        if not line or len(line) < 4:
            continue
        # Porcelain status format: XY <filename> or XY <old> -> <new>
        entry = line[3:].strip()
        if " -> " in entry:
            entry = entry.split(" -> ")[1].strip()
        entry = normalize_path(entry)
        if entry not in changed:
            changed.append(entry)
    return changed


def main():
    parser = argparse.ArgumentParser(description="OBRA Automated Per-File Git Committer")
    parser.add_argument("--push", action="store_true", help="Push to origin main after committing")
    parser.add_argument("--dry-run", action="store_true", help="Show proposed commit messages without committing")
    args = parser.parse_args()

    changed_files = get_changed_files()
    if not changed_files:
        print("✅ Working tree clean. Nothing to commit.")
        return

    print(f"🔍 Found {len(changed_files)} changed file(s):")
    for f in changed_files:
        desc = derive_description(f)
        print(f"  • {f} -> \"{desc}\"")

    if args.dry_run:
        print("\n[Dry Run] No git commands executed.")
        return

    committed = []
    for f in changed_files:
        desc = derive_description(f)
        try:
            run_git(["add", f])
            run_git(["commit", "-m", desc])
            commit_hash = run_git(["rev-parse", "--short", "HEAD"])
            committed.append((f, commit_hash, desc))
            print(f"✔ Committed: [{commit_hash}] {desc} ({f})")
        except Exception as e:
            print(f"❌ Failed to commit {f}: {e}", file=sys.stderr)
            sys.exit(1)

    print(f"\n🎉 Successfully created {len(committed)} distinct commit(s).")

    if args.push:
        print("\n🚀 Pushing commits to origin main...")
        push_output = run_git(["push", "origin", "main"])
        print(push_output or "Pushed to origin main successfully.")
        print("✅ All commits pushed to GitHub!")


if __name__ == "__main__":
    main()
