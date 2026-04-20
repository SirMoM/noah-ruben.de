package de.noah_ruben.config

import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertTrue

class ObservabilityTest {

    @Test
    fun healthEndpointIsFilteredFromRequestLogging() {
        val source = Files.readString(Path.of("src/main/kotlin/de/noah_ruben/config/Observability.kt"))

        assertTrue(source.contains("""call.request.path() != "/health""""))
    }
}
