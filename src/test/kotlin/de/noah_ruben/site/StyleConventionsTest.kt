package de.noah_ruben.site

import java.io.File
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * This is intentionally a source-shape test.
 *
 * The site uses `kotlinx.html`, so raw class literals can slip into templates without changing
 * runtime behaviour while still bypassing the shared style constants we want to enforce.
 * Tailwind also discovers classes from the Kotlin source tree itself, so verifying the source
 * layout is the most direct way to protect both conventions.
 */
class StyleConventionsTest {

    @TestFactory
    fun siteTemplatesDoNotUseRawClassStringLiterals(): List<DynamicTest> {
        val rawPatterns = listOf("classes = \"", "classes = $\"", "classes = setOf(\"")
        val sourceRoot = File("src/main/kotlin")

        return sourceRoot
            .walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .map { file ->
                DynamicTest.dynamicTest(file.relativeTo(sourceRoot).path) {
                    val fileContent = file.readText()
                    rawPatterns.forEach { pattern ->
                        assertFalse(fileContent.contains(pattern), "${file.path} contains raw class pattern: $pattern")
                    }
                }
            }
            .toList()
    }

    @Test
    fun tailwindSourcesCoverAllKotlinStyles() {
        val cssConfig = File("tailwind/style.css").readText()

        assertTrue(cssConfig.contains("@source \"../src/main/kotlin/**/*.kt\""))
    }
}
