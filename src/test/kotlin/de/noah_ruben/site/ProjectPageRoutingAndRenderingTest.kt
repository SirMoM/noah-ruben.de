package de.noah_ruben.site

import de.noah_ruben.testApplicationWithRepositoryFake
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProjectPageRoutingAndRenderingTest {

    @Test
    fun projectPageRoutingAndRenderingTest() = testApplicationWithRepositoryFake {
        client.get("/projects").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun projectsPageIncludesLightAndDarkClassVariants() = testApplicationWithRepositoryFake {
        val response = client.get("/projects")
        assertEquals(HttpStatusCode.OK, response.status)

        val body = response.bodyAsText()
        assertTrue(body.contains("bg-ctp-surface0"))
        assertTrue(body.contains("text-ctp-text"))
        assertTrue(body.contains("border-ctp-overlay1"))
        assertTrue(body.contains("text-ctp-blue"))
    }
}
