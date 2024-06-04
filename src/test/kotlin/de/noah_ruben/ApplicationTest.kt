package de.noah_ruben

import de.noah_ruben.config.exceptionHandling
import de.noah_ruben.data.Cache
import de.noah_ruben.data.WiremockClient
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.testApplication
import org.junit.Assert.assertEquals
import kotlin.test.Test

class ApplicationTest {

    @Test
    fun testStaticResources() = testApplication {
        Cache.githubClient = WiremockClient()
        application {
            staticRouting()
        }
        client.get("/resources/style.css").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun testError() = testApplication {
        environment {
            config = ApplicationConfig("application-test.yaml")
        }
        Cache.githubClient = WiremockClient()
        application {
            staticRouting()
            exceptionHandling()
        }

        client.get("/error").apply {
            assertEquals(HttpStatusCode.InternalServerError, status)
        }
    }

    @Test
    fun testGh() = testApplication {
        environment {
            config = ApplicationConfig("application-test.yaml")
        }
        Cache.githubClient = WiremockClient()
        application {
            staticRouting()
            exceptionHandling()
        }
        client.get("/gh").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }
}
