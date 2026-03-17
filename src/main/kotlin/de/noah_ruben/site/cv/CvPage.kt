@file:Suppress("ktlint:standard:no-wildcard-imports")

package de.noah_ruben.site.cv

import de.noah_ruben.misc.CssClasses.PAGE_TITLE
import de.noah_ruben.misc.CssClasses.Shared.HELP_INDENT
import de.noah_ruben.misc.CssClasses.Shared.SUBPAGE_INDENT
import de.noah_ruben.site.commandLineEmulation
import de.noah_ruben.site.defaultBody
import de.noah_ruben.site.defaultHeader
import de.noah_ruben.site.themeToggleButton
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.html.respondHtml
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.html.*

fun Application.cvPageRouting() {
    routing {
        get("/cv") {
            call.respondHtml(HttpStatusCode.OK) {
                lang = "en"
                cvPageHtml()
            }
        }
    }
}

fun HTML.cvPageHtml() {
    head {
        defaultHeader()
    }
    defaultBody {
        themeToggleButton()
        cvPageBody()
    }
}

fun BODY.cvPageBody() {
    h1(classes = PAGE_TITLE) { +"> CV" }
    div {
        span { +"TODO: Add CV content" }
    }
    div {
        div(classes = HELP_INDENT) {
            p { +"Usage: noahruben cv" }
            p { +"Curriculum vitae — work experience, education, and skills." }
            div(classes = SUBPAGE_INDENT) {
                a(href = "/") { +" home" }
                br()
                a(href = "/projects") { +" projects" }
            }
        }
    }
    commandLineEmulation()
}
