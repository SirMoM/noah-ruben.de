---
status: todo
created: 2026-02-25
completed:
---

# Refactor: Remove redundant conversion method call in Cache.kt

## Goal

The JetBrains "Redundant call of conversion method" inspection reports a redundant call in
`src/main/kotlin/de/noah_ruben/data/Cache.kt` at line 57. The `getProjects()` function calls a
conversion method whose result is already of the target type, making the conversion unnecessary.

## Acceptance criteria

- [ ] The redundant conversion call at `Cache.kt:57` is removed.
- [ ] `./amper test` passes (cache behaviour is unchanged).

## Notes

- Inspection source: `issues/index.html` — Redundant constructs group → "Redundant call of the
  conversion method" inspection, WARNING.
- Class: `Cache` in `de.noah_ruben.data`.
