package de.noah_ruben

import de.noah_ruben.config.ApplicationInfo
import de.noah_ruben.data.FakeRepositoryClient
import io.ktor.client.request.header
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectories
import kotlin.io.path.createTempDirectory
import kotlin.io.path.div
import kotlin.io.path.writeBytes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import java.util.UUID

fun testApplicationWithRepositoryFake(
    configOverrides: Map<String, String> = emptyMap(),
    block: suspend ApplicationTestBuilder.() -> Unit,
) = testApplication {
    environment {
        val baseConfig = ApplicationConfig("application-test.yaml")
        val config = MapApplicationConfig().apply {
            put("ktor.deployment.port", baseConfig.property("ktor.deployment.port").getString())
            put("github.mode", baseConfig.property("github.mode").getString())
            put("github.token", baseConfig.property("github.token").getString())
            put("github.url", baseConfig.property("github.url").getString())
            put("app.version", baseConfig.property("app.version").getString())
            put("debug.healthPollIntervalMs", baseConfig.property("debug.healthPollIntervalMs").getString())
            configOverrides.forEach { (key, value) -> put(key, value) }
        }
        this.config = config
    }
    application {
        moduleWithRepositoryClient(FakeRepositoryClient())
    }
    block()
}

class ApplicationTest {

    @Test
    fun testStaticResources() = testApplicationWithRepositoryFake {
        client.get("/resources/style.css").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun testError() = testApplicationWithRepositoryFake {
        client.get("/error").apply {
            assertEquals(HttpStatusCode.InternalServerError, status)
        }
    }

    @Test
    fun testGh() = testApplicationWithRepositoryFake {
        client.get("/gh").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun testRequestIdIsEchoedWhenProvided() = testApplicationWithRepositoryFake {
        client.get("/gh") {
            header(HttpHeaders.XRequestId, "request-123")
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("request-123", headers[HttpHeaders.XRequestId])
        }
    }

    @Test
    fun testRequestIdIsGeneratedWhenMissing() = testApplicationWithRepositoryFake {
        client.get("/gh").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertNotNull(headers[HttpHeaders.XRequestId])
            assertFalse(headers[HttpHeaders.XRequestId]!!.isBlank())
        }
    }

    @Test
    fun testHealthCheck() = testApplicationWithRepositoryFake {
        // This block is necessary because the default test client does not know how to
        // automatically parse JSON. We are creating a new client instance and installing
        // the ContentNegotiation plugin, which teaches the client how to deserialize the
        // JSON response from the server into the `ApplicationInfo` data class. Without this,
        // the call to `body<ApplicationInfo>()` would fail with a NoTransformationFoundException.
        val client = createClient {
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                json()
            }
        }

        client.get("/health").apply {
            assertEquals(HttpStatusCode.OK, status)
            println(bodyAsText())
            val body = body<ApplicationInfo>()

            assertNotNull(body.startupTime)
            assertEquals("test", body.version)
            assertEquals(1500, body.debugHealthPollIntervalMs)
            assertEquals(body.bootId, UUID.fromString(body.bootId).toString())
            assertEquals("degraded", body.overallStatus)
            assertEquals("ok", body.checks.getValue("application").status)
            assertEquals("degraded", body.checks.getValue("cvAssets").status)
            assertEquals("ok", body.checks.getValue("cache").status)
        }
    }

    @Test
    fun testHealthCheckReportsHealthyCvAssetsWhenConfigured() {
        val cvRoot = createHealthTestCvRoot()

        testApplicationWithRepositoryFake(
            configOverrides = mapOf(
                "cv" to cvRoot.absolutePathString(),
            ),
        ) {
            val client = createClient {
                install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                    json()
                }
            }

            client.get("/health").apply {
                assertEquals(HttpStatusCode.OK, status)
                val body = body<ApplicationInfo>()

                assertEquals("ok", body.overallStatus)
                assertEquals("ok", body.checks.getValue("application").status)
                assertEquals("ok", body.checks.getValue("cvAssets").status)
                assertTrue(body.checks.getValue("cvAssets").message.contains(cvRoot.absolutePathString()))
                assertEquals("ok", body.checks.getValue("cache").status)
            }
        }
    }

    @Test
    fun testHealthCheckUsesConfiguredVersionAndPollInterval() = testApplicationWithRepositoryFake(
        configOverrides = mapOf(
            "app.version" to "dev-local",
            "debug.healthPollIntervalMs" to "2750",
        ),
    ) {
        val client = createClient {
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                json()
            }
        }

        client.get("/health").apply {
            assertEquals(HttpStatusCode.OK, status)
            val body = body<ApplicationInfo>()

            assertEquals("dev-local", body.version)
            assertEquals(2750, body.debugHealthPollIntervalMs)
        }
    }
}

private fun createHealthTestCvRoot() = createTempDirectory("health-cv-root-").apply {
    createHealthTestPdf(this / "eng" / "cv_light.pdf", "english-light")
    createHealthTestPdf(this / "eng" / "cv_dark.pdf", "english-dark")
    createHealthTestPdf(this / "ger" / "cv_light.pdf", "german-light")
    createHealthTestPdf(this / "ger" / "cv_dark.pdf", "german-dark")
    toFile().deleteOnExit()
}

private fun createHealthTestPdf(path: java.nio.file.Path, label: String) {
    path.parent.createDirectories()
    path.writeBytes("%PDF-1.4\n$label\n%%EOF".toByteArray())
    path.toFile().deleteOnExit()
}
