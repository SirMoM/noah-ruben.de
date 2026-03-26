# noah-ruben.de

Personal portfolio site built with Kotlin, Ktor, Tailwind v4, and HTMX.

## Tech stack

- **Language:** Kotlin 2.3 (JVM 21)
- **Framework:** Ktor with `kotlinx.html` for server-side HTML rendering
- **Styling:** Tailwind v4 with Catppuccin theme (`latte` light / `mocha` dark)
- **Build:** Amper with a repo-local Tailwind plugin and `runLocal` plugin
- **Mock:** Wiremock for local GitHub API stubs

## Development

### 1. Start the Wiremock server

```bash
./wm/wm.sh
```

Or use an IDE run configuration.

### 2. Run the application locally

```bash
./amper task :noah-ruben.de:runLocal@run
```

This sets `GITHUB_URL` and `GITHUB_TOKEN` automatically from the `.env` file via the `runLocal` plugin.

Fallback (manual env vars):

```bash
GITHUB_TOKEN="NOT_NEEDED" GITHUB_URL="http://localhost:42069" ./amper run
```

The app listens on `http://localhost:42081`.

### 3. Build

```bash
./amper build
```

Tailwind CSS generation runs automatically as part of the build via the local `tailwind` Amper plugin. No separate CSS build step is required.

### 4. Test

Run all tests:

```bash
./amper test
```

Browser smoke verification:

```bash
npm --prefix e2e install
BASE_URL="http://127.0.0.1:42081" npm --prefix e2e run ui:smoke
```

The Playwright smoke suite assumes the target app is already running.
It does not start or stop the runtime for you.
Artifacts for agent review are written to `e2e/test-results/` and `e2e/playwright-report/`.
The CV smoke checks require a runtime with readable CV assets.

Run a single test class (requires `--include-module`):

```bash
./amper test --include-module="noah-ruben.de" --include-classes="de.noah_ruben.site.LandingPageTest"
```

Run a single test method:

```bash
./amper test --include-module="noah-ruben.de" --include-test="de.noah_ruben.site.LandingPageTest.landingPage"
```

Pattern matching:

```bash
./amper test --include-module="noah-ruben.de" --include-classes="de.noah_ruben.site.*"
```

### 5. Build the deployable artifact

```bash
./amper package -f executable-jar
```

## Theming

The site uses [Catppuccin](https://github.com/catppuccin/tailwindcss) via `@catppuccin/tailwindcss` v1.0.0:

- **Light mode:** Latte (`.latte` class on `<html>`)
- **Dark mode:** Mocha (`.mocha` class on `<html>`)

The JS theme controller in `HtmlBase.kt` reads `localStorage.theme` (`"latte"` or `"mocha"`), falls back to `prefers-color-scheme`, and toggles the class on `document.documentElement`. All color utilities use the `ctp-` prefix (e.g. `bg-ctp-base`, `text-ctp-text`).

## Tilt (Docker orchestration)

Run from `docker/`:

```bash
tilt up
```

Resources:

- `website-image-build` — builds the Docker image automatically when sources change.
- `website` — starts the container. Requires one manual trigger on first start (`tilt trigger website`), then reloads automatically whenever `website-image-build` completes.
- `wiremock-website` — starts Wiremock; starts and reloads automatically.
- `wiremock-reload` — restarts Wiremock when `wm/` mappings change.

Quick health check (after `website` is running):

```bash
curl -sf http://localhost:42081/health
```

Stop everything:

```bash
tilt down
```

## Project structure

```
src/main/kotlin/de/noah_ruben/
  Application.kt              app entry and module wiring
  config/                     HTTP, monitoring, exception setup
  data/                       cache, GitHub/Wiremock clients, models
  site/                       routes and HTML generation
  misc/                       shared helpers, CSS class constants
    styles/                   domain-split Kotlin style constants
      SharedClasses.kt        toggle, form, layout utilities
      ProjectsClasses.kt      project card and filter styles
      ThemeClasses.kt         page layout tokens
      LandingClasses.kt       landing page tokens

src/main/resources/
  application.yaml            runtime config
  static/                     served static assets (icons, fonts)

tailwind/
  style.css                   Tailwind v4 entry: imports catppuccin mocha.css
  package.json                npm deps (@catppuccin/tailwindcss, tailwindcss CLI)

wm/                           Wiremock scripts and API mappings
docker/                       Tiltfile, Dockerfile, compose.yaml
```

## Environment variables

| Variable | Purpose | Default in tests |
|---|---|---|
| `GITHUB_URL` | GitHub (or Wiremock stub) base URL | Set in `application-test.yaml` |
| `GITHUB_TOKEN` | GitHub API token | Not required for Wiremock |
