package de.noah_ruben.site

import de.noah_ruben.misc.CssClasses
import de.noah_ruben.misc.css
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.html.respondHtml
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.html.*
import java.time.LocalDate
import java.time.temporal.ChronoUnit

val startDate: LocalDate = LocalDate.of(1999, 3, 25)

fun Application.landingPage() {
    routing {
        get("/") {
            call.respondHtml(HttpStatusCode.OK) {
                lang = "en"
                landingPageHtml() // Renamed for clarity
            }
        }
    }
}

fun BODY.indexPageContent() { // Renamed for clarity
    div(classes = css { cliOutputPrefix() }) {
        // Using new component class
        +" >> noahruben"
    }
    div(classes = css { profileLayout() }) {
        img(
            src = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d4/Van_Gogh_self-portrait.svg/1508px-Van_Gogh_self-portrait.svg.png",
            classes = css { profileImage() }, // Using new component class
        )
        div(classes = css { profileDetailsContainer() }) {
            // Using new component class
            div(classes = css { profileInfoGrid() }) {
                // Using new component class
                div { +"Noah Ruben @ Reutlingen" }
                hr { }
                div {
                    span(classes = css { profileInfoLabel() }) { +"Name" }
                    +": Noah Ruben"
                }
                div {
                    span(classes = css { profileInfoLabel() }) { +"Uptime" }
                    +": ${ChronoUnit.YEARS.between(startDate, LocalDate.now())} Years"
                }
                div {
                    span(classes = css { profileInfoLabel() }) { +"Projects" }
                    +": "
                    githubLink() // Assuming this is a defined extension function
                }
                // ... other info items ...
                div {
                    span(classes = css { profileInfoLabel() }) { +"Twitter" }
                    +" : link"
                }
                div {
                    span(classes = css { profileInfoLabel() }) { +"Github" }
                    +" : link"
                }
                div {
                    span(classes = css { profileInfoLabel() }) { +"CV" }
                    +" : link"
                }
                div {
                    span(classes = css { profileInfoLabel() }) { +"Twitter" } // Duplicate?
                    +" : link"
                }
            }
            div(classes = css { colorPaletteGrid() }) {
                // Using new component class
                val colors = listOf("rose", "red", "green", "purple", "indigo", "blue", "cyan", "teal", "emerald", "green", "lime", "yellow", "orange", "red", "gray", "black")
                repeat(16) { index ->
                    val color = colors[index % colors.size] // ensure index is within bounds
                    div(
                        classes = css {
                            colorPaletteSquareBase()
                            custom("bg-$color-500")
                        },
                    ) {}
                }
            }
        }
    }
    div(classes = css { sectionTextBlock() }) {
        // Using new component class
        +"🔭I’m currently working on a royal game of Ur replica in Godot"
        br
        +"It is playable here."
        br
        +"🌱 I’m currently learning the Godot game engine"
        br
        +"📝 I participated in a #plastober: Read the blogpost here."
        br
        +"👨‍💻 All of my projects are available"
        br
    }
    div {
        // Wrapper for CLI section
        div(classes = css { cliOutputPrefix() }) {
            +" >> noahruben help"
        }
        div(classes = css { cliHelpIndent() }) {
            // Using new component class
            p { +"Usage: noahruben <subpage>" }
            p(classes = css { cliSubpageIndent() }) { +"noahruben is the personal website of Noah Ruben" }
            p(classes = css { cliSubpageIndent() }) { +"It displays information about " }
            p(classes = css { cliSubpageIndent() }) { +"TODO" }
            h1 { +"SUB-PAGES" } // Consider styling for h1
            div(classes = css { cliSubpageIndent() }) {
                a(href = "/projects") { +" projects" }
                br()
                a(href = "https://github.com/SirMoM") { +" github" }
                br()
                a(href = "/cv") { +" cv" }
                br()
                a(href = "https://www.linkedin.com/in/noah-ruben-3013991b7") { +" linked-in" }
            }
        }
        commandLineEmulation() // Assuming this function is defined and uses new CLI component classes
    }
}

fun HTML.landingPageHtml() {
    head {
        defaultHeader()
    }
    defaultBody {
        // defaultBody now uses PAGE_BODY from Components
        id = "body"
        indexPageContent()
    }
}

// Assuming defaultBody is modified or defined like this:
@HtmlTagMarker
inline fun HTML.defaultBody(crossinline block: BODY.() -> Unit = {}): Unit = BODY(attributesMapOf("class", CssClasses.Components.PAGE_BODY.joinToString(" ")), consumer).visit(block)

// Placeholder for githubLink if not defined elsewhere
fun FlowContent.githubLink() {
    a(href = "https://github.com/SirMoM") { +"GITHUB" }
}

fun HTML.landingpage() {
    head {
        defaultHeader()
    }
    defaultBody {
        id = "body"
        div {
            +" >> noahruben"
        }
        index()
    }
}
