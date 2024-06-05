package de.noah_ruben

import de.noah_ruben.config.configureHTTP
import de.noah_ruben.config.configureMonitoring
import de.noah_ruben.config.exceptionHandling
import de.noah_ruben.data.Cache
import de.noah_ruben.data.WiremockClient
import de.noah_ruben.site.commandLineEmulation
import de.noah_ruben.site.defaultBody
import de.noah_ruben.site.defaultHeader
import de.noah_ruben.site.landingPage
import de.noah_ruben.site.projectsPage
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.html.respondHtml
import io.ktor.server.http.content.staticResources
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.html.head

// fun main() {
//    embeddedServer(Netty, port = 42080, host = "0.0.0.0", module = Application::module, watchPaths = listOf("classes")).start(wait = true)
// }

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.staticRouting() {
    routing {
        staticResources("/resources", "static") {
//            enableAutoHeadResponse()
        }

        get("/gh") {
            call.response.status(HttpStatusCode.OK)
            call.respondHtml {
                head {
                    defaultHeader()
                }
                defaultBody {
                    +"asd"
                }
            }
        }

        get("/error") {
            throw RuntimeException("ERROR PAGE!")
        }
    }
}

fun Application.module() {
    Cache.githubClient = WiremockClient(url = getGithubURL())
    Cache.initialize()

    configureHTTP()
    configureMonitoring()
    exceptionHandling()

    // Routing
    landingPage()
    commandLineEmulation()
    staticRouting()
    projectsPage()
}

private fun Application.getToken(): String {
    val tokenConfig = environment.config.propertyOrNull("github.token") ?: throw IllegalStateException("Did not provide github token as GITHUB_TOKEN in the environment.")
    return tokenConfig.getString()
}
private fun Application.getGithubURL(): String {
    val url = environment.config.propertyOrNull("github.url") ?: throw IllegalStateException("Did not provide github URL as GITHUB_URL in the environment.")
    return url.getString().trim()
}
