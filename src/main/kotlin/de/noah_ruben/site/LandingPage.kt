package de.noah_ruben.site

import de.noah_ruben.misc.CssClasses.LandingPage.ABOUT_ME
import de.noah_ruben.misc.CssClasses.LandingPage.ABOUT_ME_LINE_COLORS
import de.noah_ruben.misc.CssClasses.LandingPage.COLORS
import de.noah_ruben.misc.CssClasses.LandingPage.COLOR_GRID
import de.noah_ruben.misc.CssClasses.LandingPage.PROFILE_CONTAINER
import de.noah_ruben.misc.CssClasses.LandingPage.PROFILE_DETAILS_CONTAINER
import de.noah_ruben.misc.CssClasses.LandingPage.PROFILE_HEADER
import de.noah_ruben.misc.CssClasses.LandingPage.PROFILE_LABEL
import de.noah_ruben.misc.CssClasses.LandingPage.PROFILE_PICTURE
import de.noah_ruben.misc.CssClasses.LandingPage.SECTION_DIVIDER
import de.noah_ruben.misc.CssClasses.LandingPage.SYSTEM_SUMMARY_HEADING
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
        span(classes = "text-ctp-blue") { +">>" }
        +" noahruben"
    }
    div(classes = PROFILE_CONTAINER) {
        img(
            src = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d4/Van_Gogh_self-portrait.svg/1508px-Van_Gogh_self-portrait.svg.png",
            classes = PROFILE_PICTURE,
        )
        div(classes = PROFILE_DETAILS_CONTAINER) {
            div {
                div(classes = PROFILE_HEADER) {
                    +"Noah Ruben "
                    span(classes = SECTION_DIVIDER) { +"@" }
                    span(classes = "text-ctp-teal") { +" Darmstadt" }
                }
                hr(classes = "$MB_4 $SECTION_DIVIDER") {}
                div {
                    span(classes = PROFILE_LABEL) { +"Name" }
                    +": Noah Ruben"
                }
                div {
                    span(classes = PROFILE_LABEL) { +"Uptime" }
                    +": ${ChronoUnit.YEARS.between(startDate, LocalDate.now())} Years"
                }
                div {
                    span(classes = PROFILE_LABEL) { +"Projects" }
                    +":    "
                    githubLink()
                }
                div {
                    span(classes = PROFILE_LABEL) { +"Twitter" }
                    +" : link"
                }
                div {
                    span(classes = PROFILE_LABEL) { +"Github" }
                    +" : link"
                }
                div {
                    span(classes = PROFILE_LABEL) { +"CV" }
                    +" : link"
                }
                div {
                    span(classes = PROFILE_LABEL) { +"Twitter" }
                    +" : link"
                }
            }
            // todo click on color rect to make it "main" accsent color of page!
            // "latte" class pins Catppuccin CSS variables to Latte values regardless of dark/light mode
            div(classes = "latte $COLOR_GRID") {
                COLORS.forEach { colorClass ->
                    div(classes = colorClass) {}
                }
            }
        }
    }

    div(classes = ABOUT_ME) {
        h3(classes = SYSTEM_SUMMARY_HEADING) {
            +"System summary"
        }
        val lines = listOf(
            "🔭I'm currently working on a royal game of Ur replica in Godot",
            "It is playable here.",
            "🌱 I'm currently learning the Godot game engine",
            "📝 I participated in a #plastober: Read the blogpost here.",
            "👨‍💻 All of my projects are available",
        )
        lines.forEachIndexed { i, line ->
            span(classes = ABOUT_ME_LINE_COLORS[i % ABOUT_ME_LINE_COLORS.size]) { +line }
            br
        }
    }
    div {
        span(classes = "text-ctp-blue") { +">>" }
        +" noahruben help"
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
            span(classes = "text-ctp-blue") { +">>" }
            +" noahruben"
        }
        indexPageContent()
    }
}
