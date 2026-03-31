package de.noah_ruben.site

import de.noah_ruben.misc.Commands
import de.noah_ruben.misc.CssClasses.PAGE_BASE
import de.noah_ruben.misc.CssClasses.ProjectPage.PROJECT_COMMAND_PROMPT
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
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.id
import kotlinx.html.input
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
            Commands.landingPage -> {
                call.response.header("HX-Push-Url", "/")
                htmlBase.html { landingpage() }
            }

            Commands.projects -> htmlBase.html {
                call.response.header("HX-Push-Url", "/projects")
                head {
                    defaultHeader()
                }
                body {
                    id = "body"
                    classes = setOf(PAGE_BASE)
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

                call.response.header("HX-Push-Url", language.pageUrl())
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
        span(classes = PROJECT_COMMAND_PROMPT) { +">>" }
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
        websiteHelpContent(links = cliHelpLinks())
    }
}
