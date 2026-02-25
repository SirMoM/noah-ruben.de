---
status: todo
created: 2026-02-25
completed:
---

# Fix "Expecting an element" annotator errors in kotlin-only-styles.md

## Goal

Two lines in `docs/plans/archive/2026-02-24-kotlin-only-styles.md` produce annotator errors
"Expecting an element" because the Kotlin code fence snippets at lines 146 and 149 contain
incomplete or syntactically broken Kotlin fragments. Fix or annotate the code blocks so the
Kotlin parser no longer reports errors inside the markdown file.

## Acceptance criteria

- [ ] No annotator errors remain in `docs/plans/archive/2026-02-24-kotlin-only-styles.md`
- [ ] Lines 146 and 149 contain syntactically valid Kotlin (or non-Kotlin fences if intentional)

## Notes

Inspection: **General > Annotator**
Locations: `docs/plans/archive/2026-02-24-kotlin-only-styles.md` lines 146, 149
