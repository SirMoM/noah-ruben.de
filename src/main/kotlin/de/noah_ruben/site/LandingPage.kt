package de.noah_ruben.site

import de.noah_ruben.misc.css
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.html.respondHtml
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.html.BODY
import kotlinx.html.HTML
import kotlinx.html.HtmlTagMarker
import kotlinx.html.attributesMapOf
import kotlinx.html.br
import kotlinx.html.div
import kotlinx.html.head
import kotlinx.html.hr
import kotlinx.html.id
import kotlinx.html.img
import kotlinx.html.lang
import kotlinx.html.span
import kotlinx.html.visit
import java.time.LocalDate
import java.time.temporal.ChronoUnit

val startDate: LocalDate = LocalDate.of(1999, 3, 25)

fun Application.landingPage() {
    routing {
        get("/") {
            call.respondHtml(HttpStatusCode.OK) {
                lang = "en"
                landingpage()
            }
        }
    }
}

fun BODY.index() {
    div(classes = css { flex().itemsCenter() }) {
        img(
            src = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d4/Van_Gogh_self-portrait.svg/1508px-Van_Gogh_self-portrait.svg.png",
            classes = css { custom("m-4").custom("aspect-square").custom("w-1/4").roundedFull().custom("bg-yellow-500") },
        )
        div(classes = css { custom("w-3/4").custom("xl:text-xl").custom("md:text-xl") }) {
            div(classes = css { custom("grid").custom("grid-cols-1").custom("m-4").custom("space-y-1") }) {
                div { +"Noah Ruben @ Reutlingen" }
                hr { }
                div {
                    span(classes = css { textRed500() }) { +"Name" }
                    +": Noah Ruben"
                }
                div {
                    span(classes = css { textRed500() }) { +"Uptime" }
                    +": ${ChronoUnit.YEARS.between(startDate, LocalDate.now())} Years"
                }
                div {
                    span(classes = css { textRed500() }) { +"Projects" }
                    +": "
                    githubLink()
                }
                div {
                    span(classes = css { textRed500() }) { +"Twitter" }
                    +" : link"
                }
                div {
                    span(classes = css { textRed500() }) { +"Github" }
                    +" : link"
                }
                div {
                    span(classes = css { textRed500() }) { +"CV" }
                    +" : link"
                }
                div {
                    span(classes = css { textRed500() }) { +"Twitter" }
                    +" : link"
                }
            }
            div(classes = css { custom("inline-grid").custom("flex-none").custom("grid-cols-8").custom("grid-rows-2") }) {
                repeat(16) {
                    val color = it.let { index -> listOf("rose", "red", "green", "purple", "indigo", "blue", "cyan", "teal", "emerald", "green", "lime", "yellow", "orange", "red", "gray", "black")[index] }
                    div(classes = css { border().borderBlack().custom("hover:border-2").h10().w10().custom("bg-$color-500") }) {}
                }
            }
        }
    }
    div(classes = css { custom("IDK") }) {
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
        cleUsage()
        commandLineEmulation()
    }
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

@HtmlTagMarker
inline fun HTML.defaultBody(crossinline block: BODY.() -> Unit = {}): Unit = BODY(attributesMapOf("class", css { darkBgGray900().textWhite() }), consumer).visit(block)
