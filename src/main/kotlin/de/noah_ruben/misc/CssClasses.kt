package de.noah_ruben.misc

object CssClasses {
    // Layout classes
    object Layout {
        const val CONTAINER = "container"
        const val FLEX = "flex"
        const val FLEX_COL = "flex-col"
        const val FLEX_GROW = "flex-grow"
        const val FLEX_WRAP = "flex-wrap"
        const val ITEMS_CENTER = "items-center"
        const val JUSTIFY_START = "justify-start"
        const val MX_AUTO = "mx-auto"
        const val MX_2 = "mx-2"
        const val MX_05 = "mx-0.5"
        const val ML_2 = "ml-2"
        const val ML_4 = "ml-4"
        const val ML_6 = "ml-6"
        const val MR_1 = "mr-1"
        const val MR_2 = "mr-2"
        const val MT_2 = "mt-2"
        const val MT_4 = "mt-4"
        const val MB_2 = "mb-2"
        const val MB_4 = "mb-4"
        const val P_2 = "p-2"
        const val P_4 = "p-4"
        const val PX_3 = "px-3"
        const val PY_1 = "py-1"
        const val W_FULL = "w-full"
        const val H_10 = "h-10"
        const val W_10 = "w-10"
    }

    // Typography classes
    object Typography {
        const val TEXT_BASE = "text-base"
        const val TEXT_SM = "text-sm"
        const val TEXT_XL = "text-xl"
        const val TEXT_2XL = "text-2xl"
        const val FONT_BOLD = "font-bold"
        const val FONT_SEMIBOLD = "font-semibold"
    }

    // Color classes
    object Color {
        const val TEXT_WHITE = "text-white"
        const val TEXT_RED_500 = "text-red-500"
        const val TEXT_GRAY_600 = "text-gray-600"
        const val TEXT_GRAY_700 = "text-gray-700"
        const val BG_WHITE = "bg-white"
        const val BG_GRAY_200 = "bg-gray-200"
        const val BG_GRAY_500 = "bg-gray-500"
        const val BG_GRAY_700 = "bg-gray-700"
        const val BG_GRAY_900 = "bg-gray-900"
        const val DARK_BG_GRAY_900 = "dark:bg-gray-900"
    }

    // Border classes
    object Border {
        const val BORDER = "border"
        const val BORDER_NONE = "border-none"
        const val BORDER_BLACK = "border-black"
        const val BORDER_GRAY_300 = "border-gray-300"
        val BORDER_GRAY_400 = borderGray("400")
        const val ROUNDED = "rounded"
        const val ROUNDED_FULL = "rounded-full"
        const val OVERFLOW_HIDDEN = "overflow-hidden"
        const val OUTLINE_NONE = "outline-none"
    }

    // Effect classes
    object Effect {
        const val SHADOW_LG = "shadow-lg"
        const val HOVER_BORDER_2 = "hover:border-2"
    }

    // Alignment classes
    object Alignment {
        const val ALIGN_MIDDLE = "align-middle"
        const val ORDER_0 = "order-0"
    }

    // Common class combinations
    object Combinations {
        val INPUT_CLASSES = setOf(
            Layout.FLEX_GROW,
            Color.BG_GRAY_500,
            Border.BORDER,
            Border.BORDER_GRAY_400,
            Border.ROUNDED,
            Layout.P_2,
        )

        val SEARCH_BOX_CLASSES = setOf(
            Layout.FLEX_GROW,
            Color.BG_GRAY_700,
            Border.BORDER,
            Border.BORDER_GRAY_400,
            Border.ROUNDED,
            Layout.P_4,
            Layout.MB_4,
        )

        val SELECT_CLASSES = setOf(
            Color.BG_GRAY_500,
            Border.BORDER,
            Border.BORDER_GRAY_400,
            Border.ROUNDED,
            Layout.MX_2,
        )

        val CHECKBOX_CLASSES = setOf(
            Color.BG_GRAY_700,
            Color.TEXT_WHITE,
            Border.ROUNDED,
            Border.BORDER,
            Layout.P_2,
            Layout.MT_4,
            Typography.TEXT_XL,
            Alignment.ALIGN_MIDDLE,
        )

        val LABEL_CLASSES = setOf(
            Layout.MR_1,
            Alignment.ALIGN_MIDDLE,
        )

        val BUTTON_CLASSES = setOf(
            Color.BG_GRAY_700,
            Color.TEXT_WHITE,
            Border.ROUNDED,
            Border.BORDER,
            Layout.P_2,
            Layout.MT_4,
            Typography.TEXT_XL,
        )

        val PROJECT_TILE_CLASSES = setOf(
            Border.BORDER,
            Border.BORDER_GRAY_300,
            Border.ROUNDED,
            Layout.P_4,
            Layout.MB_4,
            "max-w",
            Border.ROUNDED,
            Border.OVERFLOW_HIDDEN,
            Effect.SHADOW_LG,
            Color.BG_WHITE,
        )

        val METADATA_CLASSES = setOf(
            Layout.FLEX,
            Layout.ITEMS_CENTER,
            Layout.MT_2,
            Color.TEXT_GRAY_600,
            Typography.TEXT_SM,
        )

        val METADATA_CLASSES_MT4 = setOf(
            Layout.FLEX,
            Layout.ITEMS_CENTER,
            Layout.MT_4,
            Color.TEXT_GRAY_600,
            Typography.TEXT_SM,
        )

        val ACTION_BUTTON_CLASSES = setOf(
            "inline-block",
            Color.BG_GRAY_200,
            Border.ROUNDED_FULL,
            Layout.PX_3,
            Layout.PY_1,
            Typography.TEXT_SM,
            Typography.FONT_SEMIBOLD,
            Color.TEXT_GRAY_700,
        )

        val ERROR_MESSAGE_CLASSES = setOf(
            Color.TEXT_RED_500,
            Layout.P_4,
        )

        val COMMAND_LINE_INPUT_CLASSES = setOf(
            Layout.W_FULL,
            Color.DARK_BG_GRAY_900,
            Border.BORDER_NONE,
            Border.OUTLINE_NONE,
        )

        val LANGUAGE_TAG_BASE_CLASSES = setOf(
            Layout.MX_05,
            "inline-block",
            Border.ROUNDED_FULL,
            Layout.PX_3,
            Layout.PY_1,
            Typography.TEXT_SM,
            Typography.FONT_SEMIBOLD,
        )
    }
}

/**
 * CSS Builder class for creating CSS class combinations using a builder pattern.
 */
class CssBuilder {
    private val classes = mutableSetOf<String>()

    // Layout methods
    fun container() = apply { add(CssClasses.Layout.CONTAINER) }
    fun flex() = apply { add(CssClasses.Layout.FLEX) }
    fun flexCol() = apply { add(CssClasses.Layout.FLEX_COL) }
    fun flexGrow() = apply { add(CssClasses.Layout.FLEX_GROW) }
    fun flexWrap() = apply { add(CssClasses.Layout.FLEX_WRAP) }
    fun itemsCenter() = apply { add(CssClasses.Layout.ITEMS_CENTER) }
    fun justifyStart() = apply { add(CssClasses.Layout.JUSTIFY_START) }
    fun mxAuto() = apply { add(CssClasses.Layout.MX_AUTO) }
    fun mx2() = apply { add(CssClasses.Layout.MX_2) }
    fun mx05() = apply { add(CssClasses.Layout.MX_05) }
    fun ml2() = apply { add(CssClasses.Layout.ML_2) }
    fun ml4() = apply { add(CssClasses.Layout.ML_4) }
    fun ml6() = apply { add(CssClasses.Layout.ML_6) }
    fun mr1() = apply { add(CssClasses.Layout.MR_1) }
    fun mr2() = apply { add(CssClasses.Layout.MR_2) }
    fun mt2() = apply { add(CssClasses.Layout.MT_2) }
    fun mt4() = apply { add(CssClasses.Layout.MT_4) }
    fun mb2() = apply { add(CssClasses.Layout.MB_2) }
    fun mb4() = apply { add(CssClasses.Layout.MB_4) }
    fun p2() = apply { add(CssClasses.Layout.P_2) }
    fun p4() = apply { add(CssClasses.Layout.P_4) }
    fun px3() = apply { add(CssClasses.Layout.PX_3) }
    fun py1() = apply { add(CssClasses.Layout.PY_1) }
    fun wFull() = apply { add(CssClasses.Layout.W_FULL) }
    fun h10() = apply { add(CssClasses.Layout.H_10) }
    fun w10() = apply { add(CssClasses.Layout.W_10) }

    // Typography methods
    fun textBase() = apply { add(CssClasses.Typography.TEXT_BASE) }
    fun textSm() = apply { add(CssClasses.Typography.TEXT_SM) }
    fun textXl() = apply { add(CssClasses.Typography.TEXT_XL) }
    fun text2xl() = apply { add(CssClasses.Typography.TEXT_2XL) }
    fun fontBold() = apply { add(CssClasses.Typography.FONT_BOLD) }
    fun fontSemibold() = apply { add(CssClasses.Typography.FONT_SEMIBOLD) }

    // Color methods
    fun textWhite() = apply { add(CssClasses.Color.TEXT_WHITE) }
    fun textRed500() = apply { add(CssClasses.Color.TEXT_RED_500) }
    fun textGray600() = apply { add(CssClasses.Color.TEXT_GRAY_600) }
    fun textGray700() = apply { add(CssClasses.Color.TEXT_GRAY_700) }
    fun bgWhite() = apply { add(CssClasses.Color.BG_WHITE) }
    fun bgGray200() = apply { add(CssClasses.Color.BG_GRAY_200) }
    fun bgGray500() = apply { add(CssClasses.Color.BG_GRAY_500) }
    fun bgGray700() = apply { add(CssClasses.Color.BG_GRAY_700) }
    fun bgGray900() = apply { add(CssClasses.Color.BG_GRAY_900) }
    fun darkBgGray900() = apply { add(CssClasses.Color.DARK_BG_GRAY_900) }

    // Border methods
    fun border() = apply { add(CssClasses.Border.BORDER) }
    fun borderNone() = apply { add(CssClasses.Border.BORDER_NONE) }
    fun borderBlack() = apply { add(CssClasses.Border.BORDER_BLACK) }
    fun borderGray300() = apply { add(CssClasses.Border.BORDER_GRAY_300) }
    fun borderGray400() = apply { add(CssClasses.Border.BORDER_GRAY_400) }
    fun rounded() = apply { add(CssClasses.Border.ROUNDED) }
    fun roundedFull() = apply { add(CssClasses.Border.ROUNDED_FULL) }
    fun overflowHidden() = apply { add(CssClasses.Border.OVERFLOW_HIDDEN) }
    fun outlineNone() = apply { add(CssClasses.Border.OUTLINE_NONE) }

    // Effect methods
    fun shadowLg() = apply { add(CssClasses.Effect.SHADOW_LG) }
    fun hoverBorder2() = apply { add(CssClasses.Effect.HOVER_BORDER_2) }

    // Alignment methods
    fun alignMiddle() = apply { add(CssClasses.Alignment.ALIGN_MIDDLE) }
    fun order0() = apply { add(CssClasses.Alignment.ORDER_0) }

    // Predefined combinations
    fun inputClasses() = apply { add(CssClasses.Combinations.INPUT_CLASSES) }
    fun searchBoxClasses() = apply { add(CssClasses.Combinations.SEARCH_BOX_CLASSES) }
    fun selectClasses() = apply { add(CssClasses.Combinations.SELECT_CLASSES) }
    fun checkboxClasses() = apply { add(CssClasses.Combinations.CHECKBOX_CLASSES) }
    fun labelClasses() = apply { add(CssClasses.Combinations.LABEL_CLASSES) }
    fun buttonClasses() = apply { add(CssClasses.Combinations.BUTTON_CLASSES) }
    fun projectTileClasses() = apply { add(CssClasses.Combinations.PROJECT_TILE_CLASSES) }
    fun metadataClasses() = apply { add(CssClasses.Combinations.METADATA_CLASSES) }
    fun metadataClassesMt4() = apply { add(CssClasses.Combinations.METADATA_CLASSES_MT4) }
    fun actionButtonClasses() = apply { add(CssClasses.Combinations.ACTION_BUTTON_CLASSES) }
    fun errorMessageClasses() = apply { add(CssClasses.Combinations.ERROR_MESSAGE_CLASSES) }
    fun commandLineInputClasses() = apply { add(CssClasses.Combinations.COMMAND_LINE_INPUT_CLASSES) }
    fun languageTagBaseClasses() = apply { add(CssClasses.Combinations.LANGUAGE_TAG_BASE_CLASSES) }

    // Custom class method
    fun custom(className: String) = apply { add(className) }

    // Add multiple classes at once
    fun add(vararg classNames: String) = apply { classes.addAll(classNames) }
    fun add(classNames: Set<String>) = apply { classes.addAll(classNames) }

    fun toCssString(): String = classes.joinToString(separator = " ")
}

/**
 * Create a new CSS builder.
 */
fun css(block: CssBuilder.() -> Unit): String = CssBuilder().apply(block).toCssString()
