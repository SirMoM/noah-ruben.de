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
            assertTrue(body.contains("classList.toggle(\"dark\""))
            assertTrue(body.contains("<use href=\"/resources/icons/theme-icons.svg#moon\""))
            assertTrue(body.contains("<use href=\"/resources/icons/theme-icons.svg#sun\""))
            assertTrue(body.contains("aria-pressed=\"false\""))
            assertTrue(body.contains("aria-label=\"Toggle dark mode\""))
            assertTrue(body.contains("data-[theme=dark]:text-blue"))
            assertTrue(body.contains("data-[theme=light]:text-yellow"))
            assertTrue(body.contains("querySelectorAll(\"span\")"))
        }
    }

    @Test
    fun themeToggleCssUsesClassStrategy() = testApplicationWithRepositoryFake {
        val css = client.get("/resources/style.css").bodyAsText()

        assertFalse(css.contains("prefers-color-scheme: dark"))
        assertTrue(css.contains("html.dark"))
    }
}
