package de.noah_ruben.site.blog

import de.noah_ruben.data.blog.BlogPostRecord
import de.noah_ruben.data.blog.BlogStorage
import de.noah_ruben.data.blog.blogIngestionService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.html.respondHtml
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import java.nio.file.Path

fun Application.blogRouting() {
    routing {
        get("/blog") {
            val query = call.request.queryParameters[BLOG_QUERY_PARAMETER].orEmpty()
            val posts = blogIngestionService().visiblePosts().filterByQuery(query)

            call.respondHtml(HttpStatusCode.OK) {
                blogOverviewPage(posts, query)
            }
        }

        get("/blog/{slug}") {
            val slug = call.parameters["slug"] ?: run {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }

            val post = blogIngestionService().ensurePostAvailable(slug) ?: run {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }

            val articleHtml = BlogStorage.readContent(Path.of(post.htmlPath))
            call.respondHtml(HttpStatusCode.OK) {
                blogPostPage(post, articleHtml)
            }
        }
    }
}

private fun List<BlogPostRecord>.filterByQuery(query: String): List<BlogPostRecord> {
    if (query.isBlank()) {
        return this
    }

    return filter { post ->
        post.title.contains(query, ignoreCase = true) ||
            post.summary.contains(query, ignoreCase = true) ||
            post.tags.any { tag -> tag.contains(query, ignoreCase = true) }
    }
}
