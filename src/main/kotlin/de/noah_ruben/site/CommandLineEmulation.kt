package de.noah_ruben.site

import de.noah_ruben.misc.Commands
import de.noah_ruben.misc.CssClasses.LandingPage.PROMPT_ARROW
import de.noah_ruben.misc.CssClasses.Shared.CLI_INPUT_FIELD
import de.noah_ruben.misc.CssClasses.Shared.CLI_WRAPPER
import de.noah_ruben.misc.hxPost
import de.noah_ruben.misc.hxSwap
import de.noah_ruben.misc.hxTarget
import de.noah_ruben.misc.parseCommand
import de.noah_ruben.site.cv.CvLanguage
import de.noah_ruben.site.cv.buildCvPageState
import de.noah_ruben.site.cv.cvPageHtml
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
import kotlinx.html.span
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
        val (command, args) = parseCommand(rawCommand)
        when (command) {
            Commands.landingPage -> htmlBase.html { landingpage() }

            Commands.projects -> htmlBase.html {
                head {
                    defaultHeader()
                }
                body {
                    themeToggleButton()
                    projectsPageBody()
                }
            }

            Commands.cv -> {
                val language = try {
                    CvLanguage.fromCommandArguments(args)
                } catch (error: IllegalArgumentException) {
                    call.response.header("HX-Retarget", "#cle")
                    return@respondText htmlBase.div {
                        br
                        div {
                            +">> noahruben cv: ${error.message}"
                        }
                        commandLineEmulation()
                    }
                }

                htmlBase.html {
                    cvPageHtml(
                        buildCvPageState(
                            config = call.application.environment.config,
                            language = language,
                        ),
                    )
                }
            }

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
                div {
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
    div(classes = CLI_WRAPPER) {
        id = "cle"
        span(classes = PROMPT_ARROW) { +">>" }
        +" "
        input(type = InputType.text, name = "command", classes = CLI_INPUT_FIELD) {
            placeholder = "noahruben projects"
            autoComplete = "off"
            autoFocus = true
            attributes["spellcheck"] = "false"

            hxPost("/command")
            hxTarget("#body")
            hxSwap("outerHTML")
        }
    }
}

fun FlowContent.cleUsage() {
    div {
        p {
            +"Usage: noahruben <subpage>"
        }
        p {
            +"noahruben is the personal website of Noah Ruben"
        }
        p {
            +"It displays information about Noah Ruben: full-stack development, open source, and game dev interests."
        }

        h1 { +"SUB-PAGES" }

        div {
            selfLink("/projects", "projects")
            br
            selfLink("https://github.com/SirMoM", "github")
            br
            selfLink("https://www.linkedin.com/in/noah-ruben-3013991b7", "linked-in")
        }
    }
}
