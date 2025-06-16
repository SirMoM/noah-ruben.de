@file:Suppress("ktlint:standard:no-wildcard-imports")

package de.noah_ruben.site.projects

import de.noah_ruben.data.Cache
import de.noah_ruben.data.Cache.getAllLanguages
import de.noah_ruben.data.Cache.getAllTopics
import de.noah_ruben.data.model.Project
import de.noah_ruben.misc.*
import de.noah_ruben.site.commandLineEmulation
import de.noah_ruben.site.defaultBody
import de.noah_ruben.site.defaultHeader
import kotlinx.html.*

fun HTML.projectsPage() {
    head {
        defaultHeader()
    }
    defaultBody {
        projectsPageBody()
    }
}

fun BODY.projectsPageBody() {
    val projects = Cache.getProjects()
    h1(
        classes = CssClasses.PAGE_TITLE,
    ) { +"> Projects" }
    div(
//        classes = css { pageBody().pageContainer() }
    ) {
        id = "search-replace"
        mainSearchBar()
        br()
        projectList(projects)
        commandLineEmulation()
    }
}

fun FlowContent.projectList(
    projects: List<Project>,
) {
    div {
        id = SEARCH_RESULTS
        // If you want a grid layout for projects, you'd apply grid classes here.
        // For example: custom("grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4")
        projects.forEach {
            projectTile(it)
        }
    }
}

fun FlowContent.projectTile(project: Project) {
    with(project) {
        // Main card container
        div(
//            classes = css {
//                cardDefault() // Base styles: border, rounded, p-4, mb-4, shadow, bg-white
//                flex()
//                flexCol()
//                custom("h-full")
//                // Add custom("h-full") if cards are in a grid and you want them to have equal height.
//                // e.g., custom("h-full")
//            },
        ) {
            div(
//                classes = css { flexGrow() }
            ) {
                // Project Name
                h3(
//                    classes = css { cardTitle() }
                ) {
                    // Uses Components.CARD_TITLE
                    +name
                }

                // Description
                p(
//                    classes = css {
//                        cardDescription() // Uses Components.CARD_DESCRIPTION
//                        textBase() // Ensure base text size
//                        custom("text-gray-700") // Explicit text color
//                        mb4()
//                    },
                ) {
                    +description
                }

                div(
//                    classes = css {
//                        textSm()
//                        custom("text-gray-600")
//                        mb4()
//                    },
                ) {
                    div(
//                        classes = css { cardMetadataMt2() }
                    ) {
                        // mt-2
                        strong(
//                            classes = css {
//                                fontSemibold()
//                                custom("text-gray-800")
//                            },
                        ) { +"Stars: " }
                        +stars.toString()
                    }
                    if (releases.isNotBlank()) {
                        div(/*classes = css { cardMetadataMt2() }*/) {
                            strong(
//                                classes = css {
//                                    fontSemibold()
//                                    custom("text-gray-800")
//                                },
                            ) { +"Released: " }
                            +releases
                        }
                    }
                    if (topics.isNotEmpty()) {
                        div(/*classes = css { cardMetadataMt2() }*/) {
                            strong(
//                                classes = css {
//                                    fontSemibold()
//                                    custom("text-gray-800")
//                                },
                            ) { +"Topics: " }
                            span { +topics.joinToString(", ") }
                        }
                    }
                }

                // Languages/Tags Section
                if (languages.isNotEmpty()) {
                    div(
//                        classes = css {
//                            flex()
//                            custom("flex-wrap") // Allow tags to wrap
//                            custom("gap-2") // Space between tags (both x and y)
//                            mb4()
//                        },
                    ) {
                        languages.forEach { lang ->
                            languageTag(lang)
                        }
                    }
                }
            }

            // Action Buttons Section (at the bottom of the card)
            // This section is within the card's default padding (p-4 from cardDefault)
            div(
//                classes = css {
//                    custom("border-t") // Add a top border for separation
//                    custom("border-gray-200") // Light border color
//                    custom("pt-4") // Padding top for this button section
//                    flex()
//                    custom("justify-start") // Align buttons to the left
//                    custom("gap-3") // Space between buttons
//                },
            ) {
                a(href = githubLink/*, classes = css { actionButtonLight() }*/) {
                    +"GitHub"
                }
                if (link.isNotBlank() && link != "#") {
                    a(href = link/*, classes = css { actionButtonLight() }*/) {
                        +"Visit"
                    }
                }
            }
        }
    }
}

fun FlowContent.languageTag(tag: String) {
    div(
//        classes = css {
//            // Margins are handled by the parent div's "gap-2" class
//            custom("bg-[#${tag.colorFromString()}]")
//            custom("text-[#${tag.colorFromString().invertedFromString()}]")
//            add(CssClasses.Border.ROUNDED_FULL) // Use add() for single class string from CssClasses
//            px3() // Horizontal padding
//            py1() // Vertical padding
//            textSm() // Small text
//            fontSemibold() // Slightly bolder text for the tag
//            custom("inline-block") // Ensures padding and margins are applied correctly
//            custom("cursor-pointer")
//        },
    ) {
        hxPost(SEARCH_PATH)
        hxTarget("#search-replace")
        hxSwap("outerHTML")
        hxIndicator("#spinner")
        hxTrigger("click")
        hxInclude("#search")
        hxVals("""{"$QP_LANGUAGE": "$tag", "$QP_WITH_SEARCHBAR": true}""")

        +tag
    }
}

fun FlowContent.mainSearchBar() {
    div {
//        classes = Components.SEARCH_INPUT_CONTAINER
        span(classes = "htmx-indicator") {
            id = "spinner"
            img(src = "/resources/bars.svg", alt = "Searching...")
            +"Searching..."
        }

        form(action = SEARCH_PATH, method = FormMethod.post) {
            id = "search"
            hxPost(SEARCH_PATH)
            hxTarget("#search-results")
            hxIndicator("#spinner")
            hxTrigger("submit, change from:select, change from:input[type='checkbox'] delay:100ms, input from:input[type='text'] changed delay:500ms")

            div(/*classes = css { flex().flexCol() }*/) {
                // Consider adding gap or margin bottom to this div
                label {
                    htmlFor = "mainSearch"
                    +"Search:"
                }
                input(
                    InputType.text,
                    name = QP_QUERY,
//                    classes = css {
//                        inputTextDefault()
//                        mb2()
//                    },
                ) {
                    // Added inputTextDefault and mb2
                    autoFocus = true
                    id = "mainSearch"
                    placeholder = "Search"
                    value = ""
                }
            }

            // Grouping filter controls for better layout potential
            div(
//                classes = css {
//                    flex()
//                    custom("flex-wrap")
//                    custom("gap-4")
//                    custom("items-end")
//                    mb4()
//                },
            ) {
                div {
                    label(/*classes = css { labelDefault() }*/) {
                        // Added labelDefault
                        htmlFor = QP_TOPIC
                        +"Topic:"
                    }
                    select(/*classes = css { selectDefault() }*/) {
                        // Added selectDefault
                        name = QP_TOPIC
                        id = QP_TOPIC
                        option {
                            value = TOPIC_PLACEHOLDER
                            selected = true
                            +TOPIC_PLACEHOLDER
                        }
                        getAllTopics().forEach { topic ->
                            option {
                                value = topic
                                +topic
                            }
                        }
                    }
                }

                div(/*classes = css { flexCol() }*/) {
                    label(/*classes = css { labelDefault() }*/) {
                        htmlFor = QP_LANGUAGE
                        +"Language:"
                    }
                    select(/*classes = css { selectDefault() }*/) {
                        name = QP_LANGUAGE
                        id = QP_LANGUAGE
                        option {
                            value = LANGUAGE_PLACEHOLDER
                            selected = true
                            +LANGUAGE_PLACEHOLDER
                        }
                        getAllLanguages().forEach { language ->
                            option {
                                value = language
                                +language
                            }
                        }
                    }
                }

                div(/*classes = css { flexCol() }*/) {
                    label(/*classes = css { labelDefault() }*/) {
                        htmlFor = QP_ORDER_BY
                        +"Order by:"
                    }
                    select(/*classes = css { selectDefault() }*/) {
                        name = QP_ORDER_BY
                        id = QP_ORDER_BY
                        OrderBy.entries.forEach {
                            option {
                                value = it.name
                                +it.name
                            }
                        }
                    }
                }

                div(
//                    classes = css {
//                        flex()
//                        itemsCenter()
//                        mt2()
//                    },
                ) {
                    // Align checkbox and label
                    input(
                        type = InputType.checkBox,
                        name = QP_DIR,
//                        classes = css {
//                            checkboxDefault()
//                            mr1()
//                        },
                    ) {
                        // Added checkboxDefault and mr1
                        id = QP_DIR
                        value = "desc" // This should probably be "true" or "desc" and handled server-side
                        // For a "descending" checkbox, it's common to check its presence.
                        // If you want it to toggle between "asc" and "desc", you'd need more logic.
                    }
                    label {
                        htmlFor = QP_DIR
                        +"Descending"
                    }
                }
            }

            button(type = ButtonType.submit /* classes = css { buttonPrimary()}*/) {
                // Added buttonPrimary
                div(
//                    classes = css { flex().itemsCenter() }
                ) {
                    +"Search "
                    span(
//                        classes = css { custom("htmx-indicator").ml2() }
                    ) {
                        img(src = "/resources/bars.svg", alt = "Searching...")
                    }
                }
            }
        }
    }
}
