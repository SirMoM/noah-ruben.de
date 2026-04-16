package de.noah_ruben.site

import de.noah_ruben.misc.CssClasses.LandingPage.ABOUT_ME
import de.noah_ruben.misc.CssClasses.LandingPage.ABOUT_ME_LINE_COLORS
import de.noah_ruben.misc.CssClasses.LandingPage.COLORS_WITH_HEX
import de.noah_ruben.misc.CssClasses.LandingPage.COLOR_GRID_LATTE
import de.noah_ruben.misc.CssClasses.LandingPage.LOCATION_TEXT
import de.noah_ruben.misc.CssClasses.LandingPage.PROFILE_CONTAINER
import de.noah_ruben.misc.CssClasses.LandingPage.PROFILE_DETAILS_CONTAINER
import de.noah_ruben.misc.CssClasses.LandingPage.PROFILE_DIVIDER
import de.noah_ruben.misc.CssClasses.LandingPage.PROFILE_HEADER
import de.noah_ruben.misc.CssClasses.LandingPage.PROFILE_LABEL
import de.noah_ruben.misc.CssClasses.LandingPage.PROFILE_PICTURE_IMAGE
import de.noah_ruben.misc.CssClasses.LandingPage.PROFILE_PICTURE_LINK
import de.noah_ruben.misc.CssClasses.LandingPage.SECTION_DIVIDER
import de.noah_ruben.misc.CssClasses.LandingPage.SYSTEM_SUMMARY_HEADING
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
    commandPrompt(
        command = "noahruben",
        containerRole = "landing-command",
    )
    div(classes = PROFILE_CONTAINER) {
        vanGoghPortrait()
        div(classes = PROFILE_DETAILS_CONTAINER) {
            div {
                div(classes = PROFILE_HEADER) {
                    +"Noah Ruben "
                    span(classes = SECTION_DIVIDER) { +"@" }
                    span(classes = LOCATION_TEXT) { +" Darmstadt" }
                }
                hr(classes = PROFILE_DIVIDER) {}
                div {
                    span(classes = PROFILE_LABEL) { +"Name" }
                    +": Noah Ruben"
                }
                div {
                    span(classes = PROFILE_LABEL) { +"Uptime" }
                    +": ${ChronoUnit.YEARS.between(startDate, LocalDate.now())} Years"
                }
                div {
                    span(classes = PROFILE_LABEL) { +"Role" }
                    +": Full-Stack Developer @ "
                    a(href = "https://www.karriere-atlas.de/") { +"ATLAS" }
                }
                div {
                    span(classes = PROFILE_LABEL) { +"GitHub" }
                    +": "
                    a(href = "https://github.com/SirMoM") { +"SirMoM" }
                }
                div {
                    span(classes = PROFILE_LABEL) { +"LinkedIn" }
                    +": "
                    a(href = "https://www.linkedin.com/in/noah-ruben-3013991b7") { +"noah-ruben" }
                }
                div {
                    span(classes = PROFILE_LABEL) { +"Mail" }
                    +": "
                    a(href = "mailto:noah-ruben@pm.me") { +"noah-ruben@pm.me" }
                }
            }
            // click on a color swatch to set the accent color for the page
            div(classes = COLOR_GRID_LATTE) {
                COLORS_WITH_HEX.forEach { (colorClass, hex) ->
                    div(classes = colorClass) {
                        attributes["data-accent"] = hex
                        onClick = "window.setAccentColor && window.setAccentColor('$hex')"
                    }
                }
            }
        }
    }

    div(classes = ABOUT_ME) {
        h3(classes = SYSTEM_SUMMARY_HEADING) {
            +"System summary"
        }
        val lines = listOf(
            Pair("Languages", "Kotlin · Go · TypeScript · Java"),
            Pair("Frameworks", "Ktor · Angular · HTMX"),
            Pair("Focus", "Full-stack systems, game dev, open source"),
            Pair("Currently", "Building a Royal Game of Ur replica in Godot"),
            Pair("Projects", "Available on GitHub"),
        )
        lines.forEachIndexed { i, (label, value) ->
            div {
                span(classes = ABOUT_ME_LINE_COLORS[i % ABOUT_ME_LINE_COLORS.size]) { +label }
                +": $value"
            }
        }
    }
    commandPrompt(
        command = "noahruben help",
        containerRole = "landing-help-command",
    )
    div(classes = HELP_INDENT) {
        websiteHelpContent(
            links = landingHelpLinks(),
            subpageClasses = SUBPAGE_INDENT,
        )
    }
    commandLineEmulation()
}

private fun FlowContent.vanGoghPortrait() {
    a(
        href = "https://commons.wikimedia.org/wiki/File:Van_Gogh_self-portrait.svg",
        classes = PROFILE_PICTURE_LINK,
    ) {
        title = "Vincent van Gogh, Public domain, via Wikimedia Commons"
        img(
            src = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d4/Van_Gogh_self-portrait.svg/120px-Van_Gogh_self-portrait.svg.png",
            alt = "Self-portrait of Vincent van Gogh, vector traced",
            classes = PROFILE_PICTURE_IMAGE,
        )
    }
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

inline fun HTML.defaultBody(crossinline block: BODY.() -> Unit = {}): Unit = BODY(emptyMap(), consumer).visit(block)

fun HTML.landingpage() {
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
