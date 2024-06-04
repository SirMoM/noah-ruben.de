package de.noah_ruben.site

import de.noah_ruben.data.Cache
import de.noah_ruben.data.Cache.getAllLanguages
import de.noah_ruben.data.Cache.getAllTopics
import de.noah_ruben.data.Project
import de.noah_ruben.misc.hxSwap
import de.noah_ruben.misc.hxTarget
import io.ktor.server.application.Application
import io.ktor.server.html.respondHtml
import io.ktor.server.request.receive
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.coroutines.runBlocking
import kotlinx.html.BODY
import kotlinx.html.ButtonType
import kotlinx.html.FlowContent
import kotlinx.html.FormEncType
import kotlinx.html.FormMethod
import kotlinx.html.HTML
import kotlinx.html.InputType
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.head
import kotlinx.html.hidden
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.option
import kotlinx.html.p
import kotlinx.html.select

private const val SEARCH_PATH = "/search"

fun Application.projectsPage() {
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
            println("payload: $payload")
            call.respondHtml {
                body {
                    h2 {
                        +payload
                    }
                    projectList(Cache.getProjects())
                }
            }
        }
    }
}

fun HTML.projectsPage() {
    head {
        defaultHeader()
    }
    defaultBody {
        projectsPageBody()
    }
}

private const val SEARCH_RESULTS = "search-results"

fun BODY.projectsPageBody() {
    val projects = Cache.getProjects()

    div("container mx-auto p-4") {
        h1("text-2xl font-bold mb-4") { +"Projects" }
        searchBar()
        projectList(projects)
        commandLineEmulation()
    }
}

fun FlowContent.projectList(projects: List<Project>, orderBy: String = "") {
    div {
        id = SEARCH_RESULTS
        projects.forEach {
            projectTile(it)
        }
    }
}

fun FlowContent.projectTile(project: Project) {
    with(project) {
        div("border border-gray-300 rounded p-4 mb-4 max-w rounded overflow-hidden shadow-lg bg-white") {
            div {
                div("font-bold text-xl mb-2") {
                    +name
                }
                p("text-gray-700 text-base") {
                    +description
                }
                div("flex items-center mt-2 text-gray-600 text-sm") {
                    +"Released at: $releases"
                }
                div("flex items-center mt-4 text-gray-600 text-sm") {
                    +"Stars: "
                    +stars.toString()
                }
                if (topics.isNotEmpty()) {
                    div("flex items-center mt-2 text-gray-600 text-sm") {
                        +"Topics: "
                        +topics.joinToString(", ")
                    }
                }
                div("flex items-center mt-2") {
                    for (lang in languages) {
                        languageTag(lang)
                    }
                }
            }
            a(href = githubLink, classes = "inline-block bg-gray-200 rounded-full px-3 py-1 text-sm font-semibold text-gray-700 mr-2") {
                +"GitHub"
            }
            a(href = link, classes = "inline-block bg-gray-200 rounded-full px-3 py-1 text-sm font-semibold text-gray-700") {
                +"Visit"
            }
        }
    }
}

fun FlowContent.languageTag(tag: String) {
    a(classes = "mx-0.5 inline-block bg-gray-200 rounded-full px-3 py-1 text-sm font-semibold text-gray-700") {
        +tag
    }
}

fun FlowContent.searchBar() {
    input {
        form="binaryForm"
        type=InputType.search
        name="binaryFile"
    }
    form {
        method=FormMethod.post
        id="binaryForm"
        encType=FormEncType.multipartFormData
        hxSwap("outerHTML")
        hxTarget("#$SEARCH_RESULTS")

        button {
            type=ButtonType.submit
            +"Submit"
        }
    }
}

fun FlowContent.mainSearchBar() {

    form( classes = "form-control border border-gray-300 rounded p-4 mb-4 max-w rounded overflow-hidden shadow-lg  text-gray-700", action = "/search", method = FormMethod.post) {
        label {
            htmlFor = "mainSearch"
            text("Search:")
        }

        input(InputType.text, name = "query") { id = "mainSearch" }

        label {
            htmlFor = "topic"
            text("Topic:")
        }

        select {
            name = "topic"
            option("<topic>") {
                disabled = true
                selected = true
                hidden = true
                +"<topic>"
            }
            for (topic in getAllTopics()) {
                option(topic) {
                    +topic
                }
            }
        }

        label {
            htmlFor = "language"
            text("Language:")
        }

        select {
            name = "language"
            option("<Language>") {
                disabled = true
                selected = true
                hidden = true
                +"<Language>"
            }
            for (language in getAllLanguages()) {
                option {
                    value = language
                    +language
                }
            }
        }

        label {
            htmlFor = "orderBy"
            text("Order by:")
        }

        select {
            name = "orderBy"
            option {
                value = "relevance"
                +"Relevance"
            }
            option {
                value = "date"
                +"Date"
            }
            option {
                value = "popularity"
                +"Popularity"
            }
        }

        button(type = ButtonType.submit) {
            text("Search")
            type=ButtonType.submit
        }
    }
}
