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
        const val HTMX_REQUEST = SharedClasses.HTMX_REQUEST
        const val SCREEN_READER_ONLY = SharedClasses.SCREEN_READER_ONLY
        const val CLI_WRAPPER = SharedClasses.CLI_WRAPPER
        const val CLI_INPUT_FIELD = SharedClasses.CLI_INPUT_FIELD
        const val ERROR_MESSAGE_BOX = SharedClasses.ERROR_MESSAGE_BOX
    }

    object LandingPage {
        const val PROFILE_CONTAINER = LandingClasses.PROFILE_CONTAINER
        const val PROFILE_PICTURE_IMAGE = LandingClasses.PROFILE_PICTURE_IMAGE
        const val PROFILE_DETAILS_CONTAINER = LandingClasses.PROFILE_DETAILS_CONTAINER

        // Catppuccin bg-ctp-* classes paired with their hex values for accent switching
        val COLORS_WITH_HEX = LandingClasses.COLORS_WITH_HEX

        const val ABOUT_ME = LandingClasses.ABOUT_ME
        const val PROFILE_LABEL = LandingClasses.PROFILE_LABEL
        const val PROFILE_HEADER = LandingClasses.PROFILE_HEADER
        const val SECTION_DIVIDER = LandingClasses.SECTION_DIVIDER
        const val PROFILE_DIVIDER = LandingClasses.PROFILE_DIVIDER
        const val PROMPT_ARROW = LandingClasses.PROMPT_ARROW
        const val LOCATION_TEXT = LandingClasses.LOCATION_TEXT
        const val COLOR_GRID_LATTE = LandingClasses.COLOR_GRID_LATTE
        const val SYSTEM_SUMMARY_HEADING = LandingClasses.SYSTEM_SUMMARY_HEADING
        val ABOUT_ME_LINE_COLORS = LandingClasses.ABOUT_ME_LINE_COLORS
    }

    object ProjectPage {
        const val PROJECT_SHELL = ProjectsClasses.PROJECT_SHELL
        const val PROJECT_SHELL_STATUS = ProjectsClasses.PROJECT_SHELL_STATUS
        const val PROJECT_COMMAND_PREVIEW = ProjectsClasses.PROJECT_COMMAND_PREVIEW
        const val PROJECT_COMMAND_LINE = ProjectsClasses.PROJECT_COMMAND_LINE
        const val PROJECT_COMMAND_ACTIONS = ProjectsClasses.PROJECT_COMMAND_ACTIONS
        const val PROJECT_COMMAND_CONTROLS = ProjectsClasses.PROJECT_COMMAND_CONTROLS
        const val PROJECT_COMMAND_CONTROL_ROW = ProjectsClasses.PROJECT_COMMAND_CONTROL_ROW
        const val PROJECT_COMMAND_QUERY_ROW = ProjectsClasses.PROJECT_COMMAND_QUERY_ROW
        const val PROJECT_COMMAND_CONTINUATION = ProjectsClasses.PROJECT_COMMAND_CONTINUATION
        const val PROJECT_COMMAND_PROMPT = ProjectsClasses.PROJECT_COMMAND_PROMPT
        const val PROJECT_COMMAND_TEXT = ProjectsClasses.PROJECT_COMMAND_TEXT
        const val PROJECT_FILTER_FORM = ProjectsClasses.PROJECT_FILTER_FORM
        const val PROJECT_FILTER_LAYOUT = ProjectsClasses.PROJECT_FILTER_LAYOUT
        const val PROJECT_FILTER_WIDE_ITEM = ProjectsClasses.PROJECT_FILTER_WIDE_ITEM
        const val PROJECT_FILTER_ITEM = ProjectsClasses.PROJECT_FILTER_ITEM
        const val PROJECT_FILTER_LABEL = ProjectsClasses.PROJECT_FILTER_LABEL
        const val PROJECT_ARGUMENT_CONTROL = ProjectsClasses.PROJECT_ARGUMENT_CONTROL
        const val PROJECT_ARGUMENT_FLAG = ProjectsClasses.PROJECT_ARGUMENT_FLAG
        const val PROJECT_ARGUMENT_FIELD = ProjectsClasses.PROJECT_ARGUMENT_FIELD
        const val PROJECT_ARGUMENT_SELECT = ProjectsClasses.PROJECT_ARGUMENT_SELECT
        const val PROJECT_DIRECTION_BUTTON = ProjectsClasses.PROJECT_DIRECTION_BUTTON
        const val PROJECT_TOPIC_CONTROL = ProjectsClasses.PROJECT_TOPIC_CONTROL
        const val PROJECT_TOPIC_EMPTY_VALUE = ProjectsClasses.PROJECT_TOPIC_EMPTY_VALUE
        const val PROJECT_TOPIC_EMPTY_TRIGGER = ProjectsClasses.PROJECT_TOPIC_EMPTY_TRIGGER
        const val PROJECT_TOPIC_SELECTED_VALUE = ProjectsClasses.PROJECT_TOPIC_SELECTED_VALUE
        const val PROJECT_TOPIC_COMMA = ProjectsClasses.PROJECT_TOPIC_COMMA
        const val PROJECT_TOPIC_PICKER = ProjectsClasses.PROJECT_TOPIC_PICKER
        const val PROJECT_TOPIC_ADD_TRIGGER = ProjectsClasses.PROJECT_TOPIC_ADD_TRIGGER
        const val PROJECT_TOPIC_DROPDOWN = ProjectsClasses.PROJECT_TOPIC_DROPDOWN
        const val PROJECT_TOPIC_ADD_PANEL = ProjectsClasses.PROJECT_TOPIC_ADD_PANEL
        const val PROJECT_TOPIC_ADD_BUTTON = ProjectsClasses.PROJECT_TOPIC_ADD_BUTTON
        const val PROJECT_TOPIC_ADD_PANEL_EMPTY = ProjectsClasses.PROJECT_TOPIC_ADD_PANEL_EMPTY
        const val PROJECT_RESULTS_BAR = ProjectsClasses.PROJECT_RESULTS_BAR
        const val PROJECT_RESULTS_SUMMARY = ProjectsClasses.PROJECT_RESULTS_SUMMARY
        const val PROJECT_RESULTS_SUMMARY_TEXT = ProjectsClasses.PROJECT_RESULTS_SUMMARY_TEXT
        const val PROJECT_RESULTS_SUMMARY_ACTIONS = ProjectsClasses.PROJECT_RESULTS_SUMMARY_ACTIONS
        const val PROJECT_RESULTS_LIST = ProjectsClasses.PROJECT_RESULTS_LIST
        const val PROJECT_CARD = ProjectsClasses.PROJECT_CARD
        const val PROJECT_CARD_CONTENT = ProjectsClasses.PROJECT_CARD_CONTENT
        const val PROJECT_CARD_HEADER = ProjectsClasses.PROJECT_CARD_HEADER
        const val PROJECT_CARD_INDEX = ProjectsClasses.PROJECT_CARD_INDEX
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
        const val PROJECT_CARD_ACTIONS = ProjectsClasses.PROJECT_CARD_ACTIONS
        const val PROJECT_ACTION_LINK = ProjectsClasses.PROJECT_ACTION_LINK
        const val RESET_BUTTON = ProjectsClasses.RESET_BUTTON
        const val PROJECT_HEADER_RESET_BUTTON = ProjectsClasses.PROJECT_HEADER_RESET_BUTTON
        const val PROJECT_RESULTS_RESET_BUTTON = ProjectsClasses.PROJECT_RESULTS_RESET_BUTTON
        const val EMPTY_STATE = ProjectsClasses.EMPTY_STATE
        const val EMPTY_STATE_TITLE = ProjectsClasses.EMPTY_STATE_TITLE
        const val EMPTY_STATE_MESSAGE = ProjectsClasses.EMPTY_STATE_MESSAGE
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
