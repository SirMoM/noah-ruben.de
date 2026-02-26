package de.noah_ruben.site

import de.noah_ruben.testApplicationWithRepositoryFake
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import org.junit.Assert
import kotlin.test.Test

class CommandLineEmulationTest {

    @Test
    fun helpCommandRendersUsageWithoutTodoAndWithoutCvLink() = testApplicationWithRepositoryFake {
        val response = client.post("/command") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody("command=noahruben+help")
        }
        Assert.assertEquals(HttpStatusCode.OK, response.status)

        val html = response.bodyAsText()
        Assert.assertTrue(html.contains("Usage: noahruben"))
        Assert.assertFalse(html.contains("TODO"))
        Assert.assertFalse(html.contains("href=\"/cv\""))
    }

    @Test
    fun cvCommandReturnsNon500WithFriendlyMessage() = testApplicationWithRepositoryFake {
        val response = client.post("/command") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody("command=noahruben+cv")
        }
        Assert.assertNotEquals(HttpStatusCode.InternalServerError, response.status)

        val html = response.bodyAsText()
        Assert.assertTrue(html.contains("not available"))
    }
}
