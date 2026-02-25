---
status: todo
created: 2026-02-25
completed:
---

# Rename _url parameter in HtmlBase.kt to follow Kotlin naming convention

## Goal

`selfLink` in `src/main/kotlin/de/noah_ruben/site/HtmlBase.kt` has a local variable named `_url`
at line 19. Kotlin naming convention requires local variable names to start with a lowercase letter
without a leading underscore. Rename it to `url` (or a more descriptive name).

## Acceptance criteria

- [ ] `_url` renamed to a conventional lowerCamelCase name in `HtmlBase.kt`
- [ ] No "Local variable name should start with a lowercase letter" weak warning at that location
- [ ] All call sites updated; existing tests pass

## Notes

Inspection: **Kotlin > Naming conventions > Local variable naming convention**
Location: `src/main/kotlin/de/noah_ruben/site/HtmlBase.kt` line 19
