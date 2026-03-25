package de.noah_ruben.site

import de.noah_ruben.testApplicationWithRepositoryFake
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectories
import kotlin.io.path.createTempDirectory
import kotlin.io.path.div
import kotlin.io.path.writeBytes
import kotlin.test.Test
import kotlin.test.assertEquals

class CvPageRoutingTest {

    @Test
    fun cvPdfResponseSetsCachingHeaders() {
        val cvRoot = createRouteTestCvRoot()

        testApplicationWithRepositoryFake(
            configOverrides = mapOf(
                "cv" to cvRoot.absolutePathString(),
            ),
        ) {
            val response = client.get("/cv/pdf?lang=eng&mode=light")

            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals(
                "inline; filename=\"cv_light.pdf\"",
                response.headers[HttpHeaders.ContentDisposition],
            )
            assertEquals(
                "public, max-age=3600, stale-while-revalidate=86400",
                response.headers[HttpHeaders.CacheControl],
            )
        }
    }
}

private fun createRouteTestCvRoot() = createTempDirectory("route-cv-root-").apply {
    createRouteTestPdf(this / "eng" / "cv_light.pdf", "english-light")
    createRouteTestPdf(this / "eng" / "cv_dark.pdf", "english-dark")
    createRouteTestPdf(this / "ger" / "cv_light.pdf", "german-light")
    createRouteTestPdf(this / "ger" / "cv_dark.pdf", "german-dark")
    toFile().deleteOnExit()
}

private fun createRouteTestPdf(path: java.nio.file.Path, label: String) {
    path.parent.createDirectories()
    path.writeBytes("%PDF-1.4\n$label\n%%EOF".toByteArray())
    path.toFile().deleteOnExit()
}
