package de.noah_ruben.site.projects

import de.noah_ruben.data.Cache
import de.noah_ruben.data.model.Project
import de.noah_ruben.misc.CssClasses
import de.noah_ruben.misc.CssClasses.Shared.ERROR_MESSAGE_BOX
import de.noah_ruben.site.projects.OrderBy.Date
import de.noah_ruben.site.projects.OrderBy.Popularity
import de.noah_ruben.site.projects.OrderBy.Relevance
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.parseUrlEncodedParameters
import io.ktor.server.application.Application
import io.ktor.server.application.log
import io.ktor.server.html.respondHtml
import io.ktor.server.plugins.MissingRequestParameterException
import io.ktor.server.request.receiveText
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.util.getOrFail
import kotlinx.html.body
import kotlinx.html.br
import kotlinx.html.div
import kotlinx.html.id
import kotlinx.html.stream.createHTML
import java.net.URLDecoder

internal const val SEARCH_PATH = "/search"
internal const val QP_QUERY = "query"
internal const val QP_TOPIC = "topic"
internal const val QP_LANGUAGE = "language"
internal const val QP_ORDER_BY = "orderBy"
internal const val QP_DIR = "dir"
internal const val QP_WITH_SEARCHBAR = "withSearchBar"
internal const val TOPIC_PLACEHOLDER = "<topic>"
internal const val LANGUAGE_PLACEHOLDER = "<Language>"
internal const val SEARCH_RESULTS = "search-results"

enum class OrderBy {
    Relevance,
    Date,
    Popularity,
}

// TODO not found wenn keine projecte found
// Topic und languages begrenzen ?
data class SearchParameters(
    val query: String,
    val topic: String,
    val language: String,
    val orderBy: OrderBy,
    val withSearchbar: Boolean,
    val descending: Boolean,
) {
    companion object {
        fun from(params: Parameters): SearchParameters = SearchParameters(
            query = params.getOrFail(QP_QUERY),
            topic = params[QP_TOPIC] ?: TOPIC_PLACEHOLDER,
            language = params[QP_LANGUAGE] ?: LANGUAGE_PLACEHOLDER,
            orderBy = params[QP_ORDER_BY]?.let { OrderBy.valueOf(it) } ?: Relevance,
            withSearchbar = params[QP_WITH_SEARCHBAR].toBoolean(),
            descending = params[QP_DIR] == "desc",
        )

        fun defaults(): SearchParameters = SearchParameters(
            query = "",
            topic = TOPIC_PLACEHOLDER,
            language = LANGUAGE_PLACEHOLDER,
            orderBy = Relevance,
            withSearchbar = false,
            descending = false,
        )
    }
}

fun Application.projectsPageRouting() {
    routing {
        get("/projects") {
            call.respondHtml {
                projectsPage()
            }
        }
        post(SEARCH_PATH) {
            val payload = call.receiveText()
            try {
                val params: Parameters = payload.parseUrlEncodedParameters()

                val searchParameters = SearchParameters.from(params)

                log.info(searchParameters.toString())

                val projects = Cache.getProjects().filterBySearchParameters(searchParameters)
                val htmlFragment = if (projects.isEmpty()) {
                    createHTML().div {
                        nothingFoundProjectTile()
                    }
                } else if (searchParameters.withSearchbar) {
                    // TODO: Add parameters to search bar from / search query parameter
                    createHTML().div(
                        classes = CssClasses.CONTENT_CONTAINER,
                    ) {
                        id = "search-replace"
                        mainSearchBar(searchParameters)
                        br()
                        projectList(projects)
                    }
                } else {
                    createHTML().div {
                        id = "search-replace"
                        projectList(projects)
                    }
                }

                call.respondText(htmlFragment, ContentType.Text.Html)
            } catch (e: MissingRequestParameterException) {
                log.warn("Missing parameter in search query. Payload: '{}'. Error: {}", payload, e.message)
                call.respondHtml {
                    body {
                        div(
                            classes = ERROR_MESSAGE_BOX,
                        ) {
                            +"Error: Missing required search parameter (${e.parameterName}). Please try again."
                            br()
                            +"Received: ${URLDecoder.decode(payload, "UTF-8")}"
                        }
                        projectList(Cache.getProjects())
                    }
                }
            } catch (e: IllegalArgumentException) {
                log.warn("Invalid parameter value in search query. Payload: '{}'. Error: {}", payload, e.message)
                call.respondHtml {
                    body {
                        div(
                            classes = ERROR_MESSAGE_BOX,
                        ) {
                            +"Error: Invalid value for a search parameter (e.g., Order By). Please try again."
                            br()
                            +"Received: ${URLDecoder.decode(payload, "UTF-8")}"
                        }
                        projectList(Cache.getProjects())
                    }
                }
            } catch (e: Exception) {
                log.error("Error processing search query. Payload: '{}'", payload, e)
                call.respondHtml {
                    body {
                        div(
                            classes = ERROR_MESSAGE_BOX,
                        ) {
                            +"An unexpected error occurred. Please try again later."
                        }
                        projectList(Cache.getProjects())
                    }
                }
            }
        }
    }
}

fun List<Project>.sortedBy(selector: OrderBy, descending: Boolean): List<Project> = when (selector) {
    Relevance -> {
        if (descending) this.sortedByDescending { it.stars } else this.sortedBy { it.stars }
    }

    Date -> {
        if (descending) this.sortedByDescending { it.created } else this.sortedBy { it.created }
    }

    Popularity -> {
        if (descending) this.sortedByDescending { it.stars } else this.sortedBy { it.stars }
    }
}

fun List<Project>.filerByTopic(topic: String): List<Project> {
    if (topic.isBlank() || topic == TOPIC_PLACEHOLDER) return this
    return this.filter { topic in it.topics }
}

fun List<Project>.filerByLanguage(language: String): List<Project> {
    if (language.isBlank() || language == LANGUAGE_PLACEHOLDER) return this
    return this.filter { language in it.languages }
}

fun List<Project>.query(query: String): List<Project> {
    if (query.isBlank()) return this
    return this.filter {
        it.name.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true)
    }
}

fun List<Project>.filterBySearchParameters(searchParameters: SearchParameters): List<Project> = this.filerByTopic(searchParameters.topic).filerByLanguage(searchParameters.language)
    .query(searchParameters.query).sortedBy(searchParameters.orderBy, searchParameters.descending)
