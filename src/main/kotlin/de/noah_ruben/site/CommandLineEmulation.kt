package de.noah_ruben.site

import de.noah_ruben.misc.Commands
import de.noah_ruben.misc.hxPost
import de.noah_ruben.misc.hxSwap
import de.noah_ruben.misc.hxTarget
import de.noah_ruben.misc.parseCommand
import io.ktor.server.application.Application
import io.ktor.server.request.receiveText
import io.ktor.server.response.header
import io.ktor.server.response.respondText
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.html.FlowContent
import kotlinx.html.InputType
import kotlinx.html.body
import kotlinx.html.br
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.p
import kotlinx.html.stream.createHTML
import java.net.URLDecoder
import java.nio.charset.Charset

fun Application.commandLineEmulation() {
    routing {
        post("/command") {
            handleCommand(call)
        }
    }
}

suspend fun handleCommand(call: RoutingCall) {
    val rawCommand = call.receiveText()

    val htmlBase = createHTML(prettyPrint = true)
    val cmd = URLDecoder.decode(rawCommand, Charset.defaultCharset()).replace("command=", "").trim()

    call.respondText {
        when (parseCommand(rawCommand)) {
            Commands.landingPage -> htmlBase.html { landingpage() }
            Commands.projects -> htmlBase.html {
                head {
                    defaultHeader()
                }
                body {
                    projectsPageBody()
                }
            }
            Commands.cv -> throw RuntimeException("NOT IMPLEMENTED!")
            Commands.unknownSubpage -> htmlBase.div {
                call.response.header("HX-Retarget", "#cle")
                br
                div {
                    +">> noahruben: Page '${cmd.replace("noahruben", "")}' not found"
                }
                cleUsage()
                commandLineEmulation()
            }
            Commands.unknownCommand -> htmlBase.div {
                call.response.header("HX-Retarget", "#cle")
                br
                div("ml-4") {
                    +">> $cmd: command not found"
                }
                commandLineEmulation()
            }

            Commands.help -> {
                call.response.header("HX-Retarget", "#cle")
                htmlBase.div {
                    cleUsage()
                    commandLineEmulation()
                }
            }
        }
    }
}

fun FlowContent.commandLineEmulation() {
    div("w-full inline-flex dark:bg-gray-900 dark:text-white outline-none focus:outline-none p-4") {
        id = "cle"
        div {
            +">>\u00A0"
        }
        // TODO add htmx functionality
        // TODO Add tab functionality
        input(
            name = "command",
            type = InputType.text,
            classes = "w-full dark:bg-gray-900 border-none outline-none",
        ) {
            placeholder = "noahruben projects"
            attributes["autocomplete"] = "off"
            attributes["spellcheck"] = "false"
            attributes["autoFocus"] = "true"

            hxPost("/command")
            hxTarget("#body")
            hxSwap("outerHTML")
        }
    }
}

fun FlowContent.cleUsage() {
    div(classes = "ml-6") {
        p {
            +"Usage: noahruben <subpage>"
        }
        p(classes = "ml-4") {
            +"noahruben is the personal website of Noah Ruben"
        }
        p(classes = "ml-4") {
            +"It displays information about "
        }
        p(classes = "ml-4") {
            +"TODO"
        }

        h1 { +"SUB-PAGES" }

        div(classes = "ml-4") {
            selfLink("/projects", "projects")
            br
            selfLink("https://github.com/SirMoM", "github")
            br
            selfLink("/cv", "cv")
            br
            selfLink("https://www.linkedin.com/in/noah-ruben-3013991b7", "linked-in")
        }
    }
}
