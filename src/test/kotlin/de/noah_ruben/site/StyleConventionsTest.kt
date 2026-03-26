package de.noah_ruben.site

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
    fun tailwindSourcesCoverAllKotlinStyles() {
        val cssConfig = Files.readString(Path.of("tailwind/style.css"))

        assertTrue(cssConfig.contains("@source \"../src/main/kotlin/**/*.kt\""))
    }
}
