# AGENTS Guide for `tailwind/`

This file is for agentic coding assistants working in `/Users/i13az81/dev/uni/noah-ruben.de/tailwind`.

Use these instructions for CSS/Tailwind tasks and for cross-checking integration with the parent Ktor app.

## 1) Scope and Context

- Directory purpose: build and maintain Tailwind v4 styles consumed by the Ktor application.
- Primary source file: `style.css`.
- Primary helper script: `run.sh`.
- Output target (served by app): `../src/main/resources/static/style.css`.
- Secondary copy target (for already-built app runs): `../build/resources/main/static/`.

## 2) Non-Negotiable Command Policy

- For build/test/package tasks in the parent project, use `./amper`.
- Do not use `gradle`, `./gradlew`, or `gradlew` in this repository.
- Run parent-project commands from `/Users/i13az81/dev/uni/noah-ruben.de`.

## 3) Build, Lint, and Test Commands

Run the following from `tailwind/` unless otherwise stated.

### Tailwind build commands

- Install/update JS dependencies:
  - `npm install`
- Compile Tailwind CSS directly:
  - `npx @tailwindcss/cli -o ../src/main/resources/static/style.css -i style.css`
- Copy generated CSS into build resources:
  - `cp ../src/main/resources/static/style.css ../build/resources/main/static/`
- Run project script (compile + copy):
  - `./run.sh`

### Watch mode for quick iteration

- Recompile when CSS files change:
  - `find . -name "*.css" | entr bash ./run.sh`

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

- Shell scripts should fail loudly on missing tools or bad paths.
- Keep `run.sh` deterministic: compile first, then copy output.
- If adding script logic, prefer explicit checks and clear stderr messages.
- Do not silently ignore copy/build failures.

### Compatibility and safety

- Tailwind v4 config lives in CSS; do not reintroduce deprecated v3 config patterns unless requested.
- Preserve Catppuccin compatibility workaround unless intentionally replacing it.
- Validate generated CSS is written to `../src/main/resources/static/style.css`.

## 5) Testing and Verification Expectations

- For CSS-only changes, run `./run.sh` and verify output file updates.
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

- Ran `./run.sh` (or equivalent compile + copy commands).
- App sources actually reference verified changed classes (`@source` coverage).
- Ran targeted parent tests if behavior could affect rendering/routing.
- Avoided unrelated formatting or refactoring changes.
