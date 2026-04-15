package de.noah_ruben.config

import de.noah_ruben.data.Cache
import de.noah_ruben.site.cv.cvAssetsHealthCheck
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import java.util.UUID

private const val DEFAULT_APP_VERSION = "dev"
private const val DEFAULT_DEBUG_HEALTH_POLL_INTERVAL_MS = 1500
private val applicationStartupTime: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC)
private val applicationBootId: String = UUID.randomUUID().toString()

@Serializable
data class ApplicationInfo(
    val version: String,
    val bootId: String,
    val startupTime: LocalDateTime,
    val debugHealthPollIntervalMs: Int,
    val overallStatus: String = HEALTH_OK,
    val checks: Map<String, HealthCheckResult> = emptyMap(),
)

fun Application.configureMonitoring() {
    routing {
        get("/health") {
            val checks = linkedMapOf(
                "application" to HealthCheckResult(
                    status = HEALTH_OK,
                    message = "Application is running.",
                ),
                "cvAssets" to cvAssetsHealthCheck(environment.config),
                "cache" to Cache.healthCheck(),
            )

            call.respond(
                HttpStatusCode.OK,
                applicationInfo().copy(
                    overallStatus = overallHealthStatus(checks),
                    checks = checks,
                ),
            )
        }
    }
}

private fun Application.applicationInfo(): ApplicationInfo = ApplicationInfo(
    version = environment.config.appVersion(),
    bootId = applicationBootId,
    startupTime = applicationStartupTime,
    debugHealthPollIntervalMs = environment.config.debugHealthPollIntervalMs(),
)

private fun ApplicationConfig.appVersion(): String = propertyOrNull("app.version")
    ?.getString()
    ?.trim()
    ?.takeUnless(String::isBlank)
    ?: DEFAULT_APP_VERSION

private fun ApplicationConfig.debugHealthPollIntervalMs(): Int = propertyOrNull("debug.healthPollIntervalMs")
    ?.getString()
    ?.trim()
    ?.toIntOrNull()
    ?.takeIf { it > 0 }
    ?: DEFAULT_DEBUG_HEALTH_POLL_INTERVAL_MS

//    install(DropwizardMetrics) {
//        Slf4jReporter.forRegistry(registry)
//            .outputTo(Logger)
//            .convertRatesTo(TimeUnit.SECONDS)
//            .convertDurationsTo(TimeUnit.MILLISECONDS)
//            .build()
//            .start(10, TimeUnit.SECONDS)
//    }
// }
