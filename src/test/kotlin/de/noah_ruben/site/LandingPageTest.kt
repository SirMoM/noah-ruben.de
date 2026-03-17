package de.noah_ruben.site

import de.noah_ruben.testApplicationWithRepositoryFake
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import org.junit.Assert
import kotlin.test.Test

class LandingPageTest {

    @Test
    fun landingPage() = testApplicationWithRepositoryFake {
        client.get("/").apply {
            Assert.assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun landingPageIncludesColorGridTileSizing() = testApplicationWithRepositoryFake {
        val response = client.get("/")
        Assert.assertEquals(HttpStatusCode.OK, response.status)

        val html = response.bodyAsText()
        Assert.assertTrue(html.contains("grid-cols-8"))
        Assert.assertTrue(html.contains("h-10"))
        Assert.assertTrue(html.contains("w-10"))
        Assert.assertTrue(html.contains("border-ctp-crust"))
    }

    @Test
    fun landingPageRendersUpdatedProfileLinksAndRole() = testApplicationWithRepositoryFake {
        val response = client.get("/")
        Assert.assertEquals(HttpStatusCode.OK, response.status)

        val html = response.bodyAsText()
        Assert.assertTrue(html.contains("Full-Stack Developer @"))
        Assert.assertTrue(html.contains("https://www.karriere-atlas.de/"))
        Assert.assertTrue(html.contains("https://github.com/SirMoM"))
        Assert.assertTrue(html.contains("https://www.linkedin.com/in/noah-ruben-3013991b7"))
        Assert.assertTrue(html.contains("mailto:"))
        Assert.assertFalse(html.contains("Twitter"))
        Assert.assertFalse(html.contains("TODO"))
        Assert.assertTrue(html.contains("href=\"/cv\""))
    }

    @Test
    fun landingPageRendersLinkedPortraitWithAttributionMetadata() = testApplicationWithRepositoryFake {
        val response = client.get("/")
        Assert.assertEquals(HttpStatusCode.OK, response.status)

        val html = response.bodyAsText()
        Assert.assertTrue(html.contains("href=\"https://commons.wikimedia.org/wiki/File:Van_Gogh_self-portrait.svg\""))
        Assert.assertTrue(html.contains("title=\"Vincent van Gogh, Public domain, via Wikimedia Commons\""))
        Assert.assertTrue(html.contains("src=\"https://upload.wikimedia.org/wikipedia/commons/thumb/d/d4/Van_Gogh_self-portrait.svg/120px-Van_Gogh_self-portrait.svg.png\""))
        Assert.assertTrue(html.contains("alt=\"Self-portrait of Vincent van Gogh, vector traced\""))
        Assert.assertTrue(html.contains("w-2/5"))
        Assert.assertTrue(html.contains("shrink-0"))
    }

    @Test
    fun landingPageRendersBioSkillsAndHelp() = testApplicationWithRepositoryFake {
        val response = client.get("/")
        Assert.assertEquals(HttpStatusCode.OK, response.status)

        val html = response.bodyAsText()
        Assert.assertTrue(html.contains("System summary"))
        Assert.assertTrue(html.contains("Languages"))
        Assert.assertTrue(html.contains("Kotlin"))
        Assert.assertTrue(html.contains("Angular"))
        Assert.assertTrue(html.contains("HTMX"))
        Assert.assertTrue(html.contains("href=\"/cv\""))
    }

    @Test
    fun landingPageRendersAccentColorSwitcher() = testApplicationWithRepositoryFake {
        val response = client.get("/")
        Assert.assertEquals(HttpStatusCode.OK, response.status)

        val html = response.bodyAsText()
        Assert.assertTrue(html.contains("data-accent"))
        Assert.assertTrue(html.contains("setAccentColor"))
        Assert.assertTrue(html.contains("localStorage.getItem(&#39;accent&#39;)") || html.contains("localStorage.getItem('accent')"))
    }
}
