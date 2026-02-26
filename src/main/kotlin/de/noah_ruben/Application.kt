package de.noah_ruben

import de.noah_ruben.config.configureHTTP
import de.noah_ruben.config.configureMonitoring
import de.noah_ruben.config.exceptionHandling
import de.noah_ruben.data.Cache
import de.noah_ruben.data.FakeRepositoryClient
import de.noah_ruben.data.GitHubClient
import de.noah_ruben.data.RepositoryClient
import de.noah_ruben.data.WiremockClient
import de.noah_ruben.site.commandLineEmulation
import de.noah_ruben.site.defaultBody
import de.noah_ruben.site.defaultHeader
import de.noah_ruben.site.landingPage
import de.noah_ruben.site.projects.projectsPageRouting
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.html.respondHtml
import io.ktor.server.http.content.staticResources
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.html.*
import kotlinx.serialization.json.Json

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(repositoryClient: RepositoryClient = createRepositoryClient()) {
    Cache.githubClient = repositoryClient
    Cache.initialize()

    configuringSerialization()
    exceptionHandling()
    configureHTTP()
    configureMonitoring()

    // Routing
    landingPage()
    commandLineEmulation()
    staticRouting()
    projectsPageRouting()
}

fun Application.staticRouting() {
    routing {
        staticResources("/resources", "static") {
            enableAutoHeadResponse()
        }

        get("/favicon.ico") {
            call.respondRedirect("/resources/favicon.ico", permanent = false)
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

fun Application.configuringSerialization() {
    this.install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
                encodeDefaults = true
            },
        )
    }
}

private fun Application.getToken(): String {
    val token = environment.config.propertyOrNull("github.token")?.getString()?.trim()
    if (token.isNullOrBlank()) throw IllegalStateException("Did not provide github token as GITHUB_TOKEN in the environment.")
    return token
}

private fun Application.getGithubURL(): String {
    val url = environment.config.propertyOrNull("github.url")?.getString()?.trim()
    if (url.isNullOrBlank()) throw IllegalStateException("Did not provide github URL as GITHUB_URL in the environment.")
    return url
}

private fun Application.createRepositoryClient(): RepositoryClient {
    val mode = environment.config.propertyOrNull("github.mode")?.getString()?.trim()
        ?: throw IllegalStateException("github.mode is not configured. Set GITHUB_MODE to 'github', 'wiremock', or 'fake'.")
    return when (mode) {
        "github" -> GitHubClient(token = getToken())
        "wiremock" -> WiremockClient(url = getGithubURL())
        "fake" -> FakeRepositoryClient()
        else -> throw IllegalStateException("Unknown github.mode '$mode'. Valid values: github, wiremock, fake.")
    }
}
