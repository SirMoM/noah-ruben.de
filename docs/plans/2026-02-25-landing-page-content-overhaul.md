# Landing Page Content Overhaul Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Refresh the landing page content to balance professional portfolio signals with personal identity while preserving the CLI aesthetic.

**Architecture:** Refactor `indexPageContent()` into small section-focused render functions and update content in-place. Keep routing and page shell unchanged, add an accent-color switcher backed by `localStorage`, and remove CV exposure from help/navigation while keeping command handling safe.

**Tech Stack:** Kotlin 2.3, Ktor HTML DSL (`kotlinx.html`), Tailwind v4 classes via `CssClasses`, HTMX, JUnit/Ktor testApplication

---

### Task 1: Lock behavior with failing landing-page content tests

**Files:**
- Modify: `src/test/kotlin/de/noah_ruben/site/LandingPageTest.kt`

**Step 1: Write a failing test for refreshed profile details and links**

Add a new test to assert the rendered `/` HTML contains these strings/links:

```kotlin
@Test
fun landingPageRendersUpdatedProfileLinksAndRole() = testApplicationWithRepositoryFake {
    val response = client.get("/")
    Assert.assertEquals(HttpStatusCode.OK, response.status)

    val html = response.bodyAsText()
    Assert.assertTrue(html.contains("Full-Stack Developer @ ATLAS"))
    Assert.assertTrue(html.contains("https://github.com/SirMoM"))
    Assert.assertTrue(html.contains("https://www.linkedin.com/in/noah-ruben-3013991b7"))
    Assert.assertTrue(html.contains("mailto:"))
    Assert.assertFalse(html.contains("Twitter"))
    Assert.assertFalse(html.contains("TODO"))
}
```

**Step 2: Write a failing test for terminal-style skills and help links**

```kotlin
@Test
fun landingPageRendersBioSkillsAndHelpWithoutCv() = testApplicationWithRepositoryFake {
    val response = client.get("/")
    Assert.assertEquals(HttpStatusCode.OK, response.status)

    val html = response.bodyAsText()
    Assert.assertTrue(html.contains("System summary"))
    Assert.assertTrue(html.contains("Languages"))
    Assert.assertTrue(html.contains("Kotlin"))
    Assert.assertTrue(html.contains("Angular"))
    Assert.assertTrue(html.contains("HTMX"))
    Assert.assertFalse(html.contains("href=\"/cv\""))
}
```

**Step 3: Run targeted tests to verify failure**

Run: `./amper test --include-classes="de.noah_ruben.site.LandingPageTest"`
Expected: FAIL on new assertions (content not implemented yet).

**Step 4: Commit test scaffold**

```bash
git add src/test/kotlin/de/noah_ruben/site/LandingPageTest.kt
git commit -m "test: lock expected landing page overhaul content"
```

---

### Task 2: Refactor landing rendering into section functions and update content

**Files:**
- Modify: `src/main/kotlin/de/noah_ruben/site/LandingPage.kt`
- Modify: `src/main/kotlin/de/noah_ruben/misc/styles/LandingClasses.kt`
- Modify: `src/main/kotlin/de/noah_ruben/misc/CssClasses.kt`

**Step 1: Add/rename section helpers in `LandingPage.kt`**

Create focused render functions and wire them from `indexPageContent()`:

```kotlin
fun BODY.indexPageContent() {
    terminalPrompt()
    profileSection()
    systemSummarySection()
    skillsSection()
    helpSection()
    commandLineEmulation()
}
```

**Step 2: Implement profile section with updated fields**

Replace duplicate/stub fields with:
- Name
- Uptime
- Role (`Full-Stack Developer @ ATLAS`)
- GitHub link
- LinkedIn link
- Email link (`mailto:<placeholder-or-real-email>`)

Keep the Van Gogh placeholder image unchanged.

**Step 3: Implement system summary and skills sections**

Render concise bio lines plus terminal key-value skills:

```kotlin
div { span { +"Languages" }; +": Kotlin · Go · TypeScript · Java" }
div { span { +"Frameworks" }; +": Ktor · Angular · HTMX" }
div { span { +"Focus" }; +": Full-stack systems, game dev, open source" }
```

**Step 4: Add/propagate any new CSS class constants required**

If a separate class is needed for skills layout, add e.g. `SKILLS_SECTION` in `LandingClasses` and expose it via `CssClasses.LandingPage`.

**Step 5: Run targeted tests**

Run: `./amper test --include-classes="de.noah_ruben.site.LandingPageTest"`
Expected: PASS for content/structure assertions.

**Step 6: Commit refactor/content changes**

```bash
git add src/main/kotlin/de/noah_ruben/site/LandingPage.kt src/main/kotlin/de/noah_ruben/misc/styles/LandingClasses.kt src/main/kotlin/de/noah_ruben/misc/CssClasses.kt
git commit -m "feat: refactor landing page into section components"
```

---

### Task 3: Make the color swatch grid functional as an accent switcher

**Files:**
- Modify: `src/main/kotlin/de/noah_ruben/site/LandingPage.kt`
- Modify: `src/main/kotlin/de/noah_ruben/site/HtmlBase.kt`
- Test: `src/test/kotlin/de/noah_ruben/site/LandingPageTest.kt`

**Step 1: Add swatch metadata and click handling in landing section**

Render each tile with a deterministic mapping (`class` + hex) and attributes used by JS:

```kotlin
div(classes = colorClass) {
    attributes["data-accent"] = accentHex
    onClick = "window.setAccentColor && window.setAccentColor('$accentHex')"
}
```

**Step 2: Extend `defaultHeader()` script with accent persistence**

Add minimal JS helpers:

```javascript
const applyAccent = (hex) => document.documentElement.style.setProperty('--accent', hex)
window.setAccentColor = (hex) => { localStorage.setItem('accent', hex); applyAccent(hex) }
const savedAccent = localStorage.getItem('accent')
if (savedAccent) applyAccent(savedAccent)
```

Use existing script style/patterns in `HtmlBase.kt` (no extra framework).

**Step 3: Add failing-then-passing test assertions for accent hooks**

In landing page test(s), assert HTML contains:
- `data-accent`
- `setAccentColor`
- `localStorage.getItem('accent')`

**Step 4: Run targeted tests**

Run: `./amper test --include-classes="de.noah_ruben.site.LandingPageTest"`
Expected: PASS.

**Step 5: Commit accent switcher**

```bash
git add src/main/kotlin/de/noah_ruben/site/LandingPage.kt src/main/kotlin/de/noah_ruben/site/HtmlBase.kt src/test/kotlin/de/noah_ruben/site/LandingPageTest.kt
git commit -m "feat: add landing page accent color switcher"
```

---

### Task 4: Align CLI help output with new landing content

**Files:**
- Modify: `src/main/kotlin/de/noah_ruben/site/CommandLineEmulation.kt`
- Modify: `src/main/kotlin/de/noah_ruben/site/LandingPage.kt`
- Test: `src/test/kotlin/de/noah_ruben/site/CommandLineEmulationTest.kt` (new)

**Step 1: Update `cleUsage()` copy and links**

Replace placeholder lines:

```kotlin
p { +"It displays information about Noah Ruben: full-stack development, open source, and game dev interests." }
```

Remove `/cv` from visible help links.

**Step 2: Ensure `noahruben cv` is safe**

Replace `throw RuntimeException("NOT IMPLEMENTED!")` with a user-facing not-available response (same pattern as `unknownSubpage`, `HX-Retarget="#cle"`).

**Step 3: Add tests for `/command` help and `cv` behavior**

Create `CommandLineEmulationTest.kt` with:
- `POST /command` body `command=noahruben+help` returns usage without TODO and without `/cv` link
- `POST /command` body `command=noahruben+cv` returns a non-500 response and includes a friendly unavailable message

**Step 4: Run targeted tests**

Run: `./amper test --include-classes="de.noah_ruben.site.CommandLineEmulationTest"`
Expected: PASS.

**Step 5: Commit CLI/help changes**

```bash
git add src/main/kotlin/de/noah_ruben/site/CommandLineEmulation.kt src/main/kotlin/de/noah_ruben/site/LandingPage.kt src/test/kotlin/de/noah_ruben/site/CommandLineEmulationTest.kt
git commit -m "fix: align CLI help text with landing page overhaul"
```

---

### Task 5: Regression and integration verification

**Files:**
- Verify only (no file changes expected)

**Step 1: Run landing/site-focused tests**

Run: `./amper test --include-classes="de.noah_ruben.site.LandingPageTest" --include-classes="de.noah_ruben.site.ThemeToggleTest" --include-classes="de.noah_ruben.site.StyleConventionsTest" --include-classes="de.noah_ruben.site.CommandLineEmulationTest"`
Expected: PASS.

**Step 2: Run full test suite**

Run: `./amper test`
Expected: PASS.

**Step 3: Run build for integration confidence**

Run: `./amper build`
Expected: BUILD SUCCESSFUL.

**Step 4: Commit verification artifacts if needed**

Usually no commit required here; if follow-up test fixes were needed, commit with:

```bash
git add <modified-files>
git commit -m "test: stabilize landing page overhaul regressions"
```
