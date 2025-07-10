package de.noah_ruben.site

import de.noah_ruben.testApplicationWithRepositoryFake
import io.ktor.client.request.get
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
}
