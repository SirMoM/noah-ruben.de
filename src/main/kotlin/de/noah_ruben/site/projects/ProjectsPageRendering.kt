@file:Suppress("ktlint:standard:no-wildcard-imports")

package de.noah_ruben.site.projects

import de.noah_ruben.data.Cache
import de.noah_ruben.data.Cache.getAllLanguages
import de.noah_ruben.data.Cache.getAllTopics
import de.noah_ruben.data.model.Project
import de.noah_ruben.misc.CssClasses.CONTENT_CONTAINER
import de.noah_ruben.misc.CssClasses.PAGE_BASE
import de.noah_ruben.misc.CssClasses.ProjectPage.EMPTY_STATE
import de.noah_ruben.misc.CssClasses.ProjectPage.EMPTY_STATE_MESSAGE
import de.noah_ruben.misc.CssClasses.ProjectPage.EMPTY_STATE_TITLE
import de.noah_ruben.misc.CssClasses.ProjectPage.META_DETAIL_LABEL
import de.noah_ruben.misc.CssClasses.ProjectPage.META_DETAIL_ROW
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_ACTION_LINK
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_ARGUMENT_CONTROL
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_ARGUMENT_FIELD
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_ARGUMENT_FLAG
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_ARGUMENT_SELECT
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_CARD
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_CARD_ACTIONS
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_CARD_CONTENT
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_CARD_DESCRIPTION
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_CARD_FOOTER
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_CARD_HEADER
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_CARD_INDEX
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_CARD_META
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_CARD_TITLE
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_COMMAND_CONTROL_ROW
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_COMMAND_PREVIEW
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_COMMAND_QUERY_ROW
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_DIRECTION_BUTTON
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_FILTER_FORM
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_RESULTS_BAR
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_RESULTS_LIST
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_RESULTS_SUMMARY
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_RESULTS_SUMMARY_TEXT
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_SHELL
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_TOPIC_ADD_BUTTON
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_TOPIC_ADD_PANEL
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_TOPIC_ADD_PANEL_EMPTY
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_TOPIC_ADD_TRIGGER
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_TOPIC_COMMA
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_TOPIC_CONTROL
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_TOPIC_DROPDOWN
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_TOPIC_EMPTY_TRIGGER
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_TOPIC_EMPTY_VALUE
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_TOPIC_PICKER
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_TOPIC_SELECTED_VALUE
import de.noah_ruben.misc.CssClasses.ProjectPage.RESET_BUTTON
import de.noah_ruben.misc.CssClasses.ProjectPage.TAGS_LIST
import de.noah_ruben.misc.CssClasses.ProjectPage.TAG_ITEM
import de.noah_ruben.misc.CssClasses.ProjectPage.TOPICS_LIST
import de.noah_ruben.misc.CssClasses.ProjectPage.TOPIC_TAG
import de.noah_ruben.misc.CssClasses.Shared.SCREEN_READER_ONLY
import de.noah_ruben.misc.HX_INCLUDE
import de.noah_ruben.misc.HX_INDICATOR
import de.noah_ruben.misc.HX_POST
import de.noah_ruben.misc.HX_SWAP
import de.noah_ruben.misc.HX_TARGET
import de.noah_ruben.misc.HX_TRIGGER
import de.noah_ruben.misc.HX_VALS
import de.noah_ruben.misc.colorFromString
import de.noah_ruben.misc.hxIndicator
import de.noah_ruben.misc.hxPost
import de.noah_ruben.misc.hxSwap
import de.noah_ruben.misc.hxTarget
import de.noah_ruben.misc.hxTrigger
import de.noah_ruben.misc.hxVals
import de.noah_ruben.site.*
import kotlinx.html.*
import kotlinx.html.CommonAttributeGroupFacade

internal const val DEFAULT_PROJECTS_COMMAND = "noahruben projects"
internal const val COMMAND_PREVIEW_ID = "projects-command-preview"
private const val RESULTS_BAR_ID = "projects-results-bar"
private const val RESULTS_SUMMARY_ID = "projects-results-summary"
private const val EMPTY_DESCRIPTION_PLACEHOLDER = "\u00A0"
private const val SEARCH_FORM_ID = "search"

fun HTML.projectsPage() {
    head {
        defaultHeader()
    }
    defaultBody {
        id = "body"
        classes = setOf(PAGE_BASE)
        themeToggleButton()
        projectsPageBody()
    }
}

fun BODY.projectsPageBody() {
    div(classes = CONTENT_CONTAINER) {
        id = SEARCH_REPLACE
        projectsShell(
            projects = Cache.getProjects(),
            searchParameters = SearchParameters.defaults(),
        )
        commandLineEmulation()
    }
}

fun FlowContent.projectsShell(
    projects: List<Project>,
    searchParameters: SearchParameters = SearchParameters.defaults(),
    errorMessage: String? = null,
) {
    div(classes = PROJECT_SHELL) {
        projectsFilterForm(searchParameters)
        projectsResultsBar(projects)

        if (errorMessage != null) {
            div(classes = EMPTY_STATE) {
                h3(classes = EMPTY_STATE_TITLE) { +"error" }
                p(classes = EMPTY_STATE_MESSAGE) { +errorMessage }
            }
        }

        projectList(projects, searchParameters)
    }
}

private fun FlowContent.projectsFilterForm(searchParameters: SearchParameters) {
    form(action = SEARCH_PATH, method = FormMethod.post, classes = PROJECT_FILTER_FORM) {
        id = SEARCH_FORM_ID
        hxPost(SEARCH_PATH)
        hxTarget("#$SEARCH_REPLACE")
        hxSwap("outerHTML")
        hxIndicator("#spinner")
        hxTrigger("submit, change from:select delay:100ms, input changed delay:500ms from:#mainSearch, search from:#mainSearch")

        input(type = InputType.hidden, name = QP_WITH_SEARCHBAR) {
            value = true.toString()
        }

        input(type = InputType.hidden, name = QP_DIR) {
            value = if (searchParameters.descending) "desc" else ""
        }

        searchParameters.topics.forEach { topic ->
            input(type = InputType.hidden, name = QP_TOPIC) {
                value = topic
            }
        }

        div(classes = PROJECT_COMMAND_PREVIEW) {
            id = COMMAND_PREVIEW_ID
            desktopProjectsCommandHeader()
            mobileProjectsFilterRows(searchParameters)
        }
    }
}

private fun FlowContent.projectsResultsBar(projects: List<Project>) {
    div(classes = PROJECT_RESULTS_BAR) {
        id = RESULTS_BAR_ID

        div(classes = PROJECT_RESULTS_SUMMARY) {
            id = RESULTS_SUMMARY_ID
            attributes["aria-live"] = "polite"
            span(classes = PROJECT_RESULTS_SUMMARY_TEXT) {
                +projects.resultsSummary()
            }
        }

        mobileProjectsResultsSummaryActions()
    }
}

fun FlowContent.projectList(
    projects: List<Project>,
    searchParameters: SearchParameters,
) {
    div(classes = PROJECT_RESULTS_LIST) {
        id = SEARCH_RESULTS
        if (projects.isEmpty()) {
            nothingFoundProjectTile(searchParameters)
        } else {
            projects.forEachIndexed { index, project ->
                projectTile(index + 1, project)
            }
        }
    }
}

fun FlowContent.projectTile(
    index: Int,
    project: Project,
) {
    with(project) {
        div(classes = PROJECT_CARD) {
            div(classes = PROJECT_CARD_CONTENT) {
                div(classes = PROJECT_CARD_HEADER) {
                    span(classes = PROJECT_CARD_INDEX) {
                        +"[${index.toString().padStart(2, '0')}]"
                    }
                    h3(classes = PROJECT_CARD_TITLE) {
                        +name
                    }
                    div(classes = PROJECT_CARD_META) {
                        span(classes = META_DETAIL_ROW) {
                            strong(classes = META_DETAIL_LABEL) { +"stars:" }
                            +stars.toString()
                        }
                        div(classes = META_DETAIL_ROW) {
                            strong(classes = META_DETAIL_LABEL) { +"rel:" }
                            +displayDate()
                        }
                    }
                }

                p(classes = PROJECT_CARD_DESCRIPTION) {
                    +description.ifBlank { EMPTY_DESCRIPTION_PLACEHOLDER }
                }

                div(classes = PROJECT_CARD_FOOTER) {
                    if (languages.isNotEmpty()) {
                        div(classes = TAGS_LIST) {
                            languages.forEach { lang ->
                                languageTag(lang)
                            }
                        }
                    }
                    if (topics.isNotEmpty()) {
                        div(classes = TOPICS_LIST) {
                            topics.forEach { topic ->
                                topicTag(topic)
                            }
                        }
                    }
                }
                div(classes = PROJECT_CARD_ACTIONS) {
                    a(href = githubLink, classes = PROJECT_ACTION_LINK) {
                        +"[GitHub]"
                    }
                    if (link.isNotBlank() && link != "#") {
                        a(href = link, classes = PROJECT_ACTION_LINK) {
                            +"[visit]"
                        }
                    }
                }
            }
        }
    }
}

fun FlowContent.languageTag(tag: String) {
    button(
        type = ButtonType.button,
        classes = TAG_ITEM,
    ) {
        style = chipStyle(tag)
        configureProjectActionRequest(projectActionVals(QP_SET_LANGUAGE, tag))

        +"lang:$tag"
    }
}

fun FlowContent.topicTag(topic: String) {
    button(
        type = ButtonType.button,
        classes = TOPIC_TAG,
    ) {
        style = chipStyle(topic)
        attributes["data-topic-tag"] = topic
        configureProjectActionRequest(projectActionVals(QP_TOGGLE_TOPIC, topic))

        +"topic:$topic"
    }
}

internal fun FlowContent.projectsTopicRow(searchParameters: SearchParameters) {
    div(classes = PROJECT_COMMAND_CONTROL_ROW) {
        attributes["data-filter-row"] = "topic"

        span(classes = SCREEN_READER_ONLY) {
            +"Topic filter"
        }
        div(classes = PROJECT_TOPIC_CONTROL) {
            id = "projects-topic-control"
            span(classes = PROJECT_ARGUMENT_FLAG) {
                +"--topic"
            }

            if (searchParameters.topics.isNotEmpty()) {
                searchParameters.topics.forEachIndexed { index, topic ->
                    if (index > 0) {
                        span(classes = PROJECT_TOPIC_COMMA) {
                            +","
                        }
                    }
                    selectedTopicButton(topic)
                }
            }

            details(classes = PROJECT_TOPIC_PICKER) {
                id = "projects-topic-picker"
                summary(
                    classes = if (searchParameters.topics.isEmpty()) {
                        PROJECT_TOPIC_EMPTY_TRIGGER
                    } else {
                        PROJECT_TOPIC_ADD_TRIGGER
                    },
                ) {
                    attributes["aria-label"] = if (searchParameters.topics.isEmpty()) {
                        "Choose topics"
                    } else {
                        "Add topic"
                    }
                    if (searchParameters.topics.isEmpty()) {
                        span(classes = PROJECT_TOPIC_EMPTY_VALUE) {
                            +"all"
                        }
                    } else {
                        +"(+)"
                    }
                }

                div(classes = PROJECT_TOPIC_DROPDOWN) {
                    id = "projects-topic-dropdown"
                    val availableTopics = getAllTopics().filterNot(searchParameters.topics::contains)
                    if (availableTopics.isEmpty()) {
                        div(classes = PROJECT_TOPIC_ADD_PANEL_EMPTY) {
                            +"all topics selected"
                        }
                    } else {
                        div(classes = PROJECT_TOPIC_ADD_PANEL) {
                            availableTopics.forEach { topic ->
                                topicAddOption(topic)
                            }
                        }
                    }
                }
            }
        }
    }
}

internal fun FlowContent.projectsLanguageRow(searchParameters: SearchParameters) {
    div(classes = PROJECT_COMMAND_CONTROL_ROW) {
        attributes["data-filter-row"] = "language"

        label(classes = SCREEN_READER_ONLY) {
            htmlFor = QP_LANGUAGE
            +"Language"
        }
        div(classes = PROJECT_ARGUMENT_CONTROL) {
            id = "projects-language-control"
            span(classes = PROJECT_ARGUMENT_FLAG) {
                +"--language"
            }
            select(classes = PROJECT_ARGUMENT_SELECT) {
                name = QP_LANGUAGE
                id = QP_LANGUAGE
                option {
                    value = LANGUAGE_PLACEHOLDER
                    selected = searchParameters.language == LANGUAGE_PLACEHOLDER
                    +"all"
                }
                getAllLanguages().forEach { language ->
                    option {
                        selected = searchParameters.language == language
                        value = language
                        +language.lowercase()
                    }
                }
            }
        }
    }
}

internal fun FlowContent.projectsSortRow(searchParameters: SearchParameters) {
    div(classes = PROJECT_COMMAND_CONTROL_ROW) {
        attributes["data-filter-row"] = "sort"

        label(classes = SCREEN_READER_ONLY) {
            htmlFor = QP_ORDER_BY
            +"Sort"
        }
        div(classes = PROJECT_ARGUMENT_CONTROL) {
            id = "projects-sort-control"
            span(classes = PROJECT_ARGUMENT_FLAG) {
                +"--sort"
            }
            select(classes = PROJECT_ARGUMENT_SELECT) {
                name = QP_ORDER_BY
                id = QP_ORDER_BY
                OrderBy.entries.forEach { orderBy ->
                    option {
                        value = orderBy.name
                        selected = searchParameters.orderBy == orderBy
                        +orderBy.name.lowercase()
                    }
                }
            }
        }

        label(classes = SCREEN_READER_ONLY) {
            htmlFor = "projects-dir-toggle"
            +"Sort direction"
        }
        directionToggle(searchParameters)
        desktopProjectsContinuation()
    }
}

internal fun FlowContent.projectsQueryRow(searchParameters: SearchParameters) {
    div(classes = PROJECT_COMMAND_QUERY_ROW) {
        attributes["data-filter-row"] = "query"

        label(classes = SCREEN_READER_ONLY) {
            htmlFor = "mainSearch"
            +"Query"
        }
        div(classes = PROJECT_ARGUMENT_CONTROL) {
            id = "projects-query-control"
            span(classes = PROJECT_ARGUMENT_FLAG) {
                +"--query"
            }
            input(
                InputType.text,
                name = QP_QUERY,
                classes = PROJECT_ARGUMENT_FIELD,
            ) {
                autoFocus = true
                id = "mainSearch"
                placeholder = "filter projects"
                value = searchParameters.query
            }
        }
    }
}

internal fun FlowContent.resetFiltersButton(
    context: String,
    classes: String = RESET_BUTTON,
) {
    button(type = ButtonType.button, classes = classes) {
        attributes["data-reset-context"] = context
        hxPost(SEARCH_PATH)
        hxTarget("#$SEARCH_REPLACE")
        hxSwap("outerHTML")
        hxIndicator("#spinner")
        hxTrigger("click")
        attributes["hx-params"] = "none"
        hxVals(
            """{"$QP_QUERY": "", "$QP_LANGUAGE": "$LANGUAGE_PLACEHOLDER", "$QP_ORDER_BY": "${OrderBy.Relevance.name}", "$QP_DIR": "", "$QP_WITH_SEARCHBAR": true}""",
        )
        +"[reset filters]"
    }
}

fun FlowContent.nothingFoundProjectTile(searchParameters: SearchParameters) {
    div(classes = EMPTY_STATE) {
        h3(classes = EMPTY_STATE_TITLE) {
            +"Nothing found"
        }

        p(classes = EMPTY_STATE_MESSAGE) {
            +"No projects matched the current filter state."
        }

        p(classes = EMPTY_STATE_MESSAGE) {
            +"Current command: ${searchParameters.commandPreview()}"
        }

        resetFiltersButton(context = "empty-state")
    }
}

private fun chipStyle(value: String): String {
    val accent = value.colorFromString()
    return "border-color: currentColor; background-color: #${accent}26"
}

private fun FlowContent.selectedTopicButton(topic: String) {
    button(
        type = ButtonType.button,
        classes = PROJECT_TOPIC_SELECTED_VALUE,
    ) {
        attributes["aria-label"] = "Remove topic $topic"
        attributes["data-selected-topic"] = topic
        configureProjectActionRequest(projectActionVals(QP_REMOVE_TOPIC, topic))

        +topic
    }
}

private fun FlowContent.topicAddOption(topic: String) {
    button(
        type = ButtonType.button,
        classes = PROJECT_TOPIC_ADD_BUTTON,
    ) {
        attributes["data-topic-option"] = topic
        configureProjectActionRequest(projectActionVals(QP_ADD_TOPIC, topic))

        +topic
    }
}

private fun FlowContent.directionToggle(searchParameters: SearchParameters) {
    button(type = ButtonType.button, classes = PROJECT_DIRECTION_BUTTON) {
        id = "projects-dir-toggle"
        attributes["aria-label"] = "Sort direction"
        configureProjectActionRequest("""{"$QP_TOGGLE_DIR": true}""")

        +if (searchParameters.descending) "--desc" else "--asc"
    }
}

private fun CommonAttributeGroupFacade.configureProjectActionRequest(vals: String) {
    attributes[HX_POST] = SEARCH_PATH
    attributes[HX_INCLUDE] = "#$SEARCH_FORM_ID"
    attributes[HX_TARGET] = "#$SEARCH_REPLACE"
    attributes[HX_SWAP] = "outerHTML"
    attributes[HX_INDICATOR] = "#spinner"
    attributes[HX_TRIGGER] = "click"
    attributes[HX_VALS] = vals
}

private fun projectActionVals(param: String, value: String): String = """{"$param": "${value.escapeJsonValue()}"}"""

private fun String.escapeJsonValue(): String = replace("\\", "\\\\").replace("\"", "\\\"")

private fun SearchParameters.commandPreview(): String {
    val parts = mutableListOf(DEFAULT_PROJECTS_COMMAND)

    if (query.isNotBlank()) {
        parts += "--query \"${query.escapeShellText()}\""
    }
    if (topics.isNotEmpty()) {
        parts += "--topic ${topics.joinToString(", ") { it.escapeShellText() }}"
    }
    if (language != LANGUAGE_PLACEHOLDER) {
        parts += "--language \"${language.escapeShellText()}\""
    }
    if (orderBy != OrderBy.Relevance) {
        parts += "--sort ${orderBy.name.lowercase()}"
    }
    if (descending) {
        parts += "--desc"
    }

    return parts.joinToString(" ")
}

private fun String.escapeShellText(): String = replace("\"", "\\\"")

private fun List<Project>.resultsSummary(): String {
    val noun = if (size == 1) "result" else "results"
    return "$size $noun"
}

private fun Project.displayDate(): String = releases.substringBefore("T").ifBlank {
    created.toLocalDate().toString()
}
