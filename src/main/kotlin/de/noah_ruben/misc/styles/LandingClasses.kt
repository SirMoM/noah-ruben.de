package de.noah_ruben.misc.styles

object LandingClasses {
    const val PROFILE_CONTAINER = "flex items-center"
    const val PROFILE_PICTURE = "m-4 aspect-square rounded-full bg-ctp-yellow lg:w-1/6 w-1/3"
    const val PROFILE_DETAILS_CONTAINER = "lg:w-5/6 w-2/3 text-xl grow"
    const val COLOR_GRID = "inline-grid flex-none grid-cols-8 grid-rows-2 mt-4 [&>div]:h-10 [&>div]:w-10 [&>div]:border [&>div]:border-black [&>div]:dark:border-black [&>div]:transition-all [&>div]:duration-150 [&>div:hover]:scale-110 [&>div:hover]:border-black [&>div:hover]:dark:border-black"
    const val ABOUT_ME = "p-6 leading-relaxed"
    const val PROFILE_LABEL = "text-ctp-teal font-semibold"
    const val PROFILE_HEADER = "text-ctp-blue font-bold"
    const val SECTION_DIVIDER = "text-ctp-overlay1"
    const val PROFILE_DIVIDER = "${SharedClasses.MB_4} text-ctp-overlay1"
    const val PROMPT_ARROW = "text-ctp-blue"
    const val LOCATION_TEXT = "text-ctp-teal"
    const val COLOR_GRID_LATTE = "latte $COLOR_GRID"
    const val SYSTEM_SUMMARY_HEADING = "text-ctp-yellow"
    val ABOUT_ME_LINE_COLORS = listOf(
        "text-ctp-green",
        "text-ctp-peach",
        "text-ctp-sky",
        "text-ctp-lavender",
        "text-ctp-pink",
    )

    // Catppuccin color classes paired with their Latte hex values for accent switching
    val COLORS_WITH_HEX = listOf(
        Pair("bg-ctp-rosewater", "#dc8a78"),
        Pair("bg-ctp-flamingo", "#dd7878"),
        Pair("bg-ctp-pink", "#ea76cb"),
        Pair("bg-ctp-mauve", "#8839ef"),
        Pair("bg-ctp-red", "#d20f39"),
        Pair("bg-ctp-maroon", "#e64553"),
        Pair("bg-ctp-peach", "#fe640b"),
        Pair("bg-ctp-yellow", "#df8e1d"),
        Pair("bg-ctp-green", "#40a02b"),
        Pair("bg-ctp-teal", "#179299"),
        Pair("bg-ctp-sky", "#04a5e5"),
        Pair("bg-ctp-sapphire", "#209fb5"),
        Pair("bg-ctp-blue", "#1e66f5"),
        Pair("bg-ctp-lavender", "#7287fd"),
        Pair("bg-ctp-overlay0", "#9ca0b0"),
        Pair("bg-ctp-crust", "#dce0e8"),
    )
}
