package de.noah_ruben.site

import de.noah_ruben.data.Cache
import de.noah_ruben.data.GithubClientFake
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import org.junit.Assert
import kotlin.test.Test

class LandingPageTest {

    @Test
    fun landingPage() = testApplication {
        Cache.githubClient = GithubClientFake()
        application {
            landingPage()
        }
        client.get("/").apply {
            Assert.assertEquals(HttpStatusCode.OK, status)
        }
    }
}
