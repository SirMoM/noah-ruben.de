package de.noah_ruben.misc

import de.noah_ruben.misc.styles.LandingClasses
import de.noah_ruben.misc.styles.ProjectsClasses
import de.noah_ruben.misc.styles.SharedClasses
import de.noah_ruben.misc.styles.ThemeClasses

object CssClasses {
    const val PAGE_TITLE = ThemeClasses.PAGE_TITLE
    const val MB_8 = SharedClasses.MB_8
    const val MB_4 = SharedClasses.MB_4
    const val CONTENT_CONTAINER = ThemeClasses.CONTENT_CONTAINER
    const val LINK = ThemeClasses.LINK
    const val PAGE_BASE = ThemeClasses.PAGE_BASE

    object Shared {
        const val HELP_INDENT = SharedClasses.HELP_INDENT
        const val SUBPAGE_INDENT = SharedClasses.SUBPAGE_INDENT
        const val HTMX_INDICATOR = SharedClasses.HTMX_INDICATOR
        const val HTMX_INDICATOR_INLINE = SharedClasses.HTMX_INDICATOR_INLINE
        const val CLI_WRAPPER = SharedClasses.CLI_WRAPPER
        const val CLI_INPUT_FIELD = SharedClasses.CLI_INPUT_FIELD
        const val ERROR_MESSAGE_BOX = SharedClasses.ERROR_MESSAGE_BOX
    }

    object LandingPage {
        const val PROFILE_CONTAINER = LandingClasses.PROFILE_CONTAINER
        const val PROFILE_PICTURE = LandingClasses.PROFILE_PICTURE
        const val PROFILE_DETAILS_CONTAINER = LandingClasses.PROFILE_DETAILS_CONTAINER
        const val COLOR_GRID = LandingClasses.COLOR_GRID

        // Catppuccin bg-ctp-* classes — resolve to Latte in light mode, Mocha in dark mode via the plugin
        val COLORS = listOf(
            "bg-ctp-rosewater", "bg-ctp-flamingo", "bg-ctp-pink", "bg-ctp-mauve",
            "bg-ctp-red", "bg-ctp-maroon", "bg-ctp-peach", "bg-ctp-yellow",
            "bg-ctp-green", "bg-ctp-teal", "bg-ctp-sky", "bg-ctp-sapphire",
            "bg-ctp-blue", "bg-ctp-lavender", "bg-ctp-overlay0", "bg-ctp-crust",
        )
        const val ABOUT_ME = LandingClasses.ABOUT_ME
    }

    object ProjectPage {
        const val PROJECT_CARD = ProjectsClasses.PROJECT_CARD
        const val PROJECT_CARD_CONTENT = ProjectsClasses.PROJECT_CARD_CONTENT
        const val PROJECT_CARD_TITLE = ProjectsClasses.PROJECT_CARD_TITLE
        const val PROJECT_CARD_DESCRIPTION = ProjectsClasses.PROJECT_CARD_DESCRIPTION
        const val PROJECT_CARD_META = ProjectsClasses.PROJECT_CARD_META
        const val META_DETAIL_ROW = ProjectsClasses.META_DETAIL_ROW
        const val META_DETAIL_LABEL = ProjectsClasses.META_DETAIL_LABEL
        const val TAGS_LIST = ProjectsClasses.TAGS_LIST
        const val TAG_ITEM = ProjectsClasses.TAG_ITEM
        const val TOPIC_TAG = ProjectsClasses.TOPIC_TAG
        const val TOPICS_LIST = ProjectsClasses.TOPICS_LIST
        const val PROJECT_CARD_FOOTER = ProjectsClasses.PROJECT_CARD_FOOTER
        const val PROJECT_ACTION_LINK = ProjectsClasses.PROJECT_ACTION_LINK
        const val RESET_BUTTON = ProjectsClasses.RESET_BUTTON
        const val STAR_ICON = ProjectsClasses.STAR_ICON
    }

    object Form {
        const val FORM_GROUP = SharedClasses.FORM_GROUP
        const val FORM_FIELD = SharedClasses.FORM_FIELD
        const val FORM_LABEL = SharedClasses.FORM_LABEL
        const val FORM_INPUT_BASE = SharedClasses.FORM_INPUT_BASE
        const val FORM_INPUT_TEXT = SharedClasses.FORM_INPUT_TEXT
        const val FORM_INPUT_TEXT_WITH_MARGIN = SharedClasses.FORM_INPUT_TEXT_WITH_MARGIN
        const val FORM_CHECKBOX_GROUP = SharedClasses.FORM_CHECKBOX_GROUP
        const val FORM_CHECKBOX = SharedClasses.FORM_CHECKBOX
        const val SUBMIT_BUTTON = SharedClasses.SUBMIT_BUTTON
        const val LOADING_SPINNER = SharedClasses.LOADING_SPINNER
        const val FILTER_CONTROLS_LAYOUT = SharedClasses.FILTER_CONTROLS_LAYOUT
        const val FILTER_ITEM_LAYOUT = SharedClasses.FILTER_ITEM_LAYOUT
        const val TOGGLE_BUTTON = SharedClasses.TOGGLE_BUTTON
        const val TOGGLE_BUTTON_ICON = SharedClasses.TOGGLE_BUTTON_ICON
        const val TOGGLE_BUTTON_ICON_MOON = SharedClasses.TOGGLE_BUTTON_ICON_MOON
        const val TOGGLE_BUTTON_ICON_SUN = SharedClasses.TOGGLE_BUTTON_ICON_SUN
        const val TOGGLE_BUTTON_ICON_MOON_FULL = SharedClasses.TOGGLE_BUTTON_ICON_MOON_FULL
        const val TOGGLE_BUTTON_ICON_SUN_FULL = SharedClasses.TOGGLE_BUTTON_ICON_SUN_FULL
    }
}
