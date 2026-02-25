---
status: todo
created: 2026-02-25
completed:
---

# Fix: Redundant escaped dollar characters in StyleConventionsTest.kt

## Goal

The JetBrains "Redundant escaped dollar characters in string literals" inspection reports a
WEAK WARNING in `src/test/kotlin/de/noah_ruben/site/StyleConventionsTest.kt` at line 24.
The string `"classes = \$\""` contains an escaped `\$` that can be simplified — in a regular
Kotlin string literal, `$` does not need escaping when it is not followed by a valid identifier
or brace expression.

The pattern can be simplified to `"classes = $\""` or rewritten with a raw string to improve
readability.

## Acceptance criteria

- [ ] The escaped dollar character at `StyleConventionsTest.kt:24` is simplified or removed.
- [ ] `./amper test --include-classes="de.noah_ruben.site.StyleConventionsTest"` passes.

## Notes

- Inspection source: `issues/index.html` — Redundant constructs group → "Redundant escaped dollar
  characters in string literals" inspection, WEAK WARNING.
- Class: `StyleConventionsTest` in `de.noah_ruben.site`.
- Line 24 content: `val rawPatterns = listOf("classes = \"", "classes = \$\"", "classes = setOf(\"")`
