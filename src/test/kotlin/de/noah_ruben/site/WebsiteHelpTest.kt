package de.noah_ruben.site

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WebsiteHelpTest {

    @Test
    fun landingHelpLinksIncludeCv() {
        assertTrue(landingHelpLinks().any { it.href == "/cv" && it.label == "cv" })
    }

    @Test
    fun cliHelpLinksDoNotIncludeCv() {
        assertFalse(cliHelpLinks().any { it.href == "/cv" })
    }

    @Test
    fun helpLinksUseGitHubBranding() {
        assertTrue(landingHelpLinks().any { it.href == "https://github.com/SirMoM" && it.label == "GitHub" })
        assertTrue(cliHelpLinks().any { it.href == "https://github.com/SirMoM" && it.label == "GitHub" })
    }
}
