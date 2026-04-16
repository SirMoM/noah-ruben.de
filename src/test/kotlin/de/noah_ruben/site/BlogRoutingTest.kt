package de.noah_ruben.site

import de.noah_ruben.data.blog.BlogPaths
import de.noah_ruben.data.blog.BlogPostRecord
import de.noah_ruben.data.blog.BlogRepository
import de.noah_ruben.testApplicationWithRepositoryFake
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlin.io.path.createDirectories
import kotlin.io.path.createTempDirectory
import kotlin.io.path.deleteExisting
import kotlin.io.path.exists
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import java.time.Instant

class BlogRoutingTest {

    @Test
    fun blogOverviewRendersPostMetadata() {
        val paths = createBlogPaths()
        paths.sourceDir.createDirectories()
        paths.sourceDir.resolve("hello-world.md").writeText(sampleMarkdown(title = "Hello World"))

        testApplicationWithRepositoryFake(
            configOverrides = blogConfigOverrides(paths),
        ) {
            client.get("/blog")
            assertTrue(waitUntil { client.get("/blog").bodyAsText().contains("Hello World") })
            val response = client.get("/blog")

            assertEquals(HttpStatusCode.OK, response.status)
            assertTrue(response.bodyAsText().contains("Hello World"))
            assertTrue(response.bodyAsText().contains("A generated summary"))
            assertTrue(response.bodyAsText().contains("First paragraph for excerpt."))
            assertTrue(response.bodyAsText().contains("kotlin"))
        }
    }

    @Test
    fun blogOverviewSearchUsesTitleSummaryAndTagsOnly() {
        val paths = createBlogPaths()
        paths.sourceDir.createDirectories()
        paths.sourceDir.resolve("summary-match.md").writeText(sampleMarkdown(title = "Summary Match", summary = "SQLite notes"))
        paths.sourceDir.resolve("body-only.md").writeText(sampleMarkdown(title = "Body Only", body = "sqlite appears only in the body"))

        testApplicationWithRepositoryFake(
            configOverrides = blogConfigOverrides(paths),
        ) {
            client.get("/blog")
            assertTrue(waitUntil { paths.htmlPathFor("summary-match").exists() })
            assertTrue(waitUntil { paths.htmlPathFor("body-only").exists() })
            val response = client.get("/blog?query=sqlite")

            assertEquals(HttpStatusCode.OK, response.status)
            assertTrue(response.bodyAsText().contains("Summary Match"))
            assertFalse(response.bodyAsText().contains("Body Only"))
        }
    }

    @Test
    fun blogPostRouteServesSavedHtmlInsideSharedShell() {
        val paths = createBlogPaths()
        paths.sourceDir.createDirectories()
        paths.sourceDir.resolve("hello-world.md").writeText(sampleMarkdown(title = "Hello World"))

        testApplicationWithRepositoryFake(
            configOverrides = blogConfigOverrides(paths),
        ) {
            val response = client.get("/blog/hello-world")

            assertEquals(HttpStatusCode.OK, response.status)
            assertTrue(response.bodyAsText().contains("Hello World"))
            assertTrue(response.bodyAsText().contains("First paragraph for excerpt."))
            assertTrue(response.bodyAsText().contains("""id="theme-toggle""""))
        }
    }

    @Test
    fun deletedBlogPostReturnsNotFound() {
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
                tags = listOf("ghost"),
                excerpt = "Ghost excerpt",
                htmlPath = paths.htmlPathFor("ghost-post").toString(),
                htmlLastGenerated = 1_710_000_100L,
                isDeleted = true,
            ),
        )

        testApplicationWithRepositoryFake(
            configOverrides = blogConfigOverrides(paths),
        ) {
            val response = client.get("/blog/ghost-post")

            assertEquals(HttpStatusCode.NotFound, response.status)
        }
    }

    @Test
    fun missingSavedHtmlIsRegeneratedOnDemand() {
        val paths = createBlogPaths()
        paths.sourceDir.createDirectories()
        paths.sourceDir.resolve("hello-world.md").writeText(sampleMarkdown(title = "Hello World"))

        testApplicationWithRepositoryFake(
            configOverrides = blogConfigOverrides(paths),
        ) {
            client.get("/blog")
            assertTrue(waitUntil { paths.htmlPathFor("hello-world").exists() })
            paths.htmlPathFor("hello-world").deleteExisting()

            val response = client.get("/blog/hello-world")

            assertEquals(HttpStatusCode.OK, response.status)
            assertTrue(paths.htmlPathFor("hello-world").exists())
        }
    }
}

private fun blogConfigOverrides(paths: BlogPaths): Map<String, String> = mapOf(
    "blog.sourceDir" to paths.sourceDir.toString(),
    "blog.outputDir" to paths.outputDir.toString(),
    "blog.databasePath" to paths.databasePath.toString(),
)

private fun createBlogPaths(): BlogPaths {
    val root = createTempDirectory("blog-routing-")
    return BlogPaths(
        sourceDir = root.resolve("content").resolve("blog"),
        outputDir = root.resolve("var").resolve("blog"),
        databasePath = root.resolve("var").resolve("blog").resolve("blog.sqlite"),
    )
}

private fun sampleMarkdown(
    title: String,
    summary: String = "A generated summary",
    body: String = "First paragraph for excerpt.",
): String = """
    ---
    title: $title
    summary: $summary
    tags:
      - kotlin
      - blog
    date: 2026-04-15T11:45:00Z
    ---

    $body

    Additional content for the full post body.
""".trimIndent()

private suspend fun waitUntil(
    timeoutMillis: Long = 3_000L,
    block: suspend () -> Boolean,
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
