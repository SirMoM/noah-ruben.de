# AGENTS Guide for `tailwind/`

This file is for agentic coding assistants working in `/Users/i13az81/dev/uni/noah-ruben.de/tailwind`.

Use these instructions for CSS/Tailwind tasks and for cross-checking integration with the parent Ktor app.

## 1) Scope and Context

- Directory purpose: build and maintain Tailwind v4 styles consumed by the Ktor application.
- Primary source file: `style.css`.
- Build integration: repo-local Amper plugin (`module.yaml`, `plugin.yaml`, `src/GenerateTailwind.kt`).
- Generated output contract: plugin contributes JVM resource `static/style.css` during Amper tasks.

## 2) Non-Negotiable Command Policy

- For build/test/package tasks in the parent project, use `./amper`.
- Do not use `gradle`, `./gradlew`, or `gradlew` in this repository.
- Run parent-project commands from `/Users/i13az81/dev/uni/noah-ruben.de`.

## 3) Build, Lint, and Test Commands

Run parent-project commands from `/Users/i13az81/dev/uni/noah-ruben.de`.

### Tailwind build commands

- Build application/resources (includes Tailwind generation):
  - `./amper build`
- Run tests (includes resources needed by test tasks):
  - `./amper test`
- Build deployable executable JAR:
  - `./amper package -f executable-jar`

### Watch mode for quick iteration

- Optional direct Tailwind watch from `tailwind/` (debugging only, outside Amper task graph):
  - `npx @tailwindcss/cli -i style.css -o /tmp/style.css --watch`

### Formatting

- Respect repository `.editorconfig` defaults:
  - UTF-8.
  - Final newline.
  - Trailing whitespace trimmed.
  - 4 spaces default indent.
  - 2 spaces for `*.md`, `*.json`, `*.yml`, `*.yaml`.
- Keep CSS blocks consistently indented.
- Avoid unnecessary whitespace-only churn.

### Imports and directives

- Keep Tailwind directives at the top:
  - `@import "tailwindcss";`
  - `@tailwind utilities;`
  - `@source ...` declarations.
  - `@plugin ...` declarations.
  - `@theme inline { ... }`.
- Add new `@source` paths only when required to include new Kotlin/HTML class usage.
- Keep plugin directives grouped and easy to scan.

### Naming conventions

- Use kebab-case for CSS class names (existing pattern).
- Prefer semantic names by UI role (`project-card-title`, `filter-controls-layout`) over visual-only names.
- Keep naming consistent with constants in Kotlin `CssClasses` mapping.
- Avoid one-off class names that do not map to reusable components.

### Types and structure (CSS-specific)

- Use design tokens/theme variables where possible instead of hard-coded repeated literals.
- Group reusable component styles in `@layer components`.
- Keep `@apply` chains readable and coherent (layout -> spacing -> typography -> color -> state).
- Avoid duplicate classes; merge or reuse existing component utilities when possible.

### Error handling and reliability

- Keep plugin task behavior fail-fast on missing files or failed subprocesses.
- Use explicit checks and clear error messages in plugin code.
- Do not silently ignore `npm` or Tailwind CLI failures.

### CSS-defining Kotlin files and Amper cache

The `generateTailwind` task declares the CSS-defining Kotlin files as `@Input` parameters so Amper re-runs Tailwind whenever a class constant changes. The relevant files are enumerated manually in `GenerateTailwind.kt` and `plugin.yaml`.

**When adding a new CSS class constants file** (e.g. `src/main/kotlin/de/noah_ruben/misc/styles/NewPageClasses.kt`):

1. Add a new `@Input newPageClasses: Path` parameter to `generateTailwind` in `tailwind/src/GenerateTailwind.kt`.
2. Add the matching binding in `tailwind/plugin.yaml`:
   ```yaml
   newPageClasses: ${module.rootDir}/src/main/kotlin/de/noah_ruben/misc/styles/NewPageClasses.kt
   ```
3. Run `./amper build` to verify the new wiring compiles and Tailwind regenerates.

Skipping steps 1–2 means class changes in the new file will silently miss Amper's cache and not appear in the Docker-built CSS until `style.css` is also touched.

### Compatibility and safety

- Tailwind v4 config lives in CSS; do not reintroduce deprecated v3 config patterns unless requested.
- Preserve Catppuccin compatibility workaround unless intentionally replacing it.
- Do not commit generated CSS artifacts under `src/main/resources/static/`.

## 5) Testing and Verification Expectations

- For CSS-only changes, run `./amper build` to verify plugin generation and resource wiring.
- For style changes impacting app pages, run relevant app tests from parent repo.
- Prefer targeted test execution first; run broader `./amper test` or `./amper build` before handoff for larger changes.
- Mention what was verified and what was not verified if constraints exist.

## 6) Environment and Dependencies

- Node dependency source: `package.json` in this directory.
- Required packages currently include:
  - `@tailwindcss/cli`
  - `tailwindcss`
  - `@catppuccin/tailwindcss`
- Wiremock helper exists at parent: `../wm/wm.sh`.

## 8) Agent Checklist

Before claiming completion:

- Ran `./amper build` (or relevant Amper command) to exercise Tailwind plugin generation.
- App sources actually reference verified changed classes (`@source` coverage).
- Ran targeted parent tests if behavior could affect rendering/routing.
- Avoided unrelated formatting or refactoring changes.
