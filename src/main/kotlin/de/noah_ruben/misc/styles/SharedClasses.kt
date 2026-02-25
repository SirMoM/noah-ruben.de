package de.noah_ruben.misc.styles

object SharedClasses {
    const val MB_8 = "mb-8"
    const val MB_4 = "mb-4"

    const val CLI_WRAPPER = "w-full inline-flex outline-none focus:outline-none border border-crust rounded"
    const val CLI_INPUT_FIELD = "pl-4 flex-grow bg-transparent border-none outline-none focus:ring-0"

    const val FORM_GROUP = "flex flex-col"
    const val FORM_FIELD = "flex flex-col mb-2"
    const val FORM_LABEL = "mr-1 align-middle text-subtext1"
    const val FORM_INPUT_BASE = "bg-rosewater border border-overlay1 rounded p-2 text-crust focus:outline-none focus:border-blue dark:bg-surface0 dark:border-surface1 dark:text-text"
    const val FORM_INPUT_TEXT = "flex-grow"
    const val FORM_INPUT_TEXT_WITH_MARGIN = "$FORM_INPUT_BASE $FORM_INPUT_TEXT $MB_4"
    const val FORM_CHECKBOX_GROUP = "flex items-center mt-2"
    const val FORM_CHECKBOX = "bg-rosewater text-blue rounded border border-overlay1 p-2 mt-4 text-xl align-middle mr-1 appearance-none checked:bg-blue checked:border-transparent focus:outline-none focus:ring-2 focus:ring-sapphire dark:bg-surface0 dark:border-surface1"
    const val SUBMIT_BUTTON = "rounded border border-overlay1 p-2 text-xl flex items-center justify-center bg-rosewater text-crust mt-4 hover:bg-flamingo focus:outline-none focus:ring-2 focus:ring-sapphire dark:border-surface1 dark:bg-surface0 dark:text-text dark:hover:bg-surface1"
    const val LOADING_SPINNER = "flex items-center text-subtext0"
    const val FILTER_CONTROLS_LAYOUT = "flex flex-wrap gap-4 items-end mb-4"
    const val FILTER_ITEM_LAYOUT = "flex-col"

    const val TOGGLE_BUTTON = "fixed right-4 top-4 z-50 inline-flex items-center gap-1 rounded-full border border-overlay1 bg-rosewater p-1 text-sm font-semibold text-crust dark:border-surface1 dark:bg-surface0 dark:text-text"
    const val TOGGLE_BUTTON_ICON = "inline-flex h-8 w-8 items-center justify-center rounded-full transition-all [&_svg]:h-5 [&_svg]:w-5"
    const val TOGGLE_BUTTON_ICON_MOON = "text-overlay0 opacity-70 data-[theme=dark]:bg-surface1 data-[theme=dark]:text-blue data-[theme=dark]:opacity-100"
    const val TOGGLE_BUTTON_ICON_SUN = "text-overlay0 opacity-70 data-[theme=light]:bg-surface1 data-[theme=light]:text-yellow data-[theme=light]:opacity-100"
    const val TOGGLE_BUTTON_ICON_MOON_FULL = "$TOGGLE_BUTTON_ICON $TOGGLE_BUTTON_ICON_MOON"
    const val TOGGLE_BUTTON_ICON_SUN_FULL = "$TOGGLE_BUTTON_ICON $TOGGLE_BUTTON_ICON_SUN"

    const val HTMX_INDICATOR = "htmx-indicator"
    const val HTMX_INDICATOR_INLINE = "htmx-indicator ml-2"
    const val HELP_INDENT = "pl-6"
    const val SUBPAGE_INDENT = "pl-12"
    const val ERROR_MESSAGE_BOX = "text-red font-bold p-4 border border-red rounded mb-4"
}
