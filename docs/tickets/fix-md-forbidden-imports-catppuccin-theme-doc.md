---
status: todo
created: 2026-02-25
completed:
---

# Fix "Package directive and imports are forbidden" errors in catppuccin theme doc

## Goal

Four code fragments in `docs/plans/archive/2026-02-25-catppuccin-frappe-mocha-theme.md`
contain `package` or `import` statements, which are not allowed in Kotlin code fragments
(as opposed to full Kotlin files). Fix the fenced code blocks at lines 198, 264, 313, and 326
so they are either valid script/REPL fragments or fenced with a non-Kotlin language tag.

## Acceptance criteria

- [ ] No annotator errors in `docs/plans/archive/2026-02-25-catppuccin-frappe-mocha-theme.md`
- [ ] Lines 198, 264, 313, 326 no longer trigger "Package directive and imports are forbidden in code fragments"

## Notes

Inspection: **General > Annotator**
Locations: `docs/plans/archive/2026-02-25-catppuccin-frappe-mocha-theme.md` lines 198, 264, 313, 326
