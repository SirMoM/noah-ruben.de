package de.noah_ruben.site.projects

import de.noah_ruben.data.Cache
import de.noah_ruben.data.model.Project
import de.noah_ruben.misc.CssClasses
import de.noah_ruben.site.commandLineEmulation
import de.noah_ruben.site.projects.OrderBy.Date
import de.noah_ruben.site.projects.OrderBy.Popularity
import de.noah_ruben.site.projects.OrderBy.Relevance
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.parseUrlEncodedParameters
import io.ktor.server.application.Application
import io.ktor.server.application.log
import io.ktor.server.html.respondHtml
import io.ktor.server.request.receiveText
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.html.div
import kotlinx.html.id
import kotlinx.html.stream.createHTML

internal const val SEARCH_PATH = "/search"
internal const val QP_QUERY = "query"
internal const val QP_TOPIC = "topic"
internal const val QP_ADD_TOPIC = "addTopic"
internal const val QP_REMOVE_TOPIC = "removeTopic"
internal const val QP_TOGGLE_TOPIC = "toggleTopic"
internal const val QP_LANGUAGE = "language"
internal const val QP_SET_LANGUAGE = "setLanguage"
internal const val QP_ORDER_BY = "orderBy"
internal const val QP_DIR = "dir"
internal const val QP_TOGGLE_DIR = "toggleDir"
internal const val QP_WITH_SEARCHBAR = "withSearchBar"
internal const val LANGUAGE_PLACEHOLDER = "<Language>"
internal const val SEARCH_RESULTS = "search-results"
internal const val SEARCH_REPLACE = "search-replace"

enum class OrderBy {
    Relevance,
    Date,
    Popularity,
}

data class SearchParameters(
    val query: String,
    val topics: List<String>,
    val language: String,
    val orderBy: OrderBy,
    val withSearchbar: Boolean,
    val descending: Boolean,
) {
    companion object {
        fun from(params: Parameters): SearchParameters = SearchParameters(
            query = params[QP_QUERY].orEmpty(),
            topics = params.topics(),
            language = params.language(),
            orderBy = params[QP_ORDER_BY]?.let { OrderBy.valueOf(it) } ?: Relevance,
            withSearchbar = params[QP_WITH_SEARCHBAR].toBoolean(),
            descending = params.descending(),
        )

        fun defaults(): SearchParameters = SearchParameters(
            query = "",
            topics = emptyList(),
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
                call.respondText(
                    renderSearchReplace(searchParameters, projects),
                    ContentType.Text.Html,
                )
            } catch (e: IllegalArgumentException) {
                log.warn("Invalid parameter value in search query. Payload: '{}'. Error: {}", payload, e.message)
                call.respondText(
                    renderSearchReplace(
                        searchParameters = SearchParameters.defaults(),
                        projects = Cache.getProjects(),
                        errorMessage = "Invalid value for a search parameter. Received: $payload",
                    ),
                    ContentType.Text.Html,
                )
            } catch (e: Exception) {
                log.error("Error processing search query. Payload: '{}'", payload, e)
                call.respondText(
                    renderSearchReplace(
                        searchParameters = SearchParameters.defaults(),
                        projects = Cache.getProjects(),
                        errorMessage = "An unexpected error occurred. Please try again later.",
                    ),
                    ContentType.Text.Html,
                )
            }
        }
    }
}

private fun renderSearchReplace(
    searchParameters: SearchParameters,
    projects: List<Project>,
    errorMessage: String? = null,
): String = createHTML().div(
    classes = CssClasses.CONTENT_CONTAINER,
) {
    id = SEARCH_REPLACE
    projectsShell(
        projects = projects,
        searchParameters = searchParameters,
        errorMessage = errorMessage,
    )
    commandLineEmulation()
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

private fun Parameters.topics(): List<String> {
    val selectedTopics = getAll(QP_TOPIC).orEmpty()
        .map(String::trim)
        .filter(String::isNotBlank)
        .distinct()

    return when {
        this[QP_TOGGLE_TOPIC] != null -> selectedTopics.toggle(this[QP_TOGGLE_TOPIC].orEmpty())
        this[QP_REMOVE_TOPIC] != null -> selectedTopics.remove(this[QP_REMOVE_TOPIC].orEmpty())
        this[QP_ADD_TOPIC] != null -> selectedTopics.add(this[QP_ADD_TOPIC].orEmpty())
        else -> selectedTopics
    }
}

private fun Parameters.language(): String = this[QP_SET_LANGUAGE] ?: this[QP_LANGUAGE] ?: LANGUAGE_PLACEHOLDER

private fun Parameters.descending(): Boolean {
    val currentDirection = this[QP_DIR] == "desc"
    return if (this[QP_TOGGLE_DIR].toBoolean()) !currentDirection else currentDirection
}

private fun List<String>.toggle(topic: String): List<String> = when {
    topic.isBlank() -> this
    topic in this -> filterNot { it == topic }
    else -> this + topic
}

private fun List<String>.remove(topic: String): List<String> = when {
    topic.isBlank() -> this
    else -> filterNot { it == topic }
}

private fun List<String>.add(topic: String): List<String> = when {
    topic.isBlank() || topic in this -> this
    else -> this + topic
}

fun List<Project>.filterByTopics(topics: List<String>): List<Project> {
    if (topics.isEmpty()) return this
    return this.filter { project -> project.topics.any(topics::contains) }
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

fun List<Project>.filterBySearchParameters(searchParameters: SearchParameters): List<Project> = this.filterByTopics(searchParameters.topics).filerByLanguage(searchParameters.language)
    .query(searchParameters.query).sortedBy(searchParameters.orderBy, searchParameters.descending)
