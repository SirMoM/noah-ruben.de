---
status: done
created: 2026-02-25
completed: 2026-02-25
tickets: []
---

# Catppuccin Latte/Mocha Theme Switch Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Switch the site from using hardcoded Mocha hex values in `@theme inline` to the `@catppuccin/tailwindcss` plugin's proper flavour system — Frappe as the light theme, Mocha as the dark theme — with all color classes updated to the `ctp-` prefix.

**Architecture:** Remove the manual `@theme inline` overrides and use the plugin's CSS imports (`frappe.css` / `mocha.css`). The JS dark mode toggle is updated to add/remove `frappe`/`mocha` class names directly on `<html>` instead of toggling `dark`. All Kotlin style constants are updated to use `ctp-` prefixed color utilities.

**Tech Stack:** Tailwind v4, `@catppuccin/tailwindcss` npm package, Kotlin/kotlinx.html, Ktor

---

## Background

The current setup uses `@plugin "@catppuccin/tailwindcss"` but then immediately overrides all color tokens via `@theme inline {}` with hardcoded Mocha hex values. This means:
- Light mode background is Mocha's `rosewater` (pinkish/rose-gold) — not correct
- The plugin's flavour system (Frappe/Mocha) is never used
- All color classes use bare names (`bg-rosewater`) instead of `ctp-` prefixed ones

The target state:
- Light = Frappe, Dark = Mocha
- `<html>` carries `frappe` or `mocha` class (toggled by JS)
- All color utilities use `bg-ctp-base`, `text-ctp-text`, etc.

---

## Task 1: Update `tailwind/style.css`

**Files:**
- Modify: `tailwind/style.css`

**Step 1: Replace plugin directive and remove manual theme overrides**

Replace the entire file content with:

```css
@import "tailwindcss";

@tailwind utilities;

@source "../src/main/kotlin/**/*.kt";

@import "@catppuccin/tailwindcss/frappe.css";
@import "@catppuccin/tailwindcss/mocha.css";

@layer base {
    html {
        font-family: 'Cascadia Code', monospace;
        scroll-behavior: smooth;
        @apply min-h-screen bg-ctp-base text-ctp-text;
    }

    a {
        @apply text-ctp-red visited:text-ctp-maroon;
    }
}
```

Note: Remove the `@custom-variant dark` line and the `html.dark` rule — they are replaced by the plugin's built-in `.frappe` / `.mocha` flavour variants on `<html>`.

**Step 2: Verify build picks up the new CSS**

```bash
./amper build
```

Expected: Build succeeds, no errors about missing CSS imports. (The `@catppuccin/tailwindcss` package is already in `tailwind/package.json`.)

**Step 3: Commit**

```bash
git add tailwind/style.css
git commit -m "feat: switch catppuccin theme to frappe/mocha via plugin flavour imports"
```

---

## Task 2: Update `HtmlBase.kt` — JS dark mode toggle

The JS currently toggles `"dark"` on `<html>`. It must instead swap between `"frappe"` (light) and `"mocha"` (dark) class names. `localStorage.theme` will store `"frappe"` or `"mocha"`.

**Files:**
- Modify: `src/main/kotlin/de/noah_ruben/site/HtmlBase.kt`

**Step 1: Update the inline `<script>` block**

Replace the `script { unsafe { ... } }` block (lines 49–87) with:

```kotlin
script {
    unsafe {
        +"""
            (function () {
              function applyTheme() {
                var savedTheme = localStorage.theme;
                var prefersDark = !("theme" in localStorage) && window.matchMedia("(prefers-color-scheme: dark)").matches;
                var theme = savedTheme || (prefersDark ? "mocha" : "frappe");
                document.documentElement.classList.remove("frappe", "mocha");
                document.documentElement.classList.add(theme);

                var button = document.getElementById("theme-toggle");
                if (!button) return;
                var isDark = theme === "mocha";
                button.setAttribute("aria-pressed", String(isDark));
                button.setAttribute("data-theme", isDark ? "dark" : "light");
                button.querySelectorAll("span").forEach(function (icon) {
                  icon.setAttribute("data-theme", isDark ? "dark" : "light");
                });
              }

              applyTheme();
              document.addEventListener("DOMContentLoaded", applyTheme);

              document.addEventListener("click", function (event) {
                var target = event.target;
                if (!(target instanceof Element)) return;
                if (!target.closest("#theme-toggle")) return;

                var isDark = document.documentElement.classList.contains("mocha");
                localStorage.theme = isDark ? "frappe" : "mocha";

                applyTheme();
              });
            })();
        """.trimIndent()
    }
}
```

Key changes:
- `classList.toggle("dark", isDark)` → `classList.remove("frappe","mocha"); classList.add(theme)`
- `localStorage.theme` stores `"frappe"` or `"mocha"` instead of `"light"` / `"dark"`
- `data-theme` attribute on the button still uses `"dark"` / `"light"` so the existing `data-[theme=dark]:*` CSS selectors on toggle button icons still work

**Step 2: Update `ThemeToggleTest.kt`**

The test at `src/test/kotlin/de/noah_ruben/site/ThemeToggleTest.kt:25` asserts `classList.toggle("dark"` — update it to match the new JS:

Replace:
```kotlin
assertTrue(body.contains("classList.toggle(\"dark\""))
```
With:
```kotlin
assertTrue(body.contains("classList.remove(\"frappe\", \"mocha\")"))
assertTrue(body.contains("classList.add(theme)"))
```

Also update the CSS assertion in `themeToggleCssUsesClassStrategy`:

Replace:
```kotlin
assertTrue(css.contains("html.dark"))
```
With:
```kotlin
assertTrue(css.contains(".mocha"))
assertTrue(css.contains(".frappe"))
```

**Step 3: Run the theme toggle tests**

```bash
./amper test --include-classes="de.noah_ruben.site.ThemeToggleTest"
```

Expected: All tests pass.

**Step 4: Commit**

```bash
git add src/main/kotlin/de/noah_ruben/site/HtmlBase.kt \
        src/test/kotlin/de/noah_ruben/site/ThemeToggleTest.kt
git commit -m "feat: update dark mode JS toggle to use frappe/mocha flavour classes"
```

---

## Task 3: Update `SharedClasses.kt` — `ctp-` prefixes

All bare Catppuccin color tokens become `ctp-` prefixed. The `dark:` variants become `mocha:` variants (using the plugin's built-in flavour variant).

**Files:**
- Modify: `src/main/kotlin/de/noah_ruben/misc/styles/SharedClasses.kt`

**Step 1: Apply all renames**

Full updated file:

```kotlin
package de.noah_ruben.misc.styles

object SharedClasses {
    const val MB_8 = "mb-8"
    const val MB_4 = "mb-4"

    const val CLI_WRAPPER = "w-full inline-flex outline-none focus:outline-none border border-ctp-crust rounded"
    const val CLI_INPUT_FIELD = "pl-4 flex-grow bg-transparent border-none outline-none focus:ring-0"

    const val FORM_GROUP = "flex flex-col"
    const val FORM_FIELD = "flex flex-col mb-2"
    const val FORM_LABEL = "mr-1 align-middle text-ctp-subtext1"
    const val FORM_INPUT_BASE = "bg-ctp-base border border-ctp-overlay1 rounded p-2 text-ctp-text focus:outline-none focus:border-ctp-blue"
    const val FORM_INPUT_TEXT = "flex-grow"
    const val FORM_INPUT_TEXT_WITH_MARGIN = "$FORM_INPUT_BASE $FORM_INPUT_TEXT $MB_4"
    const val FORM_CHECKBOX_GROUP = "flex items-center mt-2"
    const val FORM_CHECKBOX = "bg-ctp-base text-ctp-blue rounded border border-ctp-overlay1 p-2 mt-4 text-xl align-middle mr-1 appearance-none checked:bg-ctp-blue checked:border-transparent focus:outline-none focus:ring-2 focus:ring-ctp-sapphire"
    const val SUBMIT_BUTTON = "rounded border border-ctp-overlay1 p-2 text-xl flex items-center justify-center bg-ctp-base text-ctp-text mt-4 hover:bg-ctp-surface0 focus:outline-none focus:ring-2 focus:ring-ctp-sapphire"
    const val LOADING_SPINNER = "flex items-center text-ctp-subtext0"
    const val FILTER_CONTROLS_LAYOUT = "flex flex-wrap gap-4 items-end mb-4"
    const val FILTER_ITEM_LAYOUT = "flex-col"

    const val TOGGLE_BUTTON = "fixed right-4 top-4 z-50 inline-flex items-center gap-1 rounded-full border border-ctp-overlay1 bg-ctp-base p-1 text-sm font-semibold text-ctp-text"
    const val TOGGLE_BUTTON_ICON = "inline-flex h-8 w-8 items-center justify-center rounded-full transition-all [&_svg]:h-5 [&_svg]:w-5"
    const val TOGGLE_BUTTON_ICON_MOON = "text-ctp-overlay0 opacity-70 data-[theme=dark]:bg-ctp-surface1 data-[theme=dark]:text-ctp-blue data-[theme=dark]:opacity-100"
    const val TOGGLE_BUTTON_ICON_SUN = "text-ctp-overlay0 opacity-70 data-[theme=light]:bg-ctp-surface1 data-[theme=light]:text-ctp-yellow data-[theme=light]:opacity-100"
    const val TOGGLE_BUTTON_ICON_MOON_FULL = "$TOGGLE_BUTTON_ICON $TOGGLE_BUTTON_ICON_MOON"
    const val TOGGLE_BUTTON_ICON_SUN_FULL = "$TOGGLE_BUTTON_ICON $TOGGLE_BUTTON_ICON_SUN"

    const val HTMX_INDICATOR = "htmx-indicator"
    const val HTMX_INDICATOR_INLINE = "htmx-indicator ml-2"
    const val HELP_INDENT = "pl-6"
    const val SUBPAGE_INDENT = "pl-12"
    const val ERROR_MESSAGE_BOX = "text-ctp-red font-bold p-4 border border-ctp-red rounded mb-4"
}
```

Note on `FORM_INPUT_BASE`: The light-mode `bg-rosewater` and dark-mode `dark:bg-surface0` collapse into a single `bg-ctp-base` — which resolves to Frappe's base in light mode and Mocha's base in dark mode. Same for text and border colors.

**Step 2: Run broader tests**

```bash
./amper test --include-classes="de.noah_ruben.site.*"
```

Expected: All pass.

**Step 3: Commit**

```bash
git add src/main/kotlin/de/noah_ruben/misc/styles/SharedClasses.kt
git commit -m "feat: rename catppuccin color utilities to ctp- prefix in SharedClasses"
```

---

## Task 4: Update `ProjectsClasses.kt` — `ctp-` prefixes

**Files:**
- Modify: `src/main/kotlin/de/noah_ruben/misc/styles/ProjectsClasses.kt`

**Step 1: Apply all renames**

Full updated file:

```kotlin
package de.noah_ruben.misc.styles

object ProjectsClasses {
    const val PROJECT_CARD = "border border-ctp-overlay1 rounded p-4 mb-4 max-w-none overflow-hidden shadow-lg bg-ctp-surface0 flex flex-col h-full text-ctp-text relative"
    const val PROJECT_CARD_CONTENT = "flex-grow"
    const val PROJECT_CARD_TITLE = "font-bold text-xl mb-2 text-ctp-blue"
    const val PROJECT_CARD_DESCRIPTION = "text-ctp-subtext0 text-base mb-4"
    const val PROJECT_CARD_META = "text-sm text-ctp-overlay2 mb-4"
    const val META_DETAIL_ROW = "flex items-center text-ctp-overlay2 text-sm mt-2"
    const val META_DETAIL_LABEL = "font-semibold text-ctp-subtext1 mr-1"
    const val TAGS_LIST = "flex flex-wrap gap-2 mb-4"
    const val TAG_ITEM = "rounded-full px-3 py-1 text-sm font-semibold inline-block cursor-pointer hover:opacity-80 transition-opacity duration-200"
    const val TOPIC_TAG = "inline-block cursor-pointer text-sm font-semibold transition-opacity duration-200 hover:opacity-80"
    const val TOPICS_LIST = "flex flex-wrap gap-2"
    const val PROJECT_CARD_FOOTER = "border-t border-ctp-overlay1 pt-4 flex justify-start gap-3"
    const val PROJECT_ACTION_LINK = "inline-block rounded-full px-3 py-1 text-sm font-semibold bg-ctp-blue text-ctp-base hover:bg-ctp-sapphire focus:outline-none focus:ring-2 focus:ring-ctp-sapphire transition-colors duration-200"
    const val RESET_BUTTON = "inline-block rounded-md px-4 py-2 text-sm font-semibold bg-gradient-to-r from-ctp-red to-ctp-peach text-ctp-base hover:from-ctp-peach hover:to-ctp-red focus:outline-none focus:ring-2 focus:ring-ctp-red transition-all duration-300 shadow-md hover:shadow-lg transform hover:-translate-y-1 cursor-pointer"
    const val STAR_ICON = "inline-flex items-center justify-center w-12 h-12 text-ctp-base font-extrabold bg-gradient-to-r from-ctp-mauve via-ctp-green to-ctp-blue absolute top-2 right-2 [clip-path:polygon(50%_0%,63%_36%,100%_36%,69%_57%,82%_100%,50%_74%,18%_100%,31%_57%,0%_36%,37%_36%)]"
}
```

Note: The `dark:border-surface1`, `dark:bg-surface0`, `dark:text-text`, `dark:text-subtext0`, `dark:text-overlay2` variants are collapsed — the `ctp-` classes already adapt per the active flavour class on `<html>`. `PROJECT_CARD` background moves to `bg-ctp-surface0` (slightly elevated surface in both flavours) rather than `bg-rosewater`.

**Step 2: Run project page tests**

```bash
./amper test --include-classes="de.noah_ruben.site.ProjectPageRoutingAndRenderingTest"
```

Expected: All pass.

**Step 3: Commit**

```bash
git add src/main/kotlin/de/noah_ruben/misc/styles/ProjectsClasses.kt
git commit -m "feat: rename catppuccin color utilities to ctp- prefix in ProjectsClasses"
```

---

## Task 5: Update `ThemeClasses.kt` and `LandingClasses.kt` — `ctp-` prefixes

**Files:**
- Modify: `src/main/kotlin/de/noah_ruben/misc/styles/ThemeClasses.kt`
- Modify: `src/main/kotlin/de/noah_ruben/misc/styles/LandingClasses.kt`

**Step 1: Update `ThemeClasses.kt`**

```kotlin
package de.noah_ruben.misc.styles

object ThemeClasses {
    const val PAGE_BASE = "flex flex-col p-6"
    const val PAGE_TITLE = "text-2xl font-bold pt-8"
    const val CONTENT_CONTAINER = "container mx-auto p-4"
    const val LINK = "text-ctp-red visited:text-ctp-maroon"
}
```

**Step 2: Update `LandingClasses.kt`**

```kotlin
package de.noah_ruben.misc.styles

object LandingClasses {
    const val PROFILE_CONTAINER = "flex items-center"
    const val PROFILE_PICTURE = "m-4 aspect-square rounded-full bg-ctp-yellow lg:w-1/6 w-1/3"
    const val PROFILE_DETAILS_CONTAINER = "lg:w-5/6 w-2/3 text-xl grow"
    const val COLOR_GRID = "inline-grid flex-none grid-cols-8 grid-rows-2 mt-4 [&>div]:h-10 [&>div]:w-10 [&>div]:border [&>div]:border-ctp-crust [&>div]:transition-all [&>div]:duration-150 [&>div:hover]:scale-110 [&>div:hover]:border-ctp-surface0"
    const val ABOUT_ME = "p-6 leading-relaxed"
}
```

**Step 3: Update `CssClasses.kt` — COLORS list**

In `src/main/kotlin/de/noah_ruben/misc/CssClasses.kt`, update the `COLORS` list (lines 31–36):

```kotlin
val COLORS = listOf(
    "bg-ctp-rosewater", "bg-ctp-red", "bg-ctp-green", "bg-ctp-mauve",
    "bg-ctp-lavender", "bg-ctp-blue", "bg-ctp-sky", "bg-ctp-teal",
    "bg-ctp-sapphire", "bg-ctp-green", "bg-ctp-peach", "bg-ctp-yellow",
    "bg-ctp-flamingo", "bg-ctp-maroon", "bg-ctp-overlay1", "bg-ctp-crust",
)
```

**Step 4: Run landing page tests**

```bash
./amper test --include-classes="de.noah_ruben.site.LandingPageTest"
```

Expected: All pass.

**Step 5: Commit**

```bash
git add src/main/kotlin/de/noah_ruben/misc/styles/ThemeClasses.kt \
        src/main/kotlin/de/noah_ruben/misc/styles/LandingClasses.kt \
        src/main/kotlin/de/noah_ruben/misc/CssClasses.kt
git commit -m "feat: rename catppuccin color utilities to ctp- prefix in ThemeClasses, LandingClasses, CssClasses"
```

---

## Task 6: Full test suite and build verification

**Step 1: Run all tests**

```bash
./amper test
```

Expected: All tests pass. If `ThemeToggleTest` or `StyleConventionsTest` fail, review the assertions — they may reference old class patterns and need updating.

**Step 2: Run full build**

```bash
./amper build
```

Expected: Build succeeds, Tailwind CSS generated without errors.

**Step 3: Smoke check locally (optional but recommended)**

```bash
./amper run
```

Open browser at `http://localhost:8080`. Verify:
- Default (no `localStorage.theme`): system dark preference → mocha, system light preference → frappe
- Toggle button switches between Mocha (dark) and Frappe (light) correctly
- Colors look like Catppuccin Frappe in light mode (soft grey/blue-ish, not rose-gold)
- Colors look like Catppuccin Mocha in dark mode

**Step 4: Final commit if any stragglers**

```bash
git add -A
git commit -m "chore: full catppuccin frappe/mocha theme migration complete"
```

---

## Reference: Color Token Mapping (bare → ctp-)

All bare Catppuccin names used in the codebase and their `ctp-` equivalent (same name, just prefixed):

| Before | After |
|---|---|
| `bg-rosewater` | `bg-ctp-rosewater` |
| `bg-base` | `bg-ctp-base` |
| `bg-surface0` | `bg-ctp-surface0` |
| `bg-surface1` | `bg-ctp-surface1` |
| `bg-blue` | `bg-ctp-blue` |
| `bg-flamingo` | `bg-ctp-flamingo` |
| `text-crust` | `text-ctp-crust` |
| `text-text` | `text-ctp-text` |
| `text-subtext0` | `text-ctp-subtext0` |
| `text-subtext1` | `text-ctp-subtext1` |
| `text-overlay0` | `text-ctp-overlay0` |
| `text-overlay1` | `text-ctp-overlay1` |
| `text-overlay2` | `text-ctp-overlay2` |
| `text-blue` | `text-ctp-blue` |
| `text-red` | `text-ctp-red` |
| `text-maroon` | `text-ctp-maroon` |
| `text-yellow` | `text-ctp-yellow` |
| `border-overlay1` | `border-ctp-overlay1` |
| `border-surface1` | `border-ctp-surface1` |
| `border-crust` | `border-ctp-crust` |
| `border-red` | `border-ctp-red` |
| `ring-sapphire` | `ring-ctp-sapphire` |
| `from-red` | `from-ctp-red` |
| `to-peach` | `to-ctp-peach` |
| `from-mauve` | `from-ctp-mauve` |
| `via-green` | `via-ctp-green` |
| `to-blue` | `to-ctp-blue` |

`dark:` variants are eliminated entirely — the `ctp-` classes already resolve per the active flavour class on `<html>`.
