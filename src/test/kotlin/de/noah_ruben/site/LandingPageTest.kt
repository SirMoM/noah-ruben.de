package de.noah_ruben.site

import de.noah_ruben.testApplicationWithRepositoryFake
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals

class LandingPageTest {

    @Test
    fun landingPageRespondsOk() = testApplicationWithRepositoryFake {
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }
}
