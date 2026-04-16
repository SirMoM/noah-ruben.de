package de.noah_ruben.data.blog

import kotlin.io.path.createDirectories
import kotlin.io.path.createTempDirectory
import kotlin.io.path.createTempFile
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import java.time.Instant

class BlogConverterTest {

    @Test
    fun convertParsesFrontMatterAndMarkdown() {
        val sourceDir = createTempDirectory("blog-source-")
        val sourcePath = sourceDir.resolve("hello-world.md")
        sourcePath.writeText(
            """
            ---
            title: Hello World
            summary: A first post
            tags:
              - kotlin
              - ktor
            date: 2026-04-15T11:45:00Z
            ---

            First paragraph for excerpt.

            ## Second section

            More content follows here.
            """.trimIndent(),
        )

        val converted = BlogConverter().convert(sourcePath)

        assertEquals("hello-world", converted.slug)
        assertEquals("Hello World", converted.title)
        assertEquals("A first post", converted.summary)
        assertContentEquals(listOf("kotlin", "ktor"), converted.tags)
        assertEquals(Instant.parse("2026-04-15T11:45:00Z"), converted.publishedDate)
        assertEquals("First paragraph for excerpt.", converted.excerpt)
        assertTrue(converted.renderedHtml.contains("<p>First paragraph for excerpt.</p>"))
        assertTrue(converted.renderedHtml.contains("<h2>Second section</h2>"))
    }

    @Test
    fun convertFallsBackToExcerptWhenSummaryAndTagsAreMissing() {
        val sourceDir = createTempDirectory("blog-source-")
        val sourcePath = sourceDir.resolve("type-safe-builders-in-kotlin.md")
        sourcePath.writeText(
            """
            ---
            title: "Elegante DSLs für komplexe Objekte erstellen mit Type-Safe Builders in Kotlin"
            date: 2026-04-01T08:15:43+02:00
            draft: false
            author: Noah Ruben
            ---

            In der modernen Softwareentwicklung suchen Entwickler stets nach Wegen, Code lesbarer zu gestalten.

            ## Abschnitt

            Weitere Details folgen hier.
            """.trimIndent(),
        )

        val converted = BlogConverter().convert(sourcePath)

        assertEquals("type-safe-builders-in-kotlin", converted.slug)
        assertEquals("Elegante DSLs für komplexe Objekte erstellen mit Type-Safe Builders in Kotlin", converted.title)
        assertEquals(Instant.parse("2026-04-01T06:15:43Z"), converted.publishedDate)
        assertContentEquals(emptyList(), converted.tags)
        assertEquals(
            "In der modernen Softwareentwicklung suchen Entwickler stets nach Wegen, Code lesbarer zu gestalten.",
            converted.excerpt,
        )
        assertEquals(converted.excerpt, converted.summary)
    }

    @Test
    fun storageReportsMissingOrOlderHtmlAsStale() {
        val outputDir = createTempDirectory("blog-storage-")
        val missingHtmlPath = outputDir.resolve("missing").resolve("content.html")

        assertTrue(BlogStorage.isStale(sourceLastModified = 200L, htmlLastGenerated = 100L, htmlPath = missingHtmlPath))

        val existingHtmlPath = outputDir.resolve("fresh").resolve("content.html")
        existingHtmlPath.parent.createDirectories()
        existingHtmlPath.writeText("<p>Saved body</p>")

        assertTrue(BlogStorage.isStale(sourceLastModified = 200L, htmlLastGenerated = 100L, htmlPath = existingHtmlPath))
        assertFalse(BlogStorage.isStale(sourceLastModified = 100L, htmlLastGenerated = 200L, htmlPath = existingHtmlPath))
    }
}
