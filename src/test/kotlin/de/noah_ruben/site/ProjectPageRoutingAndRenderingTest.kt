package de.noah_ruben.site

import de.noah_ruben.data.Cache
import de.noah_ruben.data.GithubClientFake
import de.noah_ruben.site.projects.projectsPageRouting
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import org.junit.Assert
import kotlin.test.Test

class ProjectPageRoutingAndRenderingTest {

    @Test
    fun projectPageRoutingAndRenderingTest() = testApplication {
        application {
            Cache.githubClient = GithubClientFake()
            projectsPageRouting()
        }
        client.get("/projects").apply {
            Assert.assertEquals(HttpStatusCode.OK, status)
        }
    }
}
