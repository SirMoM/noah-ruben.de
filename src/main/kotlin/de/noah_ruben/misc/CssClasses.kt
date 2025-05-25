package de.noah_ruben.misc

object CssClasses {

    // Layout classes
    object Layout {
        const val CONTAINER = "container"
        const val FLEX = "flex"
        const val FLEX_COL = "flex-col"
        const val FLEX_GROW = "flex-grow"
        const val FLEX_WRAP = "flex-wrap"
        const val FLEX_NONE = "flex-none"
        const val INLINE_FLEX = "inline-flex"
        const val INLINE_BLOCK = "inline-block"
        const val GRID = "grid"
        const val INLINE_GRID = "inline-grid"
        const val ITEMS_CENTER = "items-center"
        const val JUSTIFY_START = "justify-start"
        const val JUSTIFY_CENTER = "justify-center"
        const val JUSTIFY_BETWEEN = "justify-between"

        const val MX_AUTO = "mx-auto"
        const val M_4 = "m-4"
        const val MX_2 = "mx-2"
        const val MX_05 = "mx-0.5"
        const val ML_2 = "ml-2"
        const val ML_4 = "ml-4"
        const val ML_6 = "ml-6"
        const val MR_1 = "mr-1"
        const val MR_2 = "mr-2"
        const val MT_1 = "mt-1"
        const val MT_2 = "mt-2"
        const val MT_4 = "mt-4"
        const val MB_1 = "mb-1"
        const val MB_2 = "mb-2"
        const val MB_4 = "mb-4"
        const val P_1 = "p-1"
        const val P_2 = "p-2"
        const val P_4 = "p-4"
        const val PX_3 = "px-3"
        const val PY_1 = "py-1"

        const val W_FULL = "w-full"
        const val W_AUTO = "w-auto"
        const val W_1_4 = "w-1/4"
        const val W_3_4 = "w-3/4"
        const val W_10 = "w-10"
        const val H_10 = "h-10"
        const val ASPECT_SQUARE = "aspect-square"
        const val MAX_W_LG = "max-w-lg" //  specific example for project tile
        const val MAX_W_NONE = "max-w-none"

        const val SPACE_Y_1 = "space-y-1"
        const val GRID_COLS_1 = "grid-cols-1"
        const val GRID_COLS_8 = "grid-cols-8"
        const val GRID_ROWS_2 = "grid-rows-2"
    }

    // Typography classes
    object Typography {
        const val TEXT_BASE = "text-base"
        const val TEXT_SM = "text-sm"
        const val TEXT_LG = "text-lg"
        const val TEXT_XL = "text-xl"
        const val TEXT_2XL = "text-2xl"
        const val FONT_NORMAL = "font-normal"
        const val FONT_SEMIBOLD = "font-semibold"
        const val FONT_BOLD = "font-bold"
        const val LEADING_RELAXED = "leading-relaxed"
    }

    // Color classes
    object Color {
        const val TEXT_WHITE = "text-white"
        const val TEXT_BLACK = "text-black"
        const val TEXT_RED_500 = "text-red-500"
        const val TEXT_GRAY_500 = "text-gray-500"
        const val TEXT_GRAY_600 = "text-gray-600"
        const val TEXT_GRAY_700 = "text-gray-700"

        const val BG_TRANSPARENT = "bg-transparent"
        const val BG_WHITE = "bg-white"
        const val BG_YELLOW_500 = "bg-yellow-500"
        const val BG_GRAY_200 = "bg-gray-200"
        const val BG_GRAY_500 = "bg-gray-500"
        const val BG_GRAY_700 = "bg-gray-700"
        const val BG_GRAY_800 = "bg-gray-800"
        const val BG_GRAY_900 = "bg-gray-900"
        const val DARK_BG_GRAY_800 = "dark:bg-gray-800"
        const val DARK_BG_GRAY_900 = "dark:bg-gray-900"
    }

    // Border classes
    object Border {
        const val BORDER = "border"
        const val BORDER_NONE = "border-none"
        const val BORDER_BLACK = "border-black"
        const val BORDER_GRAY_300 = "border-gray-300"
        const val BORDER_GRAY_400 = "border-gray-400" // Simplified
        const val BORDER_GRAY_600 = "border-gray-600"
        const val ROUNDED_SM = "rounded-sm"
        const val ROUNDED = "rounded"
        const val ROUNDED_MD = "rounded-md"
        const val ROUNDED_LG = "rounded-lg"
        const val ROUNDED_FULL = "rounded-full"
        const val OVERFLOW_HIDDEN = "overflow-hidden"
        const val OUTLINE_NONE = "outline-none"
    }

    // Effect classes
    object Effect {
        const val SHADOW = "shadow"
        const val SHADOW_MD = "shadow-md"
        const val SHADOW_LG = "shadow-lg"
        const val HOVER_BORDER_2 = "hover:border-2"
        const val FOCUS_OUTLINE_NONE = "focus:outline-none"
    }

    // Alignment classes
    object Alignment {
        const val ALIGN_MIDDLE = "align-middle"
        const val ORDER_0 = "order-0"
    }

    // Compound Classes / Component Styles ("Rude Variance")
    object Components {
        // --- Global ---
        val PAGE_BODY = setOf(Color.DARK_BG_GRAY_900, Color.TEXT_WHITE)
        val PAGE_CONTAINER = setOf(Layout.CONTAINER, Layout.MX_AUTO, Layout.P_4)
        val PAGE_TITLE = setOf(Typography.TEXT_2XL, Typography.FONT_BOLD, Layout.MB_4)

        // --- Forms & Inputs ---
        val INPUT_BASE = setOf(
            Color.BG_GRAY_500,
            Border.BORDER,
            Border.BORDER_GRAY_400,
            Border.ROUNDED,
            Layout.P_2,
        )
        val INPUT_TEXT_DEFAULT = INPUT_BASE + Layout.FLEX_GROW

        val SELECT_DEFAULT = INPUT_BASE + Layout.MX_2

        val CHECKBOX_DEFAULT = setOf(
            Color.BG_GRAY_700,
            Color.TEXT_WHITE,
            Border.ROUNDED,
            Border.BORDER,
            Layout.P_2,
            Layout.MT_4,
            Typography.TEXT_XL,
            Alignment.ALIGN_MIDDLE,
        )
        val LABEL_DEFAULT = setOf(Layout.MR_1, Alignment.ALIGN_MIDDLE)

        val BUTTON_BASE = setOf(
            Border.ROUNDED,
            Border.BORDER,
            Layout.P_2,
            Typography.TEXT_XL,
            Layout.FLEX,
            Layout.ITEMS_CENTER,
            Layout.JUSTIFY_CENTER, // flex for icon + text
        )
        val BUTTON_PRIMARY = BUTTON_BASE + setOf(
            Color.BG_GRAY_700,
            Color.TEXT_WHITE,
            Layout.MT_4,
        )
        val ACTION_TAG = setOf( // Base for small rounded tags/buttons
            Layout.INLINE_BLOCK,
            Border.ROUNDED_FULL,
            Layout.PX_3,
            Layout.PY_1,
            Typography.TEXT_SM,
            Typography.FONT_SEMIBOLD,
        )
        val ACTION_BUTTON_LIGHT = ACTION_TAG + setOf(
            Color.BG_GRAY_200,
            Color.TEXT_GRAY_700,
            Layout.MR_2,
        )
        // Language tag will combine ACTION_TAG with dynamic colors

        val SEARCH_BOX_WRAPPER = setOf(
            Layout.FLEX_GROW,
            Color.BG_GRAY_700,
            Border.BORDER,
            Border.BORDER_GRAY_400,
            Border.ROUNDED,
            Layout.P_4,
            Layout.MB_4,
        )
        val SEARCH_INPUT_CONTAINER = setOf(Layout.FLEX, Layout.FLEX_COL)

        // --- Profile / Index Page ---
        val PROMPT_TEXT = setOf(Typography.TEXT_LG) // For ">> noahruben"
        val PROFILE_LAYOUT = setOf(Layout.FLEX, Layout.ITEMS_CENTER, Layout.MB_4)
        val PROFILE_IMAGE = setOf(
            Layout.M_4,
            Layout.ASPECT_SQUARE,
            Layout.W_1_4,
            Border.ROUNDED_FULL,
            Color.BG_YELLOW_500,
        )
        val PROFILE_DETAILS_CONTAINER = setOf(Layout.W_3_4, Typography.TEXT_XL) // Simplified, use media queries in CSS for xl:text-xl md:text-xl if needed
        val PROFILE_INFO_GRID = setOf(Layout.GRID, Layout.GRID_COLS_1, Layout.M_4, Layout.SPACE_Y_1)
        val PROFILE_INFO_LABEL = setOf(Color.TEXT_RED_500) // Name, Uptime, etc. label
        val COLOR_PALETTE_GRID = setOf(
            Layout.INLINE_GRID,
            Layout.FLEX_NONE,
            Layout.GRID_COLS_8,
            Layout.GRID_ROWS_2,
            Layout.MT_4,
        )
        val COLOR_PALETTE_SQUARE_BASE = setOf(
            Border.BORDER,
            Border.BORDER_BLACK,
            Effect.HOVER_BORDER_2,
            Layout.H_10,
            Layout.W_10,
        )
        val SECTION_TEXT_BLOCK = setOf(Layout.MT_4, Layout.MB_4, Typography.LEADING_RELAXED) // For the "I'm currently working on..." block

        // --- Project Tile / Cards ---
        val CARD_DEFAULT = setOf(
            Border.BORDER, Border.BORDER_GRAY_300, Border.ROUNDED, Layout.P_4, Layout.MB_4,
            Layout.MAX_W_LG, Border.OVERFLOW_HIDDEN, Effect.SHADOW_LG, Color.BG_WHITE,
        )
        val CARD_TITLE = setOf(Typography.FONT_BOLD, Typography.TEXT_XL, Layout.MB_2, Color.TEXT_BLACK)
        val CARD_DESCRIPTION = setOf(Color.TEXT_GRAY_700, Typography.TEXT_BASE, Color.TEXT_BLACK)
        val CARD_METADATA_CONTAINER = setOf(Layout.FLEX, Layout.ITEMS_CENTER, Color.TEXT_GRAY_600, Typography.TEXT_SM)
        val CARD_METADATA_MT2 = CARD_METADATA_CONTAINER + Layout.MT_2
        val CARD_METADATA_MT4 = CARD_METADATA_CONTAINER + Layout.MT_4

        // --- Command Line Emulation ---
        val CLI_OUTPUT_PREFIX = setOf(Typography.TEXT_LG, Layout.MB_1) // For ">> command" lines
        val CLI_HELP_INDENT = setOf(Layout.ML_6)
        val CLI_SUBPAGE_INDENT = setOf(Layout.ML_4)
        val CLI_INPUT_WRAPPER = setOf(
            Layout.W_FULL,
            Layout.INLINE_FLEX,
            Color.DARK_BG_GRAY_900,
            Color.TEXT_WHITE,
            Border.OUTLINE_NONE,
            Effect.FOCUS_OUTLINE_NONE,
            Layout.P_4,
            Layout.MT_4,
        )
        val CLI_INPUT_FIELD = setOf(
            Layout.FLEX_GROW,
            Color.BG_TRANSPARENT,
            Border.BORDER_NONE,
            Border.OUTLINE_NONE,
        )
        val CLI_PROMPT_SYMBOL = setOf(Layout.MR_1) // For ">>" before input

        // --- Messages ---
        val ERROR_MESSAGE = setOf(Color.TEXT_RED_500, Layout.P_4)

        // --- Misc ---
        val SPINNER_MESSAGE = setOf(Layout.FLEX, Layout.ITEMS_CENTER, Color.TEXT_WHITE) // For HTMX spinner
    }
}
