# AGENTS Guide for noah-ruben.de

This file is for agentic coding assistants working in this repository.
Follow these instructions unless a user message explicitly overrides them.

## 1) Non-Negotiable Command Policy

- Use `./amper` for all build, test, run, and package tasks.
- Do **not** use `gradle`, `./gradlew`, or `gradlew` in this repository.
- Keep commands repository-root relative unless a section says otherwise.

## 2) Tech and Runtime Snapshot

- Language: Kotlin 2.0.0.
- JVM: Java 21 toolchain.
- Framework: Ktor (server + testing).
- Build tool: Amper.
- HTML rendering: `kotlinx.html`.
- Styling: Tailwind v4 pipeline under `tailwind/`.

## 3) Project Structure

- `src/main/kotlin/de/noah_ruben`
  - `Application.kt`: app entry and module wiring.
  - `config/`: HTTP, monitoring, exception setup.
  - `data/`: cache, GitHub/Wiremock clients, models.
  - `site/`: routes and HTML generation.
  - `misc/`: shared helper utilities and CSS/HTMX helpers.
- `src/main/resources`
  - `application.yaml` runtime config.
  - `static/` served assets.
- `src/test/kotlin/de/noah_ruben`
  - mirrors source package layout.
  - route, rendering, and cache/data tests.
- `src/test/resources/application-test.yaml` test config.
- `tailwind/` CSS build sources/scripts.
- `wm/` Wiremock scripts and mappings.

## 4) Build / Lint / Test Commands

Run from `/Users/i13az81/dev/uni/noah-ruben.de`.

### Build and Run

- Run app locally:
  - `./amper run`
- Full build:
  - `./amper build`
- Build standalone jar task:
  - `./amper package -f executable-jar`

### Lint and Format

- Lint/format policy is IDE-first.
- Use IntelliJ/IDEA formatting and inspections for local enforcement.

### Test (all and targeted)

- Run all tests:
  - `./amper test`

### Run a Single Test (Important)

Amper uses include filters instead of Gradle `--tests`.

- One class:
  - `./amper test --include-classes="de.noah_ruben.site.LandingPageTest"`
- One test method:
  - `./amper test --include-test="de.noah_ruben.site.LandingPageTest.landingPage"`
- Another class example:
  - `./amper test --include-classes="de.noah_ruben.site.projects.ProjectsQueryTest"`
- Pattern-based matching:
  - `./amper test --include-classes="de.noah_ruben.site.*"`
  - `./amper test --include-classes="*ProjectsQueryTest"`

### Tailwind / Wiremock Helpers

- Tailwind script directly (run inside `tailwind/`):
  - `./run.sh`
- Start Wiremock stubs:
  - `./wm/wm.sh`

Note: run `./tailwind/run.sh` explicitly before `./amper build`, `./amper test`, and `./amper package` when CSS assets are relevant.

## 5) Configuration and Environment

- Main config: `src/main/resources/application.yaml`.
- Test config: `src/test/resources/application-test.yaml`.
- Environment-backed runtime keys:
  - `GITHUB_URL` -> `github.url`
  - `GITHUB_TOKEN` -> `github.token`
- Current app module defaults to `WiremockClient(url = getGithubURL())`.
- If you switch to `GitHubClient`, provide a valid token.

## 6) Kotlin Code Style Guidelines

Follow current code conventions first; avoid introducing a new style.

### Formatting

- Respect `.editorconfig`:
  - UTF-8, final newline, trimmed trailing whitespace.
  - Default indent: 4 spaces.
  - `*.md`, `*.json`, `*.yml`, `*.yaml`: 2 spaces.
  - line length is not hard-limited (`max_line_length = off`).
- Kotlin style is `official`.
- Ktlint profile is IntelliJ-based.

### Imports

- Prefer explicit imports.
- Avoid wildcard imports unless already established for that file.
- Repository exception: wildcard import from `kotlinx.html` is allowed.
- Remove unused imports before finishing.

### Types and Nullability

- Prefer `val` over `var`.
- Keep nullability explicit and minimize nullable surfaces.
- Avoid `!!` unless invariants are proven and documented by context.
- Add explicit types for public functions/properties and unclear inference cases.
- Keep function signatures small and readable.

### Naming

- Packages: lowercase under `de.noah_ruben.<area>`.
- Types (classes, objects, enums): `UpperCamelCase`.
- Functions/properties/locals: `lowerCamelCase`.
- Constants: `UPPER_SNAKE_CASE`.
- Test classes end with `Test` and mirror production package paths.

### Error Handling and Logging

- Route unexpected failures through centralized exception handling.
- Return meaningful HTTP status codes from route handlers.
- Use targeted catches for validation/parsing errors when possible.
- Never swallow exceptions silently.
- Log with context:
  - `info` for normal flow,
  - `warn` for bad input / recoverable issues,
  - `error` for failures with stack traces.

### Layering and Responsibilities

- Keep Ktor/plugin wiring in `config/`.
- Keep routing and response rendering in `site/`.
- Keep external API/cache concerns in `data/`.
- Keep shared utilities in `misc/`.
- Do not move logic across layers unless task requires refactoring.

## 7) Testing Guidance

- Prefer focused tests (one behavior per test).
- Use Ktor `testApplication` patterns already in repo.
- Reuse existing test fakes/helpers when available.
- Add regression tests for bug fixes.
- Avoid adding real network dependencies to tests.

## 8) Agent Work Checklist

Before claiming completion on Kotlin changes:

- run relevant targeted tests (`./amper test --include-test ...`),
- run broader tests when needed (`./amper test`),
- run `./amper build` for integration confidence,
- apply IDE formatting if formatting drift appears,
- verify no unrelated file changes are accidentally included.

## 9) Cursor and Copilot Rules

Checked rule locations requested by user:

- `.cursor/rules/`: not present.
- `.cursorrules`: not present.
- `.github/copilot-instructions.md`: not present.

If these files are added later, treat them as repository-level instructions and update this file.
