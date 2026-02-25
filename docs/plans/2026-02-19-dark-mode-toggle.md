# Dark Mode Toggle Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Add a site-wide dark mode toggle that appears on all rendered pages, persists user preference, and defaults to system theme when no preference is saved.

**Architecture:** Implement theme control in shared rendering points so one change affects all pages. Use a static JS controller to set the `dark` class on `document.documentElement` and persist selection in `localStorage`. Keep visual behavior in Tailwind classes and shared CSS class constants.

**Tech Stack:** Kotlin 2.3, Ktor HTML DSL (`kotlinx.html`), Tailwind v4, HTMX, browser `localStorage` and `matchMedia` APIs.

---

### Task 1: Add failing tests for theme artifacts in shared pages

**Files:**
- Modify: `src/test/kotlin/de/noah_ruben/site/LandingPageTest.kt`
- Modify: `src/test/kotlin/de/noah_ruben/site/ProjectPageRoutingAndRenderingTest.kt`

**Step 1: Write the failing tests**

```kotlin
@Test
fun landingPageIncludesThemeToggleAndScript() = testApplicationWithRepositoryFake {
    val response = client.get("/")
    Assert.assertEquals(HttpStatusCode.OK, response.status)
    val body = response.bodyAsText()

    Assert.assertTrue(body.contains("id=\"theme-toggle\""))
    Assert.assertTrue(body.contains("/resources/theme-toggle.js"))
}
```

```kotlin
@Test
fun projectsPageIncludesThemeToggleAndScript() = testApplicationWithRepositoryFake {
    val response = client.get("/projects")
    Assert.assertEquals(HttpStatusCode.OK, response.status)
    val body = response.bodyAsText()

    Assert.assertTrue(body.contains("id=\"theme-toggle\""))
    Assert.assertTrue(body.contains("/resources/theme-toggle.js"))
}
```

**Step 2: Run tests to verify they fail**

Run: `./amper test --include-test="de.noah_ruben.site.LandingPageTest.landingPageIncludesThemeToggleAndScript"`
Expected: FAIL because toggle markup/script is not rendered yet.

Run: `./amper test --include-test="de.noah_ruben.site.ProjectPageRoutingAndRenderingTest.projectsPageIncludesThemeToggleAndScript"`
Expected: FAIL because toggle markup/script is not rendered yet.

**Step 3: Commit test-first changes**

```bash
git add src/test/kotlin/de/noah_ruben/site/LandingPageTest.kt src/test/kotlin/de/noah_ruben/site/ProjectPageRoutingAndRenderingTest.kt
git commit -m "test: assert shared pages render dark mode toggle hooks"
```

### Task 2: Render toggle UI in shared HTML layout

**Files:**
- Modify: `src/main/kotlin/de/noah_ruben/site/HtmlBase.kt`
- Modify: `src/main/kotlin/de/noah_ruben/site/LandingPage.kt`
- Modify: `src/main/kotlin/de/noah_ruben/site/projects/ProjectsPageRendering.kt`
- Modify: `src/main/kotlin/de/noah_ruben/site/CommandLineEmulation.kt`
- Modify: `src/main/kotlin/de/noah_ruben/misc/CssClasses.kt`

**Step 1: Add a failing render assertion for accessible attributes**

Add assertions in one page test for:

```kotlin
Assert.assertTrue(body.contains("aria-pressed=\"false\""))
Assert.assertTrue(body.contains("aria-label=\"Toggle dark mode\""))
```

**Step 2: Run test to verify it fails**

Run: `./amper test --include-test="de.noah_ruben.site.LandingPageTest.landingPageIncludesThemeToggleAndScript"`
Expected: FAIL because attributes/button do not exist.

**Step 3: Write minimal shared implementation**

Implement shared helper(s) and call them from shared page bodies:

```kotlin
private const val THEME_TOGGLE_ID = "theme-toggle"

fun FlowContent.themeToggleButton() {
    button(classes = CssClasses.Form.TOGGLE_BUTTON) {
        id = THEME_TOGGLE_ID
        attributes["type"] = "button"
        attributes["aria-label"] = "Toggle dark mode"
        attributes["aria-pressed"] = "false"
        span(classes = CssClasses.Form.TOGGLE_BUTTON_ICON) { +"🌙" }
    }
}
```

Make sure this helper is used in all full-page entry render paths (`landingPageHtml`, `projectsPage`, and command-driven full HTML responses).

**Step 4: Run focused tests to verify pass**

Run: `./amper test --include-classes="de.noah_ruben.site.LandingPageTest"`
Expected: PASS.

Run: `./amper test --include-classes="de.noah_ruben.site.ProjectPageRoutingAndRenderingTest"`
Expected: PASS.

**Step 5: Commit**

```bash
git add src/main/kotlin/de/noah_ruben/site/HtmlBase.kt src/main/kotlin/de/noah_ruben/site/LandingPage.kt src/main/kotlin/de/noah_ruben/site/projects/ProjectsPageRendering.kt src/main/kotlin/de/noah_ruben/site/CommandLineEmulation.kt src/main/kotlin/de/noah_ruben/misc/CssClasses.kt src/test/kotlin/de/noah_ruben/site/LandingPageTest.kt src/test/kotlin/de/noah_ruben/site/ProjectPageRoutingAndRenderingTest.kt
git commit -m "feat: render shared dark mode toggle button"
```

### Task 3: Add client-side theme controller and script wiring

**Files:**
- Create: `src/main/resources/static/theme-toggle.js`
- Modify: `src/main/kotlin/de/noah_ruben/site/HtmlBase.kt`
- Modify: `src/test/kotlin/de/noah_ruben/ApplicationTest.kt`

**Step 1: Write failing static resource test**

```kotlin
@Test
fun testThemeToggleScriptResource() = testApplicationWithRepositoryFake {
    client.get("/resources/theme-toggle.js").apply {
        assertEquals(HttpStatusCode.OK, status)
    }
}
```

**Step 2: Run test to verify it fails**

Run: `./amper test --include-test="de.noah_ruben.ApplicationTest.testThemeToggleScriptResource"`
Expected: FAIL (resource missing).

**Step 3: Implement minimal script + shared script include**

Create script with safe initialization and persistence:

```javascript
(function () {
  const KEY = "theme";
  const root = document.documentElement;

  function preferredTheme() {
    try {
      const stored = localStorage.getItem(KEY);
      if (stored === "dark" || stored === "light") return stored;
    } catch (_) {}
    return window.matchMedia("(prefers-color-scheme: dark)").matches ? "dark" : "light";
  }

  function applyTheme(theme) {
    root.classList.toggle("dark", theme === "dark");
  }

  function persistTheme(theme) {
    try { localStorage.setItem(KEY, theme); } catch (_) {}
  }

  function updateToggle(theme) {
    const btn = document.getElementById("theme-toggle");
    if (!btn) return;
    btn.setAttribute("aria-pressed", String(theme === "dark"));
  }

  const initial = preferredTheme();
  applyTheme(initial);

  window.addEventListener("DOMContentLoaded", function () {
    updateToggle(initial);
    const btn = document.getElementById("theme-toggle");
    if (!btn) return;
    btn.addEventListener("click", function () {
      const next = root.classList.contains("dark") ? "light" : "dark";
      applyTheme(next);
      persistTheme(next);
      updateToggle(next);
    });
  });
})();
```

Wire script in shared header:

```kotlin
script(src = "/resources/theme-toggle.js") {}
```

**Step 4: Run focused tests to verify pass**

Run: `./amper test --include-test="de.noah_ruben.ApplicationTest.testThemeToggleScriptResource"`
Expected: PASS.

Run: `./amper test --include-classes="de.noah_ruben.site.*"`
Expected: PASS.

**Step 5: Commit**

```bash
git add src/main/resources/static/theme-toggle.js src/main/kotlin/de/noah_ruben/site/HtmlBase.kt src/test/kotlin/de/noah_ruben/ApplicationTest.kt
git commit -m "feat: add persistent client-side theme controller"
```

### Task 4: Add Tailwind styles for toggle and stable light/dark defaults

**Files:**
- Modify: `tailwind/style.css`
- Modify: `src/test/kotlin/de/noah_ruben/ApplicationTest.kt`

**Step 1: Add failing test for CSS class hooks**

Extend `testStaticResources` (or add dedicated test) to assert:

```kotlin
val css = client.get("/resources/style.css").bodyAsText()
assertTrue(css.contains("toggle-button"))
assertTrue(css.contains("toggle-button-icon"))
```

**Step 2: Run test to verify it fails**

Run: `./amper test --include-test="de.noah_ruben.ApplicationTest.testStaticResources"`
Expected: FAIL because classes are not defined yet.

**Step 3: Implement minimal styles**

Add component classes:

```css
.toggle-button {
  @apply fixed top-4 right-4 inline-flex items-center gap-2 rounded border border-surface1 bg-surface0 px-3 py-2 text-sm text-text hover:bg-surface1;
}

.toggle-button-icon {
  @apply inline-flex w-5 justify-center;
}
```

Also make base theme neutral for both modes (avoid dark-only defaults on `html`).

**Step 4: Run focused verification**

Run: `./amper build`
Expected: PASS with Tailwind generation and resource wiring.

Run: `./amper test --include-classes="de.noah_ruben.ApplicationTest"`
Expected: PASS.

**Step 5: Commit**

```bash
git add tailwind/style.css src/test/kotlin/de/noah_ruben/ApplicationTest.kt
git commit -m "feat: style dark mode toggle and refine theme base defaults"
```

### Task 5: Final integration verification

**Files:**
- Modify: none expected

**Step 1: Run full automated verification**

Run: `./amper test`
Expected: PASS.

Run: `./amper build`
Expected: PASS.

**Step 2: Manual verification checklist**

Run: `./amper run`

Verify manually in browser:
- Toggle is visible on `/` and `/projects`.
- Toggling changes theme immediately.
- Reload preserves choice.
- With `localStorage` cleared, initial theme follows OS preference.
- HTMX interactions (search/filter, command input) do not break toggle behavior.

**Step 3: Final commit**

```bash
git add -A
git commit -m "feat: add site-wide dark mode toggle"
```
