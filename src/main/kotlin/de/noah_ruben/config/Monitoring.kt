package de.noah_ruben.config

import de.noah_ruben.data.Cache
import de.noah_ruben.site.cv.cvAssetsHealthCheck
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class ApplicationInfo(
    val version: String = "TODO: Dynamic version",
    val startupTime: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC),
    val overallStatus: String = HEALTH_OK,
    val checks: Map<String, HealthCheckResult> = emptyMap(),
)

val appInfo = ApplicationInfo()

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
                appInfo.copy(
                    overallStatus = overallHealthStatus(checks),
                    checks = checks,
                ),
            )
        }
    }
}
//    install(DropwizardMetrics) {
//        Slf4jReporter.forRegistry(registry)
//            .outputTo(Logger)
//            .convertRatesTo(TimeUnit.SECONDS)
//            .convertDurationsTo(TimeUnit.MILLISECONDS)
//            .build()
//            .start(10, TimeUnit.SECONDS)
//    }
// }
