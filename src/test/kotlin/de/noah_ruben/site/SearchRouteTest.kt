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
        assertTrue(response.bodyAsText().contains("0 results"))
    }

    @Test
    fun searchRouteParsesRepeatedTopicParametersIntoCommandPreview() = testApplicationWithRepositoryFake {
        val response = client.post("/search") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody("query=&topic=dummy&topic=example")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("""data-selected-topic="dummy""""))
        assertTrue(response.bodyAsText().contains("""data-selected-topic="example""""))
    }

    @Test
    fun searchRouteAppliesAddAndRemoveTopicActions() = testApplicationWithRepositoryFake {
        val addedResponse = client.post("/search") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody("query=&topic=dummy&addTopic=example")
        }

        assertEquals(HttpStatusCode.OK, addedResponse.status)
        assertTrue(addedResponse.bodyAsText().contains("""data-selected-topic="dummy""""))
        assertTrue(addedResponse.bodyAsText().contains("""data-selected-topic="example""""))

        val removedResponse = client.post("/search") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody("query=&topic=dummy&topic=example&removeTopic=dummy")
        }

        assertEquals(HttpStatusCode.OK, removedResponse.status)
        assertTrue(removedResponse.bodyAsText().contains("""data-selected-topic="example""""))
        assertTrue(!removedResponse.bodyAsText().contains("""data-selected-topic="dummy""""))
    }

    @Test
    fun searchRouteResetPayloadClearsTopicState() = testApplicationWithRepositoryFake {
        val response = client.post("/search") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody("query=&language=%3CLanguage%3E&orderBy=Relevance&dir=&withSearchBar=true")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("noahruben projects"))
        assertTrue(response.bodyAsText().contains("""id="projects-topic-control""""))
        assertTrue(response.bodyAsText().contains(">all<"))
        assertTrue(!response.bodyAsText().contains("""data-selected-topic=""""))
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
