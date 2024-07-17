package de.noah_ruben.site.projects

import de.noah_ruben.data.Cache
import de.noah_ruben.data.model.Project
import de.noah_ruben.site.projects.OrderBy.Date
import de.noah_ruben.site.projects.OrderBy.Popularity
import de.noah_ruben.site.projects.OrderBy.Relevance
import io.ktor.http.parseUrlEncodedParameters
import io.ktor.server.application.Application
import io.ktor.server.html.respondHtml
import io.ktor.server.request.receive
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.util.getOrFail
import kotlinx.coroutines.runBlocking
import kotlinx.html.body
import kotlinx.html.h2

internal const val SEARCH_PATH = "/search"
internal const val QP_QUERY = "query"
internal const val QP_TOPIC = "topic"
internal const val QP_LANGUAGE = "language"
internal const val QP_ORDER_BY = "orderBy"
internal const val TOPIC_PLACEHOLDER = "<topic>"
internal const val LANGUAGE_PLACEHOLDER = "<language>"
internal const val SEARCH_RESULTS = "search-results"

enum class OrderBy {
    Relevance,
    Date,
    Popularity,
}

fun Application.projectsPageRouting() {
    routing {
        get("/projects") {
            call.respondHtml {
                projectsPage()
            }
        }
        post(SEARCH_PATH) {
            val payload = runBlocking {
                call.receive<String>()
            }
            val params = payload.parseUrlEncodedParameters()
            val query = params.getOrFail(QP_QUERY)
            val topic = params.getOrFail(QP_TOPIC)
            val language = params.getOrFail(QP_LANGUAGE)
            val orderBy = OrderBy.valueOf(params.getOrFail(QP_ORDER_BY))

            val projects = Cache.getProjects()
                .filerByTopic(topic)
                .filerByLanguage(language)
                .query(query)
                .sortedBy(orderBy)

            call.respondHtml {
                body {
                    h2 {
                        +payload
                    }
                    projectList(projects)
                }
            }
        }
    }
}

fun List<Project>.sortedBy(selector: OrderBy): List<Project> = when (selector) {
    Relevance -> this.sortedBy {
        it.releases
    }
    Date -> this.sortedBy {
        it.lastModified
    }
    Popularity -> this.sortedByDescending {
        it.stars
    }
}

fun List<Project>.filerByTopic(topic: String): List<Project> {
    if (topic == TOPIC_PLACEHOLDER) return this
    return this.filter { topic in it.topics }
}

fun List<Project>.filerByLanguage(language: String): List<Project> {
    if (language == LANGUAGE_PLACEHOLDER) return this
    return this.filter { language in it.languages }
}
fun List<Project>.query(query: String): List<Project> = this.filter {
    it.name.contains(query)
}
