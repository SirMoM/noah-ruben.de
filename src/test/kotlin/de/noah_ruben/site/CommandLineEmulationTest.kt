package de.noah_ruben.site

import de.noah_ruben.testApplicationWithRepositoryFake
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class CommandLineEmulationTest {

    @Test
    fun helpCommandRendersUsageWithoutTodoAndWithoutCvLink() = testApplicationWithRepositoryFake {
        val response = client.post("/command") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody("command=noahruben+help")
        }
        assertEquals(HttpStatusCode.OK, response.status)

        val html = response.bodyAsText()
        assertTrue(html.contains("Usage: noahruben"))
        assertFalse(html.contains("TODO"))
        assertFalse(html.contains("href=\"/cv\""))
        assertTrue(html.contains("autocomplete=\"off\""))
    }

    @Test
    fun cvCommandReturnsNon500WithFriendlyMessage() = testApplicationWithRepositoryFake {
        val response = client.post("/command") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody("command=noahruben+cv")
        }
        assertNotEquals(HttpStatusCode.InternalServerError, response.status)

        val html = response.bodyAsText()
        assertTrue(html.contains("not available"))
    }
}
