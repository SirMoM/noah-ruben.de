package de.noah_ruben.data.blog

import kotlin.io.path.absolutePathString
import kotlin.io.path.createTempDirectory
import kotlin.io.path.createTempFile
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import java.time.Instant

class BlogRepositoryTest {

    @Test
    fun repositoryBootstrapsSchemaAndStoresVisiblePosts() {
        val databasePath = createTempFile("blog-repository-", ".sqlite")
        val repository = BlogRepository(databasePath)

        repository.initialize()

        val sourcePath = createTempFile("blog-source-", ".md")
        val outputDir = createTempDirectory("blog-output-")
        val htmlPath = outputDir.resolve("hello-world").resolve("content.html")

        repository.upsert(
            BlogPostRecord(
                slug = "hello-world",
                sourcePath = sourcePath.absolutePathString(),
                sourceLastModified = 1_710_000_000L,
                title = "Hello World",
                summary = "A first post",
                publishedDate = Instant.parse("2026-04-15T11:45:00Z"),
                tags = listOf("kotlin", "ktor"),
                excerpt = "First paragraph.",
                htmlPath = htmlPath.absolutePathString(),
                htmlLastGenerated = 1_710_000_100L,
                isDeleted = false,
            ),
        )

        val stored = repository.findBySlug("hello-world")

        assertNotNull(stored)
        assertEquals("Hello World", stored.title)
        assertContentEquals(listOf("kotlin", "ktor"), stored.tags)
        assertEquals(listOf("hello-world"), repository.listVisible().map(BlogPostRecord::slug))
    }

    @Test
    fun repositoryExcludesDeletedRowsAndAllowsReingestion() {
        val databasePath = createTempFile("blog-repository-", ".sqlite")
        val repository = BlogRepository(databasePath)

        repository.initialize()

        val sourcePath = createTempFile("blog-source-", ".md")
        val outputDir = createTempDirectory("blog-output-")
        val htmlPath = outputDir.resolve("hello-world").resolve("content.html")

        repository.upsert(
            BlogPostRecord(
                slug = "hello-world",
                sourcePath = sourcePath.absolutePathString(),
                sourceLastModified = 1_710_000_000L,
                title = "Hello World",
                summary = "A first post",
                publishedDate = Instant.parse("2026-04-15T11:45:00Z"),
                tags = listOf("kotlin"),
                excerpt = "First paragraph.",
                htmlPath = htmlPath.absolutePathString(),
                htmlLastGenerated = 1_710_000_100L,
                isDeleted = false,
            ),
        )

        repository.markDeleted("hello-world")

        assertTrue(repository.listVisible().isEmpty())
        assertTrue(repository.findBySlug("hello-world")!!.isDeleted)

        repository.upsert(
            BlogPostRecord(
                slug = "hello-world",
                sourcePath = sourcePath.absolutePathString(),
                sourceLastModified = 1_710_000_500L,
                title = "Hello Again",
                summary = "Updated summary",
                publishedDate = Instant.parse("2026-04-16T09:30:00Z"),
                tags = listOf("kotlin", "sqlite"),
                excerpt = "Updated paragraph.",
                htmlPath = htmlPath.absolutePathString(),
                htmlLastGenerated = 1_710_000_900L,
                isDeleted = false,
            ),
        )

        val stored = repository.findBySlug("hello-world")

        assertNotNull(stored)
        assertFalse(stored.isDeleted)
        assertEquals("Hello Again", stored.title)
        assertEquals(listOf("hello-world"), repository.listVisible().map(BlogPostRecord::slug))
    }
}
