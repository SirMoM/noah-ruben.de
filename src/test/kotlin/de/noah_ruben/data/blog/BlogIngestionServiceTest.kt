package de.noah_ruben.data.blog

import kotlin.io.path.createDirectories
import kotlin.io.path.createTempDirectory
import kotlin.io.path.exists
import kotlin.io.path.writeText
import kotlin.io.path.deleteExisting
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import java.time.Instant

class BlogIngestionServiceTest {

    @Test
    fun initializeCreatesMissingDirectories() {
        val paths = createBlogPaths()
        val service = BlogIngestionService(paths)

        service.use {
            it.initialize()

            assertTrue(paths.sourceDir.exists())
            assertTrue(paths.outputDir.exists())
            assertTrue(it.visiblePosts().isEmpty())
        }
    }

    @Test
    fun startupScanConvertsNewSourcesAndRefreshesCache() {
        val paths = createBlogPaths()
        paths.sourceDir.createDirectories()
        paths.sourceDir.resolve("hello-world.md").writeText(sampleMarkdown(title = "Hello World"))

        val service = BlogIngestionService(paths)

        service.use {
            it.initialize()
            assertTrue(it.awaitIdle())

            assertEquals(listOf("hello-world"), it.visiblePosts().map(BlogPostRecord::slug))
            assertTrue(paths.htmlPathFor("hello-world").exists())
            assertNotNull(it.repository().findBySlug("hello-world"))
        }
    }

    @Test
    fun startupMarksMissingSourceFilesAsDeleted() {
        val paths = createBlogPaths()
        val repository = BlogRepository(paths.databasePath)
        repository.initialize()
        repository.upsert(
            BlogPostRecord(
                slug = "ghost-post",
                sourcePath = paths.sourceDir.resolve("ghost-post.md").toString(),
                sourceLastModified = 1_710_000_000L,
                title = "Ghost Post",
                summary = "Missing source",
                publishedDate = Instant.parse("2026-04-15T11:45:00Z"),
                tags = listOf("missing"),
                excerpt = "Ghost excerpt",
                htmlPath = paths.htmlPathFor("ghost-post").toString(),
                htmlLastGenerated = 1_710_000_100L,
                isDeleted = false,
            ),
        )

        val service = BlogIngestionService(paths, repository = repository)

        service.use {
            it.initialize()

            assertTrue(repository.findBySlug("ghost-post")!!.isDeleted)
            assertTrue(it.visiblePosts().isEmpty())
        }
    }

    @Test
    fun watcherProcessesNewFilesWhileRunning() {
        val paths = createBlogPaths()
        val service = BlogIngestionService(paths)

        service.use {
            it.initialize()

            paths.sourceDir.resolve("watch-me.md").writeText(sampleMarkdown(title = "Watch Me"))

            assertTrue(waitUntil { it.visiblePosts().any { post -> post.slug == "watch-me" } })
        }
    }

    @Test
    fun ensurePostAvailableRegeneratesMissingHtml() {
        val paths = createBlogPaths()
        paths.sourceDir.createDirectories()
        paths.sourceDir.resolve("hello-world.md").writeText(sampleMarkdown(title = "Hello World"))

        val service = BlogIngestionService(paths)

        service.use {
            it.initialize()
            assertTrue(it.awaitIdle())

            paths.htmlPathFor("hello-world").deleteExisting()

            val post = it.ensurePostAvailable("hello-world")

            assertNotNull(post)
            assertEquals("hello-world", post.slug)
            assertTrue(paths.htmlPathFor("hello-world").exists())
        }
    }
}

private fun createBlogPaths(): BlogPaths {
    val root = createTempDirectory("blog-ingestion-")
    return BlogPaths(
        sourceDir = root.resolve("content").resolve("blog"),
        outputDir = root.resolve("var").resolve("blog"),
        databasePath = root.resolve("var").resolve("blog").resolve("blog.sqlite"),
    )
}

private fun sampleMarkdown(title: String): String = """
    ---
    title: $title
    summary: A generated summary
    tags:
      - kotlin
      - blog
    date: 2026-04-15T11:45:00Z
    ---

    First paragraph for excerpt.

    Additional content for the full post body.
""".trimIndent()

private fun waitUntil(
    timeoutMillis: Long = 3_000L,
    block: () -> Boolean,
): Boolean {
    val deadline = System.currentTimeMillis() + timeoutMillis
    while (System.currentTimeMillis() < deadline) {
        if (block()) {
            return true
        }
        Thread.sleep(50)
    }
    return block()
}
