package de.noah_ruben.site.projects

import de.noah_ruben.data.Cache
import de.noah_ruben.data.Cache.getAllLanguages
import de.noah_ruben.data.Cache.getAllTopics
import de.noah_ruben.data.model.Project
import de.noah_ruben.misc.borderGray
import de.noah_ruben.misc.colorFromString
import de.noah_ruben.misc.hxIndicator
import de.noah_ruben.misc.hxPost
import de.noah_ruben.misc.hxTarget
import de.noah_ruben.misc.hxTrigger
import de.noah_ruben.misc.invertedFromString
import de.noah_ruben.site.commandLineEmulation
import de.noah_ruben.site.defaultBody
import de.noah_ruben.site.defaultHeader
import kotlinx.html.*

fun HTML.projectsPage() {
    head {
        defaultHeader()
    }
    defaultBody {
        projectsPageBody()
    }
}

fun BODY.projectsPageBody() {
    val projects = Cache.getProjects()

    div("container mx-auto p-4") {
        h1("text-2xl font-bold mb-4") { +"Projects" }
        mainSearchBar()
        br()
        projectList(projects)
        commandLineEmulation()
    }
}

fun FlowContent.projectList(
    projects: List<Project>,
) {
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
            if (link.isNotBlank() && link != "#") { // Example check
                a(href = link, classes = "inline-block bg-gray-200 rounded-full px-3 py-1 text-sm font-semibold text-gray-700") {
                    +"Visit"
                }
            }
        }
    }
}

fun FlowContent.languageTag(tag: String) {
    a(classes = "mx-0.5 inline-block bg-[#${tag.colorFromString()}] rounded-full px-3 py-1 text-sm font-semibold text-[#${tag.colorFromString().invertedFromString()}]") {
        +tag
    }
}

private val borderGrey400 = borderGray("400")

fun FlowContent.mainSearchBar() {
    val inputClasses = setOf("flex-grow", "bg-gray-500", "border", borderGrey400, "rounded", "p-2")
    val searchBoxClasses = setOf("flex-grow", "bg-gray-700", "border", borderGrey400, "rounded", "p-4", "mb-4")
    val selectClasses = setOf("bg-gray-500", "border", borderGrey400, "rounded", "mx-2")
    val checkboxClasses = setOf("bg-gray-700", "text-white", "rounded", "border", "p-2", "mt-4", "text-xl", "align-middle") // Added align-middle
    val labelClasses = setOf("mr-1", "align-middle")

    div {
        classes = searchBoxClasses
        span("htmx-indicator") {
            id = "spinner"
            img(src = "/resources/bars.svg", alt = "Searching...") // Use alt attribute
            +"Searching..."
        }

        form(action = SEARCH_PATH, method = FormMethod.post) {
            classes = setOf("formControl", "justify-start", "flex-wrap")
            hxPost(SEARCH_PATH)
            hxTarget("#search-results")
            hxIndicator("#spinner")
            hxTrigger("submit, change from:select, change from:input[type='checkbox'] delay:100ms, input from:input[type='text'] changed delay:500ms")

            div(classes = "flex flex-col") {
                label(classes = "order-0") {
                    htmlFor = "mainSearch"
                    +"Search:"
                }
                input(InputType.text, name = QP_QUERY) {
                    // Use constants
                    autoFocus = true
                    classes = inputClasses
                    id = "mainSearch"
                    placeholder = "Search"
                    value = ""
                }
            }

            div {
                label(classes = labelClasses.joinToString(separator = " ")) {
                    htmlFor = QP_TOPIC
                    +"Topic:"
                }
                select {
                    classes = selectClasses
                    name = QP_TOPIC
                    id = QP_TOPIC
                    option {
                        value = TOPIC_PLACEHOLDER
                        selected = true
                        +TOPIC_PLACEHOLDER
                    }
                    getAllTopics().forEach { topic ->
                        option {
                            value = topic
                            +topic
                        }
                    }
                }

                label(classes = labelClasses.joinToString(separator = " ")) {
                    htmlFor = QP_LANGUAGE
                    +"Language:"
                }
                select {
                    classes = selectClasses
                    name = QP_LANGUAGE
                    id = QP_LANGUAGE
                    option {
                        value = LANGUAGE_PLACEHOLDER
                        selected = true
                        +LANGUAGE_PLACEHOLDER
                    }
                    getAllLanguages().forEach { language ->
                        option {
                            value = language
                            +language
                        }
                    }
                }

                label(classes = labelClasses.joinToString(separator = " ")) {
                    htmlFor = QP_ORDER_BY
                    +"Order by:"
                }
                select {
                    classes = selectClasses
                    name = QP_ORDER_BY
                    id = QP_ORDER_BY
                    OrderBy.entries.forEach {
                        option {
                            value = it.name
                            +it.name
                        }
                    }
                }

                input(type = InputType.checkBox, name = QP_DIR) {
                    id = QP_DIR
                    classes = checkboxClasses
                    value = "desc"
                }
                label(classes = labelClasses.joinToString(separator = " ")) {
                    htmlFor = QP_DIR
                    +"Descending"
                }
            }

            button(type = ButtonType.submit) {
                classes = setOf("bg-gray-700", "text-white", "rounded", "border", "p-2", "mt-4", "text-xl")
                div(classes = "flex items-center") {
                    +"Search "
                    span("htmx-indicator ml-2") {
                        img(src = "/resources/bars.svg", alt = "Searching...")
                    }
                }
            }
        }
    }
}
