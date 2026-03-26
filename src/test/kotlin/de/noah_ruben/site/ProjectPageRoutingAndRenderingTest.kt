package de.noah_ruben.site

import de.noah_ruben.testApplicationWithRepositoryFake
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals

class ProjectPageRoutingAndRenderingTest {

    @Test
    fun projectsPageRespondsOk() = testApplicationWithRepositoryFake {
        client.get("/projects").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }
}
