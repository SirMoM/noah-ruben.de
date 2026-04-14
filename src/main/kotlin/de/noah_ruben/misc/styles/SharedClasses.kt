package de.noah_ruben.misc.styles

object SharedClasses {
    const val MB_8 = "mb-8"
    const val MB_4 = "mb-4"

    const val CLI_WRAPPER = "w-full inline-flex outline-none focus:outline-none border border-ctp-crust rounded"
    const val CLI_INPUT_FIELD = "pl-4 flex-grow bg-transparent border-none outline-none focus:ring-0 text-ctp-text placeholder:text-ctp-overlay1"

    const val FORM_GROUP = "flex flex-col"
    const val FORM_FIELD = "flex flex-col mb-2"
    const val FORM_LABEL = "mr-1 align-middle text-ctp-subtext1"
    const val FORM_INPUT_BASE = "bg-ctp-base border border-ctp-overlay1 rounded p-2 text-ctp-text focus:outline-none focus:border-ctp-blue"
    const val FORM_INPUT_TEXT = "flex-grow"
    const val FORM_INPUT_TEXT_WITH_MARGIN = "$FORM_INPUT_BASE $FORM_INPUT_TEXT $MB_4"
    const val FORM_CHECKBOX_GROUP = "flex items-center mt-2"
    const val FORM_CHECKBOX = "bg-ctp-base text-ctp-blue rounded border border-ctp-overlay1 p-2 mt-4 text-xl align-middle mr-1 appearance-none checked:bg-ctp-blue checked:border-transparent focus:outline-none focus:ring-2 focus:ring-ctp-sapphire"
    const val SUBMIT_BUTTON = "rounded border border-ctp-overlay1 p-2 text-xl flex items-center justify-center bg-ctp-base text-ctp-text mt-4 hover:bg-ctp-surface0 focus:outline-none focus:ring-2 focus:ring-ctp-sapphire"
    const val LOADING_SPINNER = "flex items-center text-ctp-subtext0"
    const val FILTER_CONTROLS_LAYOUT = "flex flex-wrap gap-4 items-end mb-4"
    const val FILTER_ITEM_LAYOUT = "flex-col"

    const val TOGGLE_BUTTON = "mb-4 self-end inline-flex shrink-0 items-center gap-1 rounded-full border border-ctp-overlay1 bg-ctp-base p-1 text-sm font-semibold text-ctp-text"
    const val TOGGLE_BUTTON_ICON = "inline-flex h-8 w-8 items-center justify-center rounded-full transition-all [&_svg]:h-5 [&_svg]:w-5"
    const val TOGGLE_BUTTON_ICON_MOON = "text-ctp-overlay0 opacity-70 data-[theme=dark]:bg-ctp-surface1 data-[theme=dark]:text-ctp-blue data-[theme=dark]:opacity-100"
    const val TOGGLE_BUTTON_ICON_SUN = "text-ctp-overlay0 opacity-70 data-[theme=light]:bg-ctp-surface1 data-[theme=light]:text-ctp-yellow data-[theme=light]:opacity-100"
    const val TOGGLE_BUTTON_ICON_MOON_FULL = "$TOGGLE_BUTTON_ICON $TOGGLE_BUTTON_ICON_MOON"
    const val TOGGLE_BUTTON_ICON_SUN_FULL = "$TOGGLE_BUTTON_ICON $TOGGLE_BUTTON_ICON_SUN"

    const val HTMX_INDICATOR = "htmx-indicator"
    const val HTMX_INDICATOR_INLINE = "htmx-indicator ml-2"
    const val HTMX_REQUEST = "htmx-request"
    const val SCREEN_READER_ONLY = "sr-only"
    const val HELP_INDENT = "pl-6"
    const val SUBPAGE_INDENT = "pl-12"
    const val ERROR_MESSAGE_BOX = "text-ctp-red font-bold p-4 border border-ctp-red rounded mb-4"
}
