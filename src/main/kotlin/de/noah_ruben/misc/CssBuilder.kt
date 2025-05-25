package de.noah_ruben.misc

/**
 * CSS Builder class for creating CSS class combinations using a builder pattern.
 */
class CssBuilder {
    private val classes = mutableSetOf<String>()

    // Method to add any set of classes
    private fun addComponent(componentClasses: Set<String>) = apply { classes.addAll(componentClasses) }

    // --- Global Components ---
    fun pageBody() = addComponent(CssClasses.Components.PAGE_BODY)
    fun pageContainer() = addComponent(CssClasses.Components.PAGE_CONTAINER)
    fun pageTitle() = addComponent(CssClasses.Components.PAGE_TITLE)

    // --- Form & Input Components ---
    fun inputBase() = addComponent(CssClasses.Components.INPUT_BASE)
    fun inputTextDefault() = addComponent(CssClasses.Components.INPUT_TEXT_DEFAULT)
    fun selectDefault() = addComponent(CssClasses.Components.SELECT_DEFAULT)
    fun checkboxDefault() = addComponent(CssClasses.Components.CHECKBOX_DEFAULT)
    fun labelDefault() = addComponent(CssClasses.Components.LABEL_DEFAULT)
    fun buttonBase() = addComponent(CssClasses.Components.BUTTON_BASE)
    fun buttonPrimary() = addComponent(CssClasses.Components.BUTTON_PRIMARY)
    fun actionTag() = addComponent(CssClasses.Components.ACTION_TAG)
    fun actionButtonLight() = addComponent(CssClasses.Components.ACTION_BUTTON_LIGHT)
    fun searchBoxWrapper() = addComponent(CssClasses.Components.SEARCH_BOX_WRAPPER)
    fun searchInputContainer() = addComponent(CssClasses.Components.SEARCH_INPUT_CONTAINER)

    // --- Profile / Index Page Components ---
    fun promptText() = addComponent(CssClasses.Components.PROMPT_TEXT)
    fun profileLayout() = addComponent(CssClasses.Components.PROFILE_LAYOUT)
    fun profileImage() = addComponent(CssClasses.Components.PROFILE_IMAGE)
    fun profileDetailsContainer() = addComponent(CssClasses.Components.PROFILE_DETAILS_CONTAINER)
    fun profileInfoGrid() = addComponent(CssClasses.Components.PROFILE_INFO_GRID)
    fun profileInfoLabel() = addComponent(CssClasses.Components.PROFILE_INFO_LABEL)
    fun colorPaletteGrid() = addComponent(CssClasses.Components.COLOR_PALETTE_GRID)
    fun colorPaletteSquareBase() = addComponent(CssClasses.Components.COLOR_PALETTE_SQUARE_BASE)
    fun sectionTextBlock() = addComponent(CssClasses.Components.SECTION_TEXT_BLOCK)

    // --- Project Tile / Card Components ---
    fun cardDefault() = addComponent(CssClasses.Components.CARD_DEFAULT)
    fun cardTitle() = addComponent(CssClasses.Components.CARD_TITLE)
    fun cardDescription() = addComponent(CssClasses.Components.CARD_DESCRIPTION)
    fun cardMetadataContainer() = addComponent(CssClasses.Components.CARD_METADATA_CONTAINER)
    fun cardMetadataMt2() = addComponent(CssClasses.Components.CARD_METADATA_MT2)
    fun cardMetadataMt4() = addComponent(CssClasses.Components.CARD_METADATA_MT4)

    // --- Command Line Emulation Components ---
    fun cliOutputPrefix() = addComponent(CssClasses.Components.CLI_OUTPUT_PREFIX)
    fun cliHelpIndent() = addComponent(CssClasses.Components.CLI_HELP_INDENT)
    fun cliSubpageIndent() = addComponent(CssClasses.Components.CLI_SUBPAGE_INDENT)
    fun cliInputWrapper() = addComponent(CssClasses.Components.CLI_INPUT_WRAPPER)
    fun cliInputField() = addComponent(CssClasses.Components.CLI_INPUT_FIELD)
    fun cliPromptSymbol() = addComponent(CssClasses.Components.CLI_PROMPT_SYMBOL)

    // --- Message Components ---
    fun errorMessage() = addComponent(CssClasses.Components.ERROR_MESSAGE)

    // --- Misc ---
    fun spinnerMessage() = addComponent(CssClasses.Components.SPINNER_MESSAGE)

    // --- Atomic Layout methods (keep for flexibility or remove if strictly component-based) ---
    fun container() = apply { add(CssClasses.Layout.CONTAINER) }
    fun flex() = apply { add(CssClasses.Layout.FLEX) }
    fun flexCol() = apply { add(CssClasses.Layout.FLEX_COL) }
    fun flexGrow() = apply { add(CssClasses.Layout.FLEX_GROW) }
    fun itemsCenter() = apply { add(CssClasses.Layout.ITEMS_CENTER) }

    // ... (keep other atomic methods as needed, or decide to remove them for a stricter component approach)
    // For brevity, I'm not listing all atomic methods again, but they'd be here.
    // It's often good to keep them for one-off adjustments.
    fun m4() = apply { add(CssClasses.Layout.M_4) }
    fun mt2() = apply { add(CssClasses.Layout.MT_2) }
    fun mt4() = apply { add(CssClasses.Layout.MT_4) }
    fun mb2() = apply { add(CssClasses.Layout.MB_2) }
    fun mb4() = apply { add(CssClasses.Layout.MB_4) }
    fun ml2() = apply { add(CssClasses.Layout.ML_2) }
    fun mr1() = apply { add(CssClasses.Layout.MR_1) }
    fun mr2() = apply { add(CssClasses.Layout.MR_2) }
    fun p2() = apply { add(CssClasses.Layout.P_2) }
    fun p4() = apply { add(CssClasses.Layout.P_4) }
    fun px3() = apply { add(CssClasses.Layout.PX_3) }
    fun py1() = apply { add(CssClasses.Layout.PY_1) }
    fun wFull() = apply { add(CssClasses.Layout.W_FULL) }
    fun h10() = apply { add(CssClasses.Layout.H_10) }
    fun w10() = apply { add(CssClasses.Layout.W_10) }

    // --- Atomic Typography methods ---
    fun textBase() = apply { add(CssClasses.Typography.TEXT_BASE) }
    fun textSm() = apply { add(CssClasses.Typography.TEXT_SM) }
    fun textXl() = apply { add(CssClasses.Typography.TEXT_XL) }
    fun text2xl() = apply { add(CssClasses.Typography.TEXT_2XL) }
    fun fontBold() = apply { add(CssClasses.Typography.FONT_BOLD) }
    fun fontSemibold() = apply { add(CssClasses.Typography.FONT_SEMIBOLD) }

    // --- Atomic Color methods ---
    fun textWhite() = apply { add(CssClasses.Color.TEXT_WHITE) }
    fun textRed500() = apply { add(CssClasses.Color.TEXT_RED_500) }
    // ... etc for other atomic classes

    // --- Atomic Border methods ---
    fun border() = apply { add(CssClasses.Border.BORDER) }
    fun rounded() = apply { add(CssClasses.Border.ROUNDED) }
    fun roundedFull() = apply { add(CssClasses.Border.ROUNDED_FULL) }
    // ... etc.

    // Custom class method
    fun custom(className: String) = apply { add(className) }

    // Add multiple classes at once
    fun add(vararg classNames: String) = apply { classes.addAll(classNames) }
    fun add(classNames: Set<String>) = apply { classes.addAll(classNames) }

    fun toCssString(): String = classes.joinToString(separator = " ")
    fun toSet(): Set<String> = classes.toSet()
}

fun css(block: CssBuilder.() -> Unit): String = CssBuilder().apply(block).toCssString()
fun cssSet(block: CssBuilder.() -> Unit): Set<String> = CssBuilder().apply(block).toSet()
