package de.noah_ruben.config

import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.uri
import io.ktor.server.response.header
import io.ktor.server.response.respond
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

@Serializable
data class Problem(
    val title: String,
    val status: Int,
    val detail: String?,
    val stackTrace: String?,
) {
    constructor(httpStatusCode: HttpStatusCode, msg: String? = null, stackTrace: String? = null) : this(
        title = httpStatusCode.description,
        status = httpStatusCode.value,
        detail = msg,
        stackTrace = stackTrace,
    )
}

fun createProblem(throwable: Throwable): Problem {
    return when (throwable::class) {
        RuntimeException::class -> Problem(InternalServerError, throwable.message, throwable.stackTraceToString())
        IllegalArgumentException::class -> Problem(BadRequest, throwable.message, throwable.stackTraceToString())
        else -> Problem(InternalServerError, throwable.message, throwable.localizedMessage.trim())
    }
}

fun Application.exceptionHandling() {
    this.install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
            },
        )
    }
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            val logger = LoggerFactory.getLogger("ERROR")
            logger.error(call.request.uri, cause)
            val problem: Problem = createProblem(cause)
            call.response.status(InternalServerError)
            call.response.header("Content-Type", "application/problem+json")
            call.respond(problem)
        }
    }
}
