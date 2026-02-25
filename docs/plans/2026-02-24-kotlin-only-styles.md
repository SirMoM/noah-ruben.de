# Kotlin-Only Styles and Theme Cleanup Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Centralize all template styling in Kotlin constants across multiple files, remove debug borders, and ensure light/dark theming uses Catppuccin colors without introducing a new build step.

**Architecture:** Keep the existing Tailwind Amper flow and make a one-time `@source` expansion so Tailwind scans all Kotlin sources. Move raw class strings from route/rendering templates into split Kotlin style files under `misc/styles`, then re-export through `CssClasses` for ergonomic usage. Keep `tailwind/style.css` as stable shell/theme plumbing only, while day-to-day style edits happen in Kotlin constants.

**Tech Stack:** Kotlin 2.3, Ktor + kotlinx.html, Tailwind v4, Catppuccin Tailwind plugin, Amper.

---

### Task 1: Add failing guardrails for "no magic class strings" and debug-border removal

**Files:**
- Create: `src/test/kotlin/de/noah_ruben/site/StyleConventionsTest.kt`
- Modify: `src/test/kotlin/de/noah_ruben/site/ThemeToggleTest.kt`

**Step 1: Write the failing tests**

```kotlin
@Test
fun siteTemplatesDoNotUseRawClassStringLiterals() {
    val files = listOf(
        "src/main/kotlin/de/noah_ruben/site/LandingPage.kt",
        "src/main/kotlin/de/noah_ruben/site/HtmlBase.kt",
        "src/main/kotlin/de/noah_ruben/site/CommandLineEmulation.kt",
        "src/main/kotlin/de/noah_ruben/site/projects/ProjectsPageRendering.kt",
        "src/main/kotlin/de/noah_ruben/site/projects/ProjectPage.kt",
    )
    val rawPatterns = listOf("classes = \"", "classes = \$\"", "classes = setOf(\"")

    files.forEach { path ->
        val text = java.nio.file.Files.readString(java.nio.file.Path.of(path))
        rawPatterns.forEach { pattern ->
            kotlin.test.assertFalse(text.contains(pattern), "$path contains raw class pattern: $pattern")
        }
    }
}

@Test
fun renderedPagesDoNotContainDebugBorderClasses() = testApplicationWithRepositoryFake {
    val routes = listOf("/", "/projects")
    val debugClasses = listOf("border-pink-500", "border-white-500")
    routes.forEach { route ->
        val html = client.get(route).bodyAsText()
        debugClasses.forEach { cls -> kotlin.test.assertFalse(html.contains(cls)) }
    }
}
```

**Step 2: Run tests to verify they fail**

Run: `./amper test --include-test="de.noah_ruben.site.StyleConventionsTest.siteTemplatesDoNotUseRawClassStringLiterals"`
Expected: FAIL because templates currently contain inline class strings.

Run: `./amper test --include-test="de.noah_ruben.site.StyleConventionsTest.renderedPagesDoNotContainDebugBorderClasses"`
Expected: FAIL because debug border classes are still present.

**Step 3: Commit failing-test scaffold once green path starts**

```bash
git add src/test/kotlin/de/noah_ruben/site/StyleConventionsTest.kt src/test/kotlin/de/noah_ruben/site/ThemeToggleTest.kt
git commit -m "test(styles): add guardrails for template class conventions"
```

### Task 2: Introduce split Kotlin style definition files and CssClasses exports

**Files:**
- Create: `src/main/kotlin/de/noah_ruben/misc/styles/ThemeClasses.kt`
- Create: `src/main/kotlin/de/noah_ruben/misc/styles/SharedClasses.kt`
- Create: `src/main/kotlin/de/noah_ruben/misc/styles/LandingClasses.kt`
- Create: `src/main/kotlin/de/noah_ruben/misc/styles/ProjectsClasses.kt`
- Modify: `src/main/kotlin/de/noah_ruben/misc/CssClasses.kt`

**Step 1: Write failing compile expectation (test already fails from Task 1)**

No new test required; Task 1 failing convention test is the RED state for this refactor.

**Step 2: Run tests to keep RED state stable**

Run: `./amper test --include-test="de.noah_ruben.site.StyleConventionsTest.siteTemplatesDoNotUseRawClassStringLiterals"`
Expected: FAIL (still red before implementation).

**Step 3: Write minimal split constants**

Example structure:

```kotlin
// ThemeClasses.kt
object ThemeClasses {
    const val PAGE_ROOT = "flex flex-col p-6 bg-base text-text"
    const val PAGE_ROOT_LIGHT = "light:bg-base light:text-text" // or explicit light token mapping strategy
}

// SharedClasses.kt
object SharedClasses {
    const val TOGGLE_BUTTON = "fixed right-4 top-4 ... bg-surface0 text-text dark:bg-surface0"
    const val TOGGLE_BUTTON_ICON_BASE = "inline-flex h-8 w-8 ... text-overlay0"
    const val TOGGLE_BUTTON_ICON_MOON_ACTIVE = "data-[theme=dark]:text-blue ..."
    const val TOGGLE_BUTTON_ICON_SUN_ACTIVE = "data-[theme=light]:text-yellow ..."
}
```

Then re-export in `CssClasses.kt` as constants used by templates.

**Step 4: Run focused tests**

Run: `./amper test --include-classes="de.noah_ruben.site.StyleConventionsTest"`
Expected: Still FAIL until template call sites are migrated in Task 3.

**Step 5: Commit split style constant foundation**

```bash
git add src/main/kotlin/de/noah_ruben/misc/styles/*.kt src/main/kotlin/de/noah_ruben/misc/CssClasses.kt
git commit -m "refactor(styles): split Kotlin class definitions by domain"
```

### Task 3: Migrate templates to constants only and remove debug classes

**Files:**
- Modify: `src/main/kotlin/de/noah_ruben/site/LandingPage.kt`
- Modify: `src/main/kotlin/de/noah_ruben/site/HtmlBase.kt`
- Modify: `src/main/kotlin/de/noah_ruben/site/CommandLineEmulation.kt`
- Modify: `src/main/kotlin/de/noah_ruben/site/projects/ProjectsPageRendering.kt`
- Modify: `src/main/kotlin/de/noah_ruben/site/projects/ProjectPage.kt`

**Step 1: Keep failing convention test as RED**

Run: `./amper test --include-test="de.noah_ruben.site.StyleConventionsTest.siteTemplatesDoNotUseRawClassStringLiterals"`
Expected: FAIL before migration.

**Step 2: Replace raw class literals with Kotlin constants**

Examples:

```kotlin
// Before
div(classes = "pl-6") { ... }

// After
div(classes = CssClasses.Layout.HELP_BLOCK) { ... }
```

```kotlin
// Before
div(classes = "$PROFILE_CONTAINER border-white-500 border-2")

// After
div(classes = PROFILE_CONTAINER)
```

Also remove all debug-only border assignments from body/container sections.

**Step 3: Run focused tests to verify pass**

Run: `./amper test --include-classes="de.noah_ruben.site.StyleConventionsTest"`
Expected: PASS.

Run: `./amper test --include-classes="de.noah_ruben.site.LandingPageTest"`
Expected: PASS.

Run: `./amper test --include-classes="de.noah_ruben.site.ProjectPageRoutingAndRenderingTest"`
Expected: PASS.

**Step 4: Commit template migration**

```bash
git add src/main/kotlin/de/noah_ruben/site/LandingPage.kt src/main/kotlin/de/noah_ruben/site/HtmlBase.kt src/main/kotlin/de/noah_ruben/site/CommandLineEmulation.kt src/main/kotlin/de/noah_ruben/site/projects/ProjectsPageRendering.kt src/main/kotlin/de/noah_ruben/site/projects/ProjectPage.kt src/test/kotlin/de/noah_ruben/site/StyleConventionsTest.kt
git commit -m "refactor(styles): remove inline template class strings and debug borders"
```

### Task 4: One-time Tailwind source coverage update and Catppuccin-only color cleanup

**Files:**
- Modify: `tailwind/style.css`
- Modify: `src/main/kotlin/de/noah_ruben/misc/styles/*.kt`

**Step 1: Add failing test for source coverage assumptions**

Extend `StyleConventionsTest` with a check that `tailwind/style.css` includes broad Kotlin source coverage:

```kotlin
@Test
fun tailwindSourcesCoverAllKotlinStyles() {
    val cssConfig = Files.readString(Path.of("tailwind/style.css"))
    assertTrue(cssConfig.contains("@source \"../src/main/kotlin/**/*.kt\""))
}
```

**Step 2: Run test to verify it fails**

Run: `./amper test --include-test="de.noah_ruben.site.StyleConventionsTest.tailwindSourcesCoverAllKotlinStyles"`
Expected: FAIL with current narrow source list.

**Step 3: Implement minimal config + token cleanup**

- Update `@source` to include all Kotlin files once.
- Replace remaining non-Catppuccin utility colors in Kotlin constants (`slate-*`, `gray-*`, `red-*`, `yellow-*`, etc.) with Catppuccin tokens (`base`, `text`, `surface*`, `overlay*`, `blue`, `yellow`, etc.).

**Step 4: Run verification**

Run: `./amper test --include-classes="de.noah_ruben.site.ThemeToggleTest"`
Expected: PASS.

Run: `./amper build`
Expected: PASS with Tailwind generation from updated sources.

**Step 5: Commit**

```bash
git add tailwind/style.css src/main/kotlin/de/noah_ruben/misc/styles/*.kt src/test/kotlin/de/noah_ruben/site/StyleConventionsTest.kt
git commit -m "build(styles): broaden Tailwind Kotlin source scanning and enforce Catppuccin tokens"
```

### Task 5: Final regression and cleanup pass

**Files:**
- Modify: `src/test/kotlin/de/noah_ruben/ApplicationTest.kt` (only if needed for moved assertions)
- Modify: `src/test/kotlin/de/noah_ruben/site/ThemeToggleTest.kt` (only if needed)

**Step 1: Run full targeted suite**

Run: `./amper test --include-classes="de.noah_ruben.ApplicationTest"`
Expected: PASS.

Run: `./amper test --include-classes="de.noah_ruben.site.ThemeToggleTest"`
Expected: PASS.

Run: `./amper test --include-classes="de.noah_ruben.site.LandingPageTest"`
Expected: PASS.

Run: `./amper test --include-classes="de.noah_ruben.site.ProjectPageRoutingAndRenderingTest"`
Expected: PASS.

**Step 2: Run full project verification**

Run: `./amper test`
Expected: PASS.

Run: `./amper build`
Expected: PASS.

**Step 3: Final commit**

```bash
git add -A
git commit -m "test(styles): finalize Kotlin-only class usage and theme regressions"
```
