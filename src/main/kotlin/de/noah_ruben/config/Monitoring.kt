package de.noah_ruben.config

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.event.Level

@Serializable
data class ApplicationInfo(
    val version: String = "TODO: Dynamic version",
    val startupTime: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC),
)

val appInfo = ApplicationInfo()

fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO
        filter { true }
    }

    routing {
        get(path = "/health") {
            call.respond(HttpStatusCode.OK, appInfo)
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
