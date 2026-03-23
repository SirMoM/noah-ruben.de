package de.noah_ruben

import de.noah_ruben.config.ApplicationInfo
import de.noah_ruben.config.appInfo
import de.noah_ruben.data.FakeRepositoryClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

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
            configOverrides.forEach { (key, value) -> put(key, value) }
        }
        this.config = config
    }
    application {
        module(FakeRepositoryClient())
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

            assertEquals(appInfo.version, body.version)
        }
    }
}
