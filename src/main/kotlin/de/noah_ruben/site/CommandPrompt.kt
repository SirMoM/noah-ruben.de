package de.noah_ruben.site

import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_COMMAND_LINE
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_COMMAND_PROMPT
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_COMMAND_TEXT
import kotlinx.html.FlowContent
import kotlinx.html.code
import kotlinx.html.div
import kotlinx.html.span

fun FlowContent.commandPrompt(
    command: String,
    containerRole: String? = null,
    textRole: String? = null,
) {
    div(classes = PROJECT_COMMAND_LINE) {
        if (containerRole != null) {
            attributes["data-role"] = containerRole
        }

        span(classes = PROJECT_COMMAND_PROMPT) {
            +">>"
        }

        code(classes = PROJECT_COMMAND_TEXT) {
            if (textRole != null) {
                attributes["data-role"] = textRole
            }
            +command
        }
    }
}
