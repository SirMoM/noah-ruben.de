package de.noah_ruben.data

import de.noah_ruben.data.model.github.Repository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as CN

private const val USERS_PATH = "users"
private const val OWNER = "SirMoM"
private const val REPOSITORY_PATH = "repos"
private const val LANGUAGES_PATH = "languages"

interface RepositoryClient {
    suspend fun getRepositories(): List<Repository>
    suspend fun getRepositoryLanguages(repositoryName: String, unit: () -> Unit): List<String>
}

fun HttpRequestBuilder.authHeader(token: String) {
    headers.append("Authorization", "Bearer $token")
}

class GitHubClient(private val token: String) : RepositoryClient {
    private val baseUrl = "https://api.github.com"

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
            val req = it.get("$baseUrl/$USERS_PATH/$OWNER/$REPOSITORY_PATH") {
                headers.append("Authorization", "Bearer $token")
            }
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
            val languages: Map<String, Int> = get("$baseUrl/$REPOSITORY_PATH/$OWNER/$repositoryName/$LANGUAGES_PATH") {
                authHeader(token)
            }.body()
            languages.keys.toList()
        }
    }
}
