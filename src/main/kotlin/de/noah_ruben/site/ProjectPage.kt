package de.noah_ruben.site

import de.noah_ruben.data.Cache
import de.noah_ruben.data.Cache.getAllLanguages
import de.noah_ruben.data.Cache.getAllTopics
import de.noah_ruben.data.model.Project
import de.noah_ruben.misc.borderGray
import de.noah_ruben.misc.hxIndicator
import de.noah_ruben.misc.hxPost
import de.noah_ruben.misc.hxTarget
import de.noah_ruben.misc.hxTrigger
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
import kotlinx.html.FormMethod
import kotlinx.html.HTML
import kotlinx.html.InputType
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.br
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.head
import kotlinx.html.hidden
import kotlinx.html.id
import kotlinx.html.img
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.option
import kotlinx.html.p
import kotlinx.html.select
import kotlinx.html.span
import java.lang.Thread.sleep

private const val SEARCH_PATH = "/search"

fun Application.projectsPage() {
    routing {
        get("/projects") {
            call.respondHtml {
                projectsPage()
            }
        }
        post(SEARCH_PATH) {
            sleep(2000)
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
        mainSearchBar()
        br
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

private val borderGrey400 = borderGray("400")

fun FlowContent.mainSearchBar() {
    val inputClasses = setOf("flex-grow", "bg-gray-500", "border", borderGrey400, "rounded", "p-2")
    val searchBoxClasses = setOf("flex-grow", "bg-gray-700", "border", borderGrey400, "rounded", "p-4", "mb-4")
    val selectClasses = setOf("bg-gray-500", "border", borderGrey400, "rounded", "mx-2")

    div {
        classes = searchBoxClasses
        span("htmx-indicator") {
            id = "spinner"
            img(src = "/resources/bars.svg") {
                +"Searching..."
            }
        }
        form(action = SEARCH_PATH, method = FormMethod.post) {
            classes = setOf("formControl", "justify-start", "flex-wrap")
            hxTrigger("input changed delay:500ms, search")
            hxIndicator("#spinner")
            hxTarget("#search-results")

            div(classes = "flex flex-col") {
                label(classes = "order-0") {
                    htmlFor = "mainSearch"
                    text("Search:")
                }
                input(InputType.text, name = "query") {
                    autoFocus = true
                    classes = inputClasses
                    id = "mainSearch"
                    hxPost("/search")
                    hxTrigger("input changed delay:500ms, search")
                    hxTarget("#search-results")
                    hxIndicator(".htmx-indicator")
                }
            }
            div {
                label {
                    htmlFor = "topic"
                    text("Topic:")
                }

                select {
                    hxPost(SEARCH_PATH)
                    classes = selectClasses
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
                    classes = selectClasses
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
                    classes = selectClasses
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
            }
            button(type = ButtonType.submit) {
                classes = setOf("bg-gray-700", "text-white", "rounded", "border", "p-2", "mt-4", "text-xl")
                hxPost(SEARCH_PATH)
                div(classes = "flex") {
                    text("Search ")
                    span("htmx-indicator ml-2") {
                        id = "spinner"
                        img(src = "/resources/bars.svg") {
                            alt = "Searching..."
                        }
                    }
                }
            }
        }
    }
}
