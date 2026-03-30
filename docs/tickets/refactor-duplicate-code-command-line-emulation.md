---
status: todo
created: 2026-02-25
completed:
---

# Deduplicate code fragment in CommandLineEmulation.kt lines 113-126

## Goal

The IDE reports a duplicated code fragment at lines 113-126 inside `cleUsage` in
`src/main/kotlin/de/noah_ruben/site/CommandLineEmulation.kt`.
Extract the duplicated block into a shared helper so it is defined once.
The extraction must cover the shared help content rendered from both
`src/main/kotlin/de/noah_ruben/site/CommandLineEmulation.kt` and
`src/main/kotlin/de/noah_ruben/site/LandingPage.kt`.

## Acceptance criteria

- [ ] No "Duplicate code: lines 113-126" weak warning in `CommandLineEmulation.kt`
- [ ] Behaviour of `cleUsage` is unchanged (existing tests pass)
- [ ] The rendered help content on the landing page remains unchanged after the extraction
- [ ] No unrelated UI copy changes are introduced outside the deduplicated help content
- [ ] The extracted helper preserves call-site-specific subpage links,
      including the landing page `cv` link

## Notes

Inspection: **General > Duplicated code fragment**
Location: `src/main/kotlin/de/noah_ruben/site/CommandLineEmulation.kt` lines 113-126
The duplicate presumably lives elsewhere in the same file or in `LandingPage.kt` (see related ticket).
The shared duplicate currently also exists in
`src/main/kotlin/de/noah_ruben/site/LandingPage.kt`.
