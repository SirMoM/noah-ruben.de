package de.noah_ruben.site

import de.noah_ruben.misc.CssClasses.LandingPage.ABOUT_ME
import de.noah_ruben.misc.CssClasses.LandingPage.COLORS
import de.noah_ruben.misc.CssClasses.LandingPage.COLOR_GRID
import de.noah_ruben.misc.CssClasses.LandingPage.PROFILE_CONTAINER
import de.noah_ruben.misc.CssClasses.LandingPage.PROFILE_DETAILS_CONTAINER
import de.noah_ruben.misc.CssClasses.LandingPage.PROFILE_PICTURE
import de.noah_ruben.misc.CssClasses.MB_4
import de.noah_ruben.misc.CssClasses.PAGE_BASE
import de.noah_ruben.misc.CssClasses.Shared.HELP_INDENT
import de.noah_ruben.misc.CssClasses.Shared.SUBPAGE_INDENT
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
                landingPageHtml()
            }
        }
    }
}

fun BODY.indexPageContent() {
    div {
        +" >> noahruben"
    }
    div(classes = PROFILE_CONTAINER) {
        img(
            src = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d4/Van_Gogh_self-portrait.svg/1508px-Van_Gogh_self-portrait.svg.png",
            classes = PROFILE_PICTURE,
        )
        div(classes = PROFILE_DETAILS_CONTAINER) {
            div {
                div { +"Noah Ruben @ Reutlingen" }
                hr(MB_4) {}
                div {
                    span { +"Name" }
                    +": Noah Ruben"
                }
                div {
                    span { +"Uptime" }
                    +": ${ChronoUnit.YEARS.between(startDate, LocalDate.now())} Years"
                }
                div {
                    span { +"Projects" }
                    p {
                        +":    "
                        githubLink()
                    }
                }
                div {
                    span { +"Twitter" }
                    +" : link"
                }
                div {
                    span { +"Github" }
                    +" : link"
                }
                div {
                    span { +"CV" }
                    +" : link"
                }
                div {
                    span { +"Twitter" }
                    +" : link"
                }
            }
            // todo click on color rect to make it "main" accsent color of page!
            div(classes = COLOR_GRID) {
                COLORS.forEach { colorClass ->
                    div(classes = colorClass)
                }
            }
        }
    }

    div(classes = ABOUT_ME) {
        h3 {
            +"System summary"
        }
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
        +" >> noahruben help"
    }
    div(classes = HELP_INDENT) {
        p { +"Usage: noahruben <subpage>" }
        p { +"noahruben is the personal website of Noah Ruben" }
        p { +"It displays information about " }
        p { +"TODO" }
        h1 { +"SUB-PAGES" }
        div(classes = SUBPAGE_INDENT) {
            a(href = "/projects") { +" projects" }
            br()
            a(href = "https://github.com/SirMoM") { +" github" }
            br()
            a(href = "/cv") { +" cv" }
            br()
            a(href = "https://www.linkedin.com/in/noah-ruben-3013991b7") { +" linked-in" }
        }
    }
    commandLineEmulation()
}

fun HTML.landingPageHtml() {
    head {
        defaultHeader()
    }
    defaultBody {
        id = "body"
        classes = setOf(PAGE_BASE)
        themeToggleButton()
        indexPageContent()
    }
}

@HtmlTagMarker
inline fun HTML.defaultBody(crossinline block: BODY.() -> Unit = {}): Unit = BODY(emptyMap(), consumer).visit(block)

fun FlowContent.githubLink() {
    a(href = "https://github.com/SirMoM") { +"GITHUB" }
}

fun HTML.landingpage() {
    head {
        defaultHeader()
    }
    defaultBody {
        id = "body"
        classes = setOf(PAGE_BASE)
        themeToggleButton()
        div {
            +" >> noahruben"
        }
        indexPageContent()
    }
}
