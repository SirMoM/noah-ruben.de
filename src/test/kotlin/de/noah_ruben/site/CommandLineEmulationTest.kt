package de.noah_ruben.site

import de.noah_ruben.testApplicationWithRepositoryFake
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectories
import kotlin.io.path.createTempDirectory
import kotlin.io.path.div
import kotlin.io.path.writeBytes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CommandLineEmulationTest {

    @Test
    fun helpCommandRetargetsToCliContainer() = testApplicationWithRepositoryFake {
        val response = client.post("/command") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody("command=noahruben+help")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("#cle", response.headers["HX-Retarget"])
    }

    @Test
    fun landingCommandPushesLandingUrlIntoHtmxHistory() = testApplicationWithRepositoryFake {
        val response = client.post("/command") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody("command=noahruben")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("/", response.headers["HX-Push-Url"])
    }

    @Test
    fun projectsCommandPushesProjectsUrlIntoHtmxHistory() = testApplicationWithRepositoryFake {
        val response = client.post("/command") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody("command=noahruben+projects")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("/projects", response.headers["HX-Push-Url"])
    }

    @Test
    fun cvCommandDefaultsToEnglishCvPage() {
        val cvRoot = createCommandTestCvRoot()

        testApplicationWithRepositoryFake(
            configOverrides = mapOf(
                "cv" to cvRoot.absolutePathString(),
            ),
        ) {
            val response = client.post("/command") {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody("command=noahruben+cv")
            }
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals("/cv?lang=eng", response.headers["HX-Push-Url"])
        }
    }

    @Test
    fun cvCommandSupportsExplicitLanguageSelectionAndValidation() {
        val cvRoot = createCommandTestCvRoot()

        testApplicationWithRepositoryFake(
            configOverrides = mapOf(
                "cv" to cvRoot.absolutePathString(),
            ),
        ) {
            val defaultResponse = client.post("/command") {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody("command=noahruben+cv")
            }
            assertEquals(HttpStatusCode.OK, defaultResponse.status)

            val germanResponse = client.post("/command") {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody("command=noahruben+cv+ger")
            }
            assertEquals(HttpStatusCode.OK, germanResponse.status)
            assertEquals("/cv?lang=ger", germanResponse.headers["HX-Push-Url"])

            val invalidResponse = client.post("/command") {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody("command=noahruben+cv+fra")
            }
            assertEquals(HttpStatusCode.OK, invalidResponse.status)
            assertEquals("#cle", invalidResponse.headers["HX-Retarget"])
            assertTrue(invalidResponse.bodyAsText().contains("Unsupported CV language 'fra'"))
        }
    }
}

private fun createCommandTestCvRoot() = createTempDirectory("command-cv-root-").apply {
    createCommandTestPdf(this / "eng" / "cv_light.pdf", "english-light")
    createCommandTestPdf(this / "eng" / "cv_dark.pdf", "english-dark")
    createCommandTestPdf(this / "ger" / "cv_light.pdf", "german-light")
    createCommandTestPdf(this / "ger" / "cv_dark.pdf", "german-dark")
    toFile().deleteOnExit()
}

private fun createCommandTestPdf(path: java.nio.file.Path, label: String) {
    path.parent.createDirectories()
    path.writeBytes("%PDF-1.4\n$label\n%%EOF".toByteArray())
    path.toFile().deleteOnExit()
}
