---
status: todo
created: 2026-02-25
completed:
---

# Fix: Remove unused symbols in Application.kt and CssClasses.kt

## Goal

The JetBrains "Unused symbol" inspection reports 20 warnings across several files. Dead code increases
maintenance burden and creates a misleading impression of the API surface.

### `Application.kt`
- `getToken()` (line 85) — private extension function on `Application`, never called anywhere.

### `CssClasses.kt` (in `de.noah_ruben.misc`)
- `MB_8` (line 10) — top-level property in `CssClasses`, never used.
- `LINK` (line 13) — top-level property in `CssClasses`, never used.
- `CssClasses.Form.FORM_FIELD` (line 62) — never used.
- `CssClasses.Form.FORM_INPUT_TEXT` (line 65) — never used.
- `CssClasses.Form.TOGGLE_BUTTON_ICON` (line 74) — never used.
- `CssClasses.Form.TOGGLE_BUTTON_ICON_MOON` (line 75) — never used.
- `CssClasses.Form.TOGGLE_BUTTON_ICON_SUN` (line 76) — never used.

### Other unused classes (separate packages — verify scope before removing)
The inspection also flags `GitHubClient` (`de.noah_ruben.data`) and `GithubClientFake`
(`de.noah_ruben.data`) as unused. These may be intentionally kept for future use or manual
switching — assess whether to remove or suppress before acting.

## Acceptance criteria

- [ ] `Application.getToken()` is removed from `Application.kt`.
- [ ] `CssClasses.MB_8` and `CssClasses.LINK` are removed (or confirmed still needed and suppressed).
- [ ] `CssClasses.Form.FORM_FIELD`, `FORM_INPUT_TEXT`, `TOGGLE_BUTTON_ICON`, `TOGGLE_BUTTON_ICON_MOON`,
      `TOGGLE_BUTTON_ICON_SUN` are removed (or confirmed still needed and suppressed).
- [ ] `GitHubClient` and `GithubClientFake` are assessed and either removed or annotated/suppressed.
- [ ] `./amper build` passes.
- [ ] `./amper test` passes.

## Notes

- Inspection source: `issues/index.html` — Kotlin > Unused symbol inspection, WARNING (20 total).
- Before removing `GitHubClient`: check if it is used anywhere in tests or mentioned in docs as
  the production client to switch to. If so, keep it and suppress the warning with a comment.
- The `Format` enum in `com.dvag.noah.ruben.build.quality` (entries `BASELINE`, `CHECKSTYLE`, etc.)
  is also flagged — it appears to be build-tooling code outside the main application module;
  exclude it from this ticket unless it lives in a tracked source set.
