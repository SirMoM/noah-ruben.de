package de.noah_ruben.site

import de.noah_ruben.misc.Commands
import de.noah_ruben.misc.CssClasses
import de.noah_ruben.misc.CssClasses.Layout.ML_4
import de.noah_ruben.misc.CssClasses.Layout.ML_6
import de.noah_ruben.misc.css
import de.noah_ruben.misc.parseCommand
import de.noah_ruben.site.projects.projectsPageBody
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
import kotlinx.html.classes
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
                body(classes = css { container() }) {
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
                div(classes = css { cliPromptSymbol() }) {
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
    div(classes = css { cliInputWrapper() }) {
        id = "cle" // Keep id if used by htmx or JS
        div(classes = css { cliPromptSymbol() }) { +">> " }
        input(type = InputType.text, name = "command") {
            classes = CssClasses.Components.CLI_INPUT_FIELD.toSet()
            placeholder = "noahruben projects"
            autoComplete = false
            attributes["spellcheck"] = "false"
            autoFocus = true
            attributes["hx-post"] = "/command"
            attributes["hx-target"] = "#body"
            attributes["hx-swap"] = "outerHTML"
        }
    }
}

fun FlowContent.cleUsage() {
    div(classes = css { ML_6 }) {
        p {
            +"Usage: noahruben <subpage>"
        }
        p(
            classes = css {
                ML_4
            },
        ) {
            +"noahruben is the personal website of Noah Ruben"
        }
        p(classes = css { ML_4 }) {
            +"It displays information about "
        }
        p(classes = css { ML_4 }) {
            +"TODO"
        }

        h1 { +"SUB-PAGES" }

        div(classes = css { ML_4 }) {
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
