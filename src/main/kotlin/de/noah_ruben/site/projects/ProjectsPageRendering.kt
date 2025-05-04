@file:Suppress("ktlint:standard:no-wildcard-imports")

package de.noah_ruben.site.projects

import de.noah_ruben.data.Cache
import de.noah_ruben.data.Cache.getAllLanguages
import de.noah_ruben.data.Cache.getAllTopics
import de.noah_ruben.data.model.Project
import de.noah_ruben.misc.*
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

    div(classes = css { container().mxAuto().p4() }) {
        h1(classes = css { text2xl().fontBold().mb4() }) { +"Projects" }
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
        div(classes = css { projectTileClasses() }) {
            div {
                div(classes = css { fontBold().textXl().mb2() }) {
                    +name
                }
                p(classes = css { textGray700().textBase() }) {
                    +description
                }
                div(classes = css { metadataClasses() }) {
                    +"Released at: $releases"
                }
                div(classes = css { metadataClassesMt4() }) {
                    +"Stars: "
                    +stars.toString()
                }
                if (topics.isNotEmpty()) {
                    div(classes = css { metadataClasses() }) {
                        +"Topics: "
                        +topics.joinToString(", ")
                    }
                }
                div(classes = css { metadataClasses() }) {
                    for (lang in languages) {
                        languageTag(lang)
                    }
                }
            }
            a(href = githubLink, classes = css { actionButtonClasses().mr2() }) {
                +"GitHub"
            }
            if (link.isNotBlank() && link != "#") { // Example check
                a(href = link, classes = css { actionButtonClasses() }) {
                    +"Visit"
                }
            }
        }
    }
}

fun FlowContent.languageTag(tag: String) {
    val cssBuilder = css {
        languageTagBaseClasses()
        custom("bg-[#${tag.colorFromString()}]")
        custom("text-[#${tag.colorFromString().invertedFromString()}]")
    }

    a(classes = cssBuilder) {
        +tag
    }
}

private val borderGrey400 = borderGray("400")

fun FlowContent.mainSearchBar() {
    val inputClasses = cssSet { inputClasses() }
    val searchBoxClasses = cssSet { searchBoxClasses() }
    val selectClasses = cssSet { selectClasses() }
    val checkboxClasses = cssSet { checkboxClasses() }
    val labelClasses = cssSet { labelClasses() }

    div {
        classes = searchBoxClasses
        span(classes = css { custom("htmx-indicator") }) {
            id = "spinner"
            img(src = "/resources/bars.svg", alt = "Searching...") // Use alt attribute
            +"Searching..."
        }

        form(action = SEARCH_PATH, method = FormMethod.post) {
            classes = cssSet { custom("formControl").justifyStart().flexWrap() }
            hxPost(SEARCH_PATH)
            hxTarget("#search-results")
            hxIndicator("#spinner")
            hxTrigger("submit, change from:select, change from:input[type='checkbox'] delay:100ms, input from:input[type='text'] changed delay:500ms")

            div(classes = css { flex().flexCol() }) {
                label(classes = css { order0() }) {
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
                label(classes = css { labelClasses() }) {
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

                label(classes = css { labelClasses() }) {
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

                label(classes = css { labelClasses() }) {
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
                label(classes = css { labelClasses() }) {
                    htmlFor = QP_DIR
                    +"Descending"
                }
            }

            button(type = ButtonType.submit, classes = css { buttonClasses() }) {
                div(classes = css { flex().itemsCenter() }) {
                    +"Search "
                    span(classes = css { custom("htmx-indicator").ml2() }) {
                        img(src = "/resources/bars.svg", alt = "Searching...")
                    }
                }
            }
        }
    }
}
