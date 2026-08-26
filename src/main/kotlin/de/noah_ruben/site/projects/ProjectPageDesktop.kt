package de.noah_ruben.site.projects

import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_COMMAND_ACTIONS
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_COMMAND_CONTINUATION
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_COMMAND_LINE
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_COMMAND_PROMPT
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_COMMAND_TEXT
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_HEADER_RESET_BUTTON
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_SHELL_STATUS
import de.noah_ruben.misc.CssClasses.Shared.HTMX_INDICATOR
import kotlinx.html.FlowContent
import kotlinx.html.code
import kotlinx.html.div
import kotlinx.html.id
import kotlinx.html.img
import kotlinx.html.span

internal fun FlowContent.desktopProjectsCommandHeader() {
    div(classes = PROJECT_COMMAND_LINE) {
        span(classes = PROJECT_COMMAND_PROMPT) { +">>" }
        code(classes = PROJECT_COMMAND_TEXT) {
            +DEFAULT_PROJECTS_COMMAND
        }
        desktopProjectsContinuation()
        div(classes = PROJECT_COMMAND_ACTIONS) {
            resetFiltersButton(context = "desktop", classes = PROJECT_HEADER_RESET_BUTTON)
            projectsShellStatus()
        }
    }
}

internal fun FlowContent.desktopProjectsContinuation() {
    span(classes = PROJECT_COMMAND_CONTINUATION) {
        +"\\"
    }
}

private fun FlowContent.projectsShellStatus() {
    span(classes = PROJECT_SHELL_STATUS) {
        span(classes = HTMX_INDICATOR) {
            id = "spinner"
            img(src = "/resources/bars.svg", alt = "")
            +" syncing"
        }
    }
}
