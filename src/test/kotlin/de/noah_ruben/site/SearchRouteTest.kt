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
import kotlin.test.assertTrue

class SearchRouteTest {

    @Test
    fun searchRouteReturnsMatchingProjectsForValidQuery() = testApplicationWithRepositoryFake {
        val response = client.post("/search") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody("query=dummy")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("dummy-repo"))
    }

    @Test
    fun searchRouteReturnsEmptyStateForUnmatchedQuery() = testApplicationWithRepositoryFake {
        val response = client.post("/search") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody("query=__definitely_no_matches__")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Nothing Found"))
    }

    @Test
    fun searchRouteReportsMissingRequiredQueryParameter() = testApplicationWithRepositoryFake {
        val response = client.post("/search") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody("topic=dummy")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Missing required search parameter (query)"))
    }

    @Test
    fun searchRouteReportsInvalidOrderByValue() = testApplicationWithRepositoryFake {
        val response = client.post("/search") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody("query=dummy&orderBy=Nope")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Invalid value for a search parameter"))
    }
}
