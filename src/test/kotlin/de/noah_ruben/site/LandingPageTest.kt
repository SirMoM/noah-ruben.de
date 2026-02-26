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
        Assert.assertTrue(html.contains("Full-Stack Developer @ ATLAS"))
        Assert.assertTrue(html.contains("https://github.com/SirMoM"))
        Assert.assertTrue(html.contains("https://www.linkedin.com/in/noah-ruben-3013991b7"))
        Assert.assertTrue(html.contains("mailto:"))
        Assert.assertFalse(html.contains("Twitter"))
        Assert.assertFalse(html.contains("TODO"))
    }

    @Test
    fun landingPageRendersBioSkillsAndHelpWithoutCv() = testApplicationWithRepositoryFake {
        val response = client.get("/")
        Assert.assertEquals(HttpStatusCode.OK, response.status)

        val html = response.bodyAsText()
        Assert.assertTrue(html.contains("System summary"))
        Assert.assertTrue(html.contains("Languages"))
        Assert.assertTrue(html.contains("Kotlin"))
        Assert.assertTrue(html.contains("Angular"))
        Assert.assertTrue(html.contains("HTMX"))
        Assert.assertFalse(html.contains("href=\"/cv\""))
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
