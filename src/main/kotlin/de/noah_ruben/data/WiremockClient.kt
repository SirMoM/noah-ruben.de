package de.noah_ruben.data

import de.noah_ruben.data.model.github.Repository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as CN

private const val BASE_URL = "http://website-wiremock:8080"
private const val USERS_PATH = "users"
private const val OWNER = "SirMoM"
private const val REPOSITORY_PATH = "repos"
private const val LANGUAGES_PATH = "languages"

class WiremockClient(private val url: String = BASE_URL) : RepositoryClient {

    private val newHttpClient: () -> HttpClient = {
        HttpClient(CIO) {
            install(CN) {
                json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    },
                )
            }
        }
    }

    override suspend fun getRepositories(): List<Repository> {
        newHttpClient().use {
            val req = it.get("$url/$USERS_PATH/$OWNER/$REPOSITORY_PATH")
            if (req.status != HttpStatusCode.OK) {
                throw Error(req.bodyAsText())
            } else {
                val repositories: List<Repository> = req.body()
                return@getRepositories repositories
            }
        }
    }

    override suspend fun getRepositoryLanguages(repositoryName: String, unit: () -> Unit): List<String> {
        return with(newHttpClient()) {
            val languages: Map<String, Int> = get("$url/$REPOSITORY_PATH/$OWNER/$repositoryName/$LANGUAGES_PATH").body()
            languages.keys.toList()
        }
    }
}
