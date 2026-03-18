package de.noah_ruben.site

import de.noah_ruben.testApplicationWithRepositoryFake
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LandingPageTest {

    @Test
    fun landingPage() = testApplicationWithRepositoryFake {
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun landingPageIncludesColorGridTileSizing() = testApplicationWithRepositoryFake {
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)

        val html = response.bodyAsText()
        assertTrue(html.contains("grid-cols-8"))
        assertTrue(html.contains("h-10"))
        assertTrue(html.contains("w-10"))
        assertTrue(html.contains("border-ctp-crust"))
    }

    @Test
    fun landingPageRendersUpdatedProfileLinksAndRole() = testApplicationWithRepositoryFake {
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)

        val html = response.bodyAsText()
        assertTrue(html.contains("Full-Stack Developer @"))
        assertTrue(html.contains("https://www.karriere-atlas.de/"))
        assertTrue(html.contains("https://github.com/SirMoM"))
        assertTrue(html.contains("https://www.linkedin.com/in/noah-ruben-3013991b7"))
        assertTrue(html.contains("mailto:"))
        assertFalse(html.contains("Twitter"))
        assertFalse(html.contains("TODO"))
        assertTrue(html.contains("href=\"/cv\""))
    }

    @Test
    fun landingPageRendersLinkedPortraitWithAttributionMetadata() = testApplicationWithRepositoryFake {
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)

        val html = response.bodyAsText()
        assertTrue(html.contains("href=\"https://commons.wikimedia.org/wiki/File:Van_Gogh_self-portrait.svg\""))
        assertTrue(html.contains("title=\"Vincent van Gogh, Public domain, via Wikimedia Commons\""))
        assertTrue(html.contains("src=\"https://upload.wikimedia.org/wikipedia/commons/thumb/d/d4/Van_Gogh_self-portrait.svg/120px-Van_Gogh_self-portrait.svg.png\""))
        assertTrue(html.contains("alt=\"Self-portrait of Vincent van Gogh, vector traced\""))
        assertTrue(html.contains("w-2/5"))
        assertTrue(html.contains("shrink-0"))
    }

    @Test
    fun landingPageRendersBioSkillsAndHelp() = testApplicationWithRepositoryFake {
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)

        val html = response.bodyAsText()
        assertTrue(html.contains("System summary"))
        assertTrue(html.contains("Languages"))
        assertTrue(html.contains("Kotlin"))
        assertTrue(html.contains("Angular"))
        assertTrue(html.contains("HTMX"))
        assertTrue(html.contains("href=\"/cv\""))
    }

    @Test
    fun landingPageRendersAccentColorSwitcher() = testApplicationWithRepositoryFake {
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)

        val html = response.bodyAsText()
        assertTrue(html.contains("data-accent"))
        assertTrue(html.contains("setAccentColor"))
        assertTrue(html.contains("localStorage.getItem(&#39;accent&#39;)") || html.contains("localStorage.getItem('accent')"))
    }
}
