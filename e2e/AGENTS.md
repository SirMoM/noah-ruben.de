# AGENTS Guide for `e2e/`

This folder contains agent-oriented browser verification.
The goal is not pixel-perfect regression testing.
The goal is to let agents see, exercise, and judge the important behavior and feel of the site.

## Intent

- Use these tests to verify real browser behavior.
- Prefer behavior-based assertions over markup, class-name, or implementation-detail checks.
- Always leave behind useful artifacts.
  Screenshots, traces, console logs, and network logs matter as much as pass/fail.
- Target a provided `BASE_URL`.
  The browser suite should not assume how the app was started.

## What Matters By Page

### Landing Page

- The page should feel like a personal terminal.
- The prompt-style presentation should read clearly and feel intentional, not like a generic portfolio page.
- Navigation to the important destinations should feel direct and lightweight.
- Theme switching should preserve the same terminal-like character in both light and dark modes.

### Projects Page

- Filtering should feel responsive and lightweight.
- HTMX updates should feel like focused content replacement, not like a jarring page refresh.
- The page should make browsing projects feel fast and interactive.
- Theme changes should not disrupt the flow of filtering and browsing.

### CV Page

- Switching between PDF variants should feel buttery smooth.
- Language and theme changes should not visibly flicker or leave broken intermediate states behind.
- Loading indicators and error states should feel controlled and intentional.
- PDF text should be selectable.
  Agents should be able to inspect and verify that the text layer is real, not just an image-like rendering.

## Authoring Rules

- Keep tests coarse and behavior-first.
- Do not assert generated HTML strings, CSS utility classes, or inline script text.
- Add screenshots at meaningful states so agents can review visual feel from the artifacts.
- When behavior has a subjective quality like “smooth” or “terminal-like,” combine a small assertion with a captured artifact rather than trying to over-formalize it.
- Keep the `e2e/` suite in TypeScript.
- Prefer small typed helpers over repeated inline page logic when assertions are reused across specs.

## TypeScript

- Use `.ts` for Playwright config, support helpers, and specs in this folder.
- Verify the suite shape with `npx tsc --noEmit -p tsconfig.json` when changing `e2e/` code.
- Do not reintroduce `jsconfig.json` or mixed JS/TS files unless the user explicitly asks for that tradeoff.

## Running

- Install once with `npm --prefix e2e install`.
- Run against a live target with `BASE_URL="http://127.0.0.1:42081" npm --prefix e2e run ui:smoke`.
- `ui:smoke` runs the regular `chromium` smoke suite only.
- Use `npm --prefix e2e run ui:cv:video` when an agent needs motion artifacts for CV feel checks.
  This project records video for the CV specs so smoothness and flicker can be judged directly.
