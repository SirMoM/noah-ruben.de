---
status: todo
created: 2026-02-25
completed:
---

# Fix: Incorrect Markdown table formatting

## Goal

The JetBrains Markdown inspection reports "Table is not correctly formatted" in three files. Malformed
tables can render incorrectly in GitHub previews and documentation viewers.

Affected files and locations:
- `docs/plans/archive/2026-02-25-catppuccin-frappe-mocha-theme.md` — line 412
- `README.md` — line 144
- `docs/ui-design-punchlist.md` — line 9

## Acceptance criteria

- [ ] The table at `docs/plans/archive/2026-02-25-catppuccin-frappe-mocha-theme.md:412` is correctly
      formatted (consistent column count and separator row).
- [ ] The table at `README.md:144` is correctly formatted.
- [ ] The table at `docs/ui-design-punchlist.md:9` is correctly formatted.
- [ ] No new Markdown table formatting warnings are introduced.

## Notes

- Inspection source: `issues/index.html` — Markdown group → "Incorrect table formatting" inspection,
  WEAK WARNING (3 occurrences).
- Common causes: missing or misaligned separator row (`| --- |`), inconsistent column counts, or
  extra/missing pipe characters.
