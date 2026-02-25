---
status: todo
created: 2026-02-25
completed:
---

# Deduplicate code fragment in LandingPage.kt lines 109-113

## Goal

The IDE reports a duplicated code fragment at lines 109-113 inside `indexPageContent` in
`src/main/kotlin/de/noah_ruben/site/LandingPage.kt`.
Extract the duplicated block into a shared helper so it is defined once.

## Acceptance criteria

- [ ] No "Duplicate code: lines 109-113" weak warning in `LandingPage.kt`
- [ ] Behaviour of `indexPageContent` is unchanged (existing tests pass)

## Notes

Inspection: **General > Duplicated code fragment**
Location: `src/main/kotlin/de/noah_ruben/site/LandingPage.kt` lines 109-113
Related: `refactor-duplicate-code-command-line-emulation.md`
