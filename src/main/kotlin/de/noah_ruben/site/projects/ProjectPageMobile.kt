package de.noah_ruben.site.projects

import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_COMMAND_CONTROLS
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_RESULTS_RESET_BUTTON
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_RESULTS_SUMMARY_ACTIONS
import kotlinx.html.FlowContent
import kotlinx.html.div

internal fun FlowContent.mobileProjectsFilterRows(searchParameters: SearchParameters) {
    div(classes = PROJECT_COMMAND_CONTROLS) {
        projectsTopicRow(searchParameters)
        projectsLanguageRow(searchParameters)
        projectsSortRow(searchParameters)
    }
    projectsQueryRow(searchParameters)
}

internal fun FlowContent.mobileProjectsResultsSummaryActions() {
    div(classes = PROJECT_RESULTS_SUMMARY_ACTIONS) {
        resetFiltersButton(context = "mobile", classes = PROJECT_RESULTS_RESET_BUTTON)
    }
}
