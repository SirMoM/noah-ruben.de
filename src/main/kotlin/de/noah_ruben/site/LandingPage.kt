package de.noah_ruben.site

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
    div("flex items-center") {
        img(src = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d4/Van_Gogh_self-portrait.svg/1508px-Van_Gogh_self-portrait.svg.png", classes = "m-4 aspect-square w-1/4 rounded-full bg-yellow-500")
        div("w-3/4 xl:text-xl md:text-xl") {
            div("grid grid-cols-1 m-4 space-y-1") {
                div { +"Noah Ruben @ Reutlingen" }
                hr { }
                div {
                    span(classes = "text-red-500") { +"Name" }
                    +": Noah Ruben"
                }
                div {
                    span(classes = "text-red-500") { +"Uptime" }
                    +": ${ChronoUnit.YEARS.between(startDate, LocalDate.now())} Years"
                }
                div {
                    span(classes = "text-red-500") { +"Projects" }
                    +": "
                    githubLink()
                }
                div {
                    span(classes = "text-red-500") { +"Twitter" }
                    +" : link"
                }
                div {
                    span(classes = "text-red-500") { +"Github" }
                    +" : link"
                }
                div {
                    span(classes = "text-red-500") { +"CV" }
                    +" : link"
                }
                div {
                    span(classes = "text-red-500") { +"Twitter" }
                    +" : link"
                }
            }
            div("inline-grid flex-none grid-cols-8 grid-rows-2") {
                repeat(16) {
                    div(classes = "border border-black hover:border-2 h-10 w-10 bg-${it.let { index -> listOf("rose", "red", "green", "purple", "indigo", "blue", "cyan", "teal", "emerald", "green", "lime", "yellow", "orange", "red", "gray", "black")[index] }}-500") {}
                }
            }
        }
    }
    div("IDK") {
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
inline fun HTML.defaultBody(crossinline block: BODY.() -> Unit = {}): Unit = BODY(attributesMapOf("class", "dark:bg-gray-900 dark:text-white"), consumer).visit(block)
