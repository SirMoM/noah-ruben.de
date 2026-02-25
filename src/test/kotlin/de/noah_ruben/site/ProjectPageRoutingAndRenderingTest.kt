package de.noah_ruben.site

import de.noah_ruben.testApplicationWithRepositoryFake
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import org.junit.Assert
import kotlin.test.Test

class ProjectPageRoutingAndRenderingTest {

    @Test
    fun projectPageRoutingAndRenderingTest() = testApplicationWithRepositoryFake {
        client.get("/projects").apply {
            Assert.assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun projectsPageIncludesLightAndDarkClassVariants() = testApplicationWithRepositoryFake {
        val response = client.get("/projects")
        Assert.assertEquals(HttpStatusCode.OK, response.status)

        val body = response.bodyAsText()
        Assert.assertTrue(body.contains("bg-ctp-surface0"))
        Assert.assertTrue(body.contains("text-ctp-text"))
        Assert.assertTrue(body.contains("border-ctp-overlay1"))
        Assert.assertTrue(body.contains("text-ctp-blue"))
    }
}
