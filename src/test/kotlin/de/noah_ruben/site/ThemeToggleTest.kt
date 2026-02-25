package de.noah_ruben.site

import de.noah_ruben.testApplicationWithRepositoryFake
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ThemeToggleTest {

    @Test
    fun themeToggleMarkupIsRenderedOnPrimaryPages() = testApplicationWithRepositoryFake {
        val routes = listOf("/", "/projects")

        routes.forEach { route ->
            val response = client.get(route)
            assertEquals(HttpStatusCode.OK, response.status)

            val body = response.bodyAsText()
            assertTrue(body.contains("id=\"theme-toggle\""))
            assertTrue(body.contains("localStorage.theme"))
            assertTrue(body.contains("classList.remove(\"latte\", \"mocha\")"))
            assertTrue(body.contains("classList.add(theme)"))
            assertTrue(body.contains("<use href=\"/resources/icons/theme-icons.svg#moon\""))
            assertTrue(body.contains("<use href=\"/resources/icons/theme-icons.svg#sun\""))
            assertTrue(body.contains("aria-pressed=\"false\""))
            assertTrue(body.contains("aria-label=\"Toggle dark mode\""))
            assertTrue(body.contains("data-[theme=dark]:text-ctp-blue"))
            assertTrue(body.contains("data-[theme=light]:text-ctp-yellow"))
            assertTrue(body.contains("data-[theme=light]:bg-ctp-surface1"))
            assertTrue(body.contains("querySelectorAll(\"span\")"))
        }
    }

    @Test
    fun themeToggleCssUsesClassStrategy() = testApplicationWithRepositoryFake {
        val css = client.get("/resources/style.css").bodyAsText()

        assertTrue(css.contains(".mocha"))
        assertTrue(css.contains(".latte"))
    }
}
