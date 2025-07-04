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
    h1(
        classes = CssClasses.PAGE_TITLE,
    ) { +"> Projects" }
    div(
        classes = CssClasses.CONTENT_CONTAINER,
    ) {
        id = "search-replace"
        mainSearchBar()
        br()
        projectList(projects)
        commandLineEmulation()
    }
}

fun FlowContent.projectList(
    projects: List<Project>,
) {
    div(classes = CssClasses.CONTENT_CONTAINER) {
        id = SEARCH_RESULTS
        projects.forEach {
            projectTile(it)
        }
    }
}

fun FlowContent.projectTile(project: Project) {
    with(project) {
        // Main card container
        div(
            classes = CssClasses.ProjectPage.PROJECT_CARD,
        ) {
            div(
                classes = CssClasses.ProjectPage.PROJECT_CARD_CONTENT,
            ) {
                // Project Name
                h3(
                    classes = CssClasses.ProjectPage.PROJECT_CARD_TITLE,
                ) {
                    +name
                }

                // Description
                p(
                    classes = CssClasses.ProjectPage.PROJECT_CARD_DESCRIPTION,
                ) {
                    +description
                }

                div(
                    classes = CssClasses.ProjectPage.PROJECT_CARD_META,
                ) {
                    div(
                        classes = CssClasses.ProjectPage.META_DETAIL_ROW,
                    ) {
                        strong(
                            classes = CssClasses.ProjectPage.META_DETAIL_LABEL,
                        ) { +"Stars: " }
                        +stars.toString()
                    }
                    if (releases.isNotBlank()) {
                        div(classes = CssClasses.ProjectPage.META_DETAIL_ROW) {
                            strong(
                                classes = CssClasses.ProjectPage.META_DETAIL_LABEL,
                            ) { +"Released: " }
                            +releases
                        }
                    }
                    if (topics.isNotEmpty()) {
                        div(classes = CssClasses.ProjectPage.META_DETAIL_ROW) {
                            strong(
                                classes = CssClasses.ProjectPage.META_DETAIL_LABEL,
                            ) { +"Topics: " }
                            span { +topics.joinToString(", ") }
                        }
                    }
                }

                // Languages/Tags Section
                if (languages.isNotEmpty()) {
                    div(
                        classes = CssClasses.ProjectPage.TAGS_LIST,
                    ) {
                        languages.forEach { lang ->
                            languageTag(lang)
                        }
                    }
                }
            }

            // Action Buttons Section (at the bottom of the card)
            div(
                classes = CssClasses.ProjectPage.PROJECT_CARD_FOOTER,
            ) {
                a(href = githubLink, classes = CssClasses.ProjectPage.PROJECT_ACTION_LINK) {
                    +"GitHub"
                }
                if (link.isNotBlank() && link != "#") {
                    a(href = link, classes = CssClasses.ProjectPage.PROJECT_ACTION_LINK) {
                        +"Visit"
                    }
                }
            }
        }
    }
}

fun FlowContent.languageTag(tag: String) {
    div(
        classes = CssClasses.ProjectPage.TAG_ITEM,
    ) {
        style = "background-color: #${tag.colorFromString()}; color: #${tag.colorFromString().invertedFromString()}"
        hxPost(SEARCH_PATH)
        hxTarget("#search-replace")
        hxSwap("outerHTML")
        hxIndicator("#spinner")
        hxTrigger("click")
        hxInclude("#search")
        hxVals("""{"$QP_LANGUAGE": "$tag", "$QP_WITH_SEARCHBAR": true}""")

        +tag
    }
}

fun FlowContent.mainSearchBar(searchParameters: SearchParameters = SearchParameters.defaults()) {
    div {
        span(classes = "htmx-indicator") {
            id = "spinner"
            img(src = "/resources/bars.svg", alt = "Searching...")
            +"Searching..."
        }

        form(action = SEARCH_PATH, method = FormMethod.post) {
            id = "search"
            hxPost(SEARCH_PATH)
            hxTarget("#search-results")
            hxIndicator("#spinner")
            hxTrigger("submit, change from:select, change from:input[type='checkbox'] delay:100ms, input from:input[type='text'] changed delay:500ms")

            div(classes = CssClasses.Form.FORM_GROUP) {
                label(classes = CssClasses.Form.FORM_LABEL) {
                    htmlFor = "mainSearch"
                    +"Search:"
                }
                input(
                    InputType.text,
                    name = QP_QUERY,
                    classes = "${CssClasses.Form.FORM_INPUT_BASE} ${CssClasses.Form.FORM_INPUT_TEXT} ${CssClasses.MB_4}",
                ) {
                    autoFocus = true
                    id = "mainSearch"
                    placeholder = "Search"
                    value = searchParameters.query
                }
            }

            // Grouping filter controls for better layout potential
            div(
                classes = CssClasses.Form.FILTER_CONTROLS_LAYOUT,
            ) {
                div(classes = CssClasses.Form.FILTER_ITEM_LAYOUT) {
                    label(classes = CssClasses.Form.FORM_LABEL) {
                        htmlFor = QP_TOPIC
                        +"Topic:"
                    }
                    select(classes = CssClasses.Form.FORM_INPUT_BASE) {
                        name = QP_TOPIC
                        id = QP_TOPIC
                        option {
                            value = TOPIC_PLACEHOLDER
                            selected = searchParameters.topic == TOPIC_PLACEHOLDER
                            +TOPIC_PLACEHOLDER
                        }
                        getAllTopics().forEach { topic ->
                            option {
                                value = topic
                                +topic
                                selected = searchParameters.topic == topic
                            }
                        }
                    }
                }

                div(classes = CssClasses.Form.FILTER_ITEM_LAYOUT) {
                    label(classes = CssClasses.Form.FORM_LABEL) {
                        htmlFor = QP_LANGUAGE
                        +"Language:"
                    }
                    select(classes = CssClasses.Form.FORM_INPUT_BASE) {
                        name = QP_LANGUAGE
                        id = QP_LANGUAGE
                        option {
                            value = LANGUAGE_PLACEHOLDER
                            selected = searchParameters.language == LANGUAGE_PLACEHOLDER
                            +LANGUAGE_PLACEHOLDER
                        }
                        getAllLanguages().forEach { language ->
                            option {
                                selected = searchParameters.language == language
                                value = language
                                +language
                            }
                        }
                    }
                }

                div(classes = CssClasses.Form.FILTER_ITEM_LAYOUT) {
                    label(classes = CssClasses.Form.FORM_LABEL) {
                        htmlFor = QP_ORDER_BY
                        +"Order by:"
                    }
                    select(classes = CssClasses.Form.FORM_INPUT_BASE) {
                        name = QP_ORDER_BY
                        id = QP_ORDER_BY
                        OrderBy.entries.forEach {
                            option {
                                value = it.name
                                +it.name
                            }
                        }
                    }
                }

                div(
                    classes = CssClasses.Form.FORM_CHECKBOX_GROUP,
                ) {
                    input(
                        type = InputType.checkBox,
                        name = QP_DIR,
                        classes = CssClasses.Form.FORM_CHECKBOX,
                    ) {
                        id = QP_DIR
                        value = "desc"
                    }
                    label(classes = CssClasses.Form.FORM_LABEL) {
                        htmlFor = QP_DIR
                        +"Descending"
                    }
                }
            }

            button(type = ButtonType.submit, classes = CssClasses.Form.SUBMIT_BUTTON) {
                div(
                    classes = CssClasses.Form.LOADING_SPINNER,
                ) {
                    +"Search "
                    span(
                        classes = "htmx-indicator ml-2",
                    ) {
                        img(src = "/resources/bars.svg", alt = "Searching...")
                    }
                }
            }
        }
    }
}
