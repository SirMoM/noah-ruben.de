---
status: todo
created: 2026-02-25
completed:
---

# Fix: Remove unused import directives

## Goal

The JetBrains "Unused import directive" inspection reports warnings in two Kotlin source files.
Unused imports add noise to diffs and may confuse readers about which APIs are actually in use.

Affected locations:
- `src/main/kotlin/de/noah_ruben/site/CommandLineEmulation.kt` — line 22
- `src/test/kotlin/de/noah_ruben/site/ThemeToggleTest.kt` — line 9

## Acceptance criteria

- [ ] The unused import at `CommandLineEmulation.kt:22` is removed.
- [ ] The unused import at `ThemeToggleTest.kt:9` is removed.
- [ ] `./amper test --include-classes="de.noah_ruben.site.ThemeToggleTest"` passes.
- [ ] `./amper build` passes.

## Notes

- Inspection source: `issues/index.html` — Kotlin > Redundant constructs group → "Unused import
  directive" inspection, WARNING.
- `CommandLineEmulation.kt` line 22 is `import kotlinx.html.classes`.
- `ThemeToggleTest.kt` line 9 is `import kotlin.test.assertFalse`.
