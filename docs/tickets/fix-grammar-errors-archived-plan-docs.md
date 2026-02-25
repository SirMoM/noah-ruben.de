---
status: todo
created: 2026-02-25
completed:
---

# Fix: Grammar errors in archived plan documents

## Goal

The JetBrains Proofreading → Grammar inspection reports 13 grammar errors across two archived
plan documents. While these are archived docs (not production code), fixing them keeps the
repository's written content professional and internally consistent.

### `docs/plans/archive/2026-02-24-kotlin-only-styles.md` — 6 grammar errors
- Line 160: Missing punctuation
- Line 201: `'list' usually goes with an article` (×2)
- Line 206: `Did you mean 'cappuccino'?` (×2) — "Catppuccin" is a colour scheme name, not
  misspelled; this may warrant suppression rather than a change.
- Line 229: Missing article

### `docs/plans/archive/2026-02-25-catppuccin-frappe-mocha-theme.md` — 7 grammar errors
- Lines 12, 24, 28, 235, 394, 395, 396: `'Frappe' is an imported foreign name or expression,
  which originally has a diacritic.` — "Frappé" with accent. Again "Frappé" is a brand/theme
  name; decide whether to add the accent or suppress.

## Acceptance criteria

- [ ] Missing punctuation and article issues at `2026-02-24-kotlin-only-styles.md:160` and `:229`
      are corrected.
- [ ] The `'list' usually goes with an article` issues at lines 201 are corrected or suppressed.
- [ ] The `'cappuccino'` and `'Frappe'` suggestions are reviewed and either the diacritic/spelling
      is added where appropriate, or the occurrences are left as-is with a rationale noted (they
      are proper brand names in this context).
- [ ] No regressions to the meaning of any archived document.

## Notes

- Inspection source: `issues/index.html` — Proofreading > Grammar inspection, 16 GRAMMAR ERRORs
  total (6 + 7 = 13 in these two files; the summary count of 16 includes duplicated synopsis
  entries).
- Both files are in `docs/plans/archive/` and are not rendered on the live site.
- Since "Catppuccin" and "Frappé" are proper names/brand names, suppress rather than alter them
  unless there is a strong reason to add diacritics.
