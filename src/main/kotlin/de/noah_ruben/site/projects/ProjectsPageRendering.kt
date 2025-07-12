@file:Suppress("ktlint:standard:no-wildcard-imports")

package de.noah_ruben.site.projects

import de.noah_ruben.data.Cache
import de.noah_ruben.data.Cache.getAllLanguages
import de.noah_ruben.data.Cache.getAllTopics
import de.noah_ruben.data.model.Project
import de.noah_ruben.misc.CssClasses.CONTENT_CONTAINER
import de.noah_ruben.misc.CssClasses.Form.FILTER_CONTROLS_LAYOUT
import de.noah_ruben.misc.CssClasses.Form.FILTER_ITEM_LAYOUT
import de.noah_ruben.misc.CssClasses.Form.FORM_CHECKBOX
import de.noah_ruben.misc.CssClasses.Form.FORM_CHECKBOX_GROUP
import de.noah_ruben.misc.CssClasses.Form.FORM_GROUP
import de.noah_ruben.misc.CssClasses.Form.FORM_INPUT_BASE
import de.noah_ruben.misc.CssClasses.Form.FORM_INPUT_TEXT
import de.noah_ruben.misc.CssClasses.Form.FORM_LABEL
import de.noah_ruben.misc.CssClasses.Form.LOADING_SPINNER
import de.noah_ruben.misc.CssClasses.Form.SUBMIT_BUTTON
import de.noah_ruben.misc.CssClasses.MB_4
import de.noah_ruben.misc.CssClasses.PAGE_TITLE
import de.noah_ruben.misc.CssClasses.ProjectPage.META_DETAIL_LABEL
import de.noah_ruben.misc.CssClasses.ProjectPage.META_DETAIL_ROW
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_ACTION_LINK
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_CARD
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_CARD_CONTENT
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_CARD_DESCRIPTION
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_CARD_FOOTER
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_CARD_META
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_CARD_TITLE
import de.noah_ruben.misc.CssClasses.ProjectPage.RESET_BUTTON
import de.noah_ruben.misc.CssClasses.ProjectPage.TAGS_LIST
import de.noah_ruben.misc.CssClasses.ProjectPage.TAG_ITEM
import de.noah_ruben.misc.colorFromString
import de.noah_ruben.misc.hxInclude
import de.noah_ruben.misc.hxIndicator
import de.noah_ruben.misc.hxPost
import de.noah_ruben.misc.hxSwap
import de.noah_ruben.misc.hxTarget
import de.noah_ruben.misc.hxTrigger
import de.noah_ruben.misc.hxVals
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
    h1(
        classes = PAGE_TITLE,
    ) { +"> Projects" }
    div(
        classes = CONTENT_CONTAINER,
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
    div {
        id = SEARCH_RESULTS
        projects.forEach {
            projectTile(it)
        }
    }
}

fun FlowContent.projectTile(project: Project) {
    with(project) {
        div(
            classes = PROJECT_CARD,
        ) {
            div(
                classes = PROJECT_CARD_CONTENT,
            ) {
                h3(
                    classes = PROJECT_CARD_TITLE,
                ) {
                    +name
                }

                p(
                    classes = PROJECT_CARD_DESCRIPTION,
                ) {
                    +description
                }

                div(
                    classes = PROJECT_CARD_META,
                ) {
                    div(
                        classes = META_DETAIL_ROW,
                    ) {
                        strong(
                            classes = META_DETAIL_LABEL,
                        ) { +"Stars: " }
                        +stars.toString()
                    }
                    if (releases.isNotBlank()) {
                        div(classes = META_DETAIL_ROW) {
                            strong(
                                classes = META_DETAIL_LABEL,
                            ) { +"Released: " }
                            +releases
                        }
                    }
                    if (topics.isNotEmpty()) {
                        div(classes = META_DETAIL_ROW) {
                            strong(
                                classes = META_DETAIL_LABEL,
                            ) { +"Topics: " }
                            div(classes = "topics-list") {
                                topics.forEach { topic ->
                                    topicTag(topic)
                                }
                            }
                        }
                    }
                }

                if (languages.isNotEmpty()) {
                    div(
                        classes = TAGS_LIST,
                    ) {
                        languages.forEach { lang ->
                            languageTag(lang)
                        }
                    }
                }
            }

            div(
                classes = PROJECT_CARD_FOOTER,
            ) {
                a(href = githubLink, classes = PROJECT_ACTION_LINK) {
                    +"GitHub"
                }
                if (link.isNotBlank() && link != "#") {
                    a(href = link, classes = PROJECT_ACTION_LINK) {
                        +"Visit"
                    }
                }
            }
        }
    }
}

fun FlowContent.languageTag(tag: String) {
    div(
        classes = TAG_ITEM,
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

fun FlowContent.topicTag(topic: String) {
    span(
        classes = "topic-tag",
    ) {
        style = "color: #${topic.colorFromString()}; border-bottom: 1px solid #${topic.colorFromString()}"
        hxPost(SEARCH_PATH)
        hxTarget("#search-replace")
        hxSwap("outerHTML")
        hxIndicator("#spinner")
        hxTrigger("click")
        hxInclude("#search")
        hxVals("""{"$QP_TOPIC": "$topic", "$QP_WITH_SEARCHBAR": true}""")

        +topic
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

            div(classes = FORM_GROUP) {
                label(classes = FORM_LABEL) {
                    htmlFor = "mainSearch"
                    +"Search:"
                }
                input(
                    InputType.text,
                    name = QP_QUERY,
                    classes = "$FORM_INPUT_BASE $FORM_INPUT_TEXT $MB_4",
                ) {
                    autoFocus = true
                    id = "mainSearch"
                    placeholder = "Search"
                    value = searchParameters.query
                }
            }

            // Grouping filter controls for better layout potential
            div(
                classes = FILTER_CONTROLS_LAYOUT,
            ) {
                div(classes = FILTER_ITEM_LAYOUT) {
                    label(classes = FORM_LABEL) {
                        htmlFor = QP_TOPIC
                        +"Topic:"
                    }
                    select(classes = FORM_INPUT_BASE) {
                        name = QP_TOPIC
                        id = QP_TOPIC
                        option {
                            selected = searchParameters.topic == TOPIC_PLACEHOLDER
                            value = TOPIC_PLACEHOLDER
                            +TOPIC_PLACEHOLDER
                        }
                        getAllTopics().forEach { topic ->
                            option {
                                selected = searchParameters.topic == topic
                                value = topic
                                +topic
                            }
                        }
                    }
                }

                div(classes = FILTER_ITEM_LAYOUT) {
                    label(classes = FORM_LABEL) {
                        htmlFor = QP_LANGUAGE
                        +"Language:"
                    }
                    select(classes = FORM_INPUT_BASE) {
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

                div(classes = FILTER_ITEM_LAYOUT) {
                    label(classes = FORM_LABEL) {
                        htmlFor = QP_ORDER_BY
                        +"Order by:"
                    }
                    select(classes = FORM_INPUT_BASE) {
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
                    classes = FORM_CHECKBOX_GROUP,
                ) {
                    input(
                        type = InputType.checkBox,
                        name = QP_DIR,
                        classes = FORM_CHECKBOX,
                    ) {
                        id = QP_DIR
                        value = "desc"
                    }
                    label(classes = FORM_LABEL) {
                        htmlFor = QP_DIR
                        +"Descending"
                    }
                }
            }

            button(type = ButtonType.submit, classes = SUBMIT_BUTTON) {
                div(
                    classes = LOADING_SPINNER,
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

fun FlowContent.nothingFoundProjectTile() {
    div(classes = PROJECT_CARD) {
        div(classes = PROJECT_CARD_CONTENT) {
            h3(classes = PROJECT_CARD_TITLE) {
                +"Nothing Found"
            }

            p(classes = PROJECT_CARD_DESCRIPTION) {
                +"No projects match your search criteria."
            }
        }

        div(classes = PROJECT_CARD_FOOTER) {
            input(type = InputType.button, classes = RESET_BUTTON) {
                hxPost(SEARCH_PATH)
                hxTarget("#search-replace")
                hxSwap("outerHTML")
                hxIndicator("#spinner")
                hxTrigger("click")
                hxInclude("#search")
                hxVals("""{"$QP_QUERY": "", "$QP_LANGUAGE": "$LANGUAGE_PLACEHOLDER", "$QP_TOPIC": "$TOPIC_PLACEHOLDER", "$QP_WITH_SEARCHBAR": true}""")
                value = "Reset Search"
            }
        }
    }
}
