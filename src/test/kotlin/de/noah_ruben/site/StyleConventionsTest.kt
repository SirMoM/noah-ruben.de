package de.noah_ruben.site

import de.noah_ruben.testApplicationWithRepositoryFake
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StyleConventionsTest {

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

        files.forEach { filePath ->
            val fileContent = Files.readString(Path.of(filePath))
            rawPatterns.forEach { pattern ->
                assertFalse(fileContent.contains(pattern), "$filePath contains raw class pattern: $pattern")
            }
        }
    }

    @Test
    fun renderedPagesDoNotContainDebugBorderClasses() = testApplicationWithRepositoryFake {
        val routes = listOf("/", "/projects")
        val debugClasses = listOf("border-pink-500", "border-white-500")

        routes.forEach { route ->
            val html = client.get(route).bodyAsText()
            debugClasses.forEach { debugClass ->
                assertFalse(html.contains(debugClass), "Found debug class '$debugClass' on route '$route'")
            }
        }
    }

    @Test
    fun tailwindSourcesCoverAllKotlinStyles() {
        val cssConfig = Files.readString(Path.of("tailwind/style.css"))

        assertTrue(cssConfig.contains("@source \"../src/main/kotlin/**/*.kt\""))
    }

    @Test
    fun landingPageBodyDoesNotHardcodeThemeColors() = testApplicationWithRepositoryFake {
        val html = client.get("/").bodyAsText()
        val bodyClassMatch = Regex("""id=\"body\"[^>]*class=\"([^\"]*)\"""").find(html)
        val bodyClasses = bodyClassMatch?.groupValues?.get(1) ?: ""

        assertFalse(bodyClasses.contains("bg-base"))
        assertFalse(bodyClasses.contains("text-text"))
    }
}
