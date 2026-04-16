package de.noah_ruben.site

import kotlinx.html.DIV
import kotlinx.html.FlowContent
import kotlinx.html.br
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.p

data class WebsiteHelpLink(
    val href: String,
    val label: String,
)

fun landingHelpLinks(): List<WebsiteHelpLink> = listOf(
    WebsiteHelpLink("/blog", "blog"),
    WebsiteHelpLink("/projects", "projects"),
    WebsiteHelpLink("/cv", "cv"),
    WebsiteHelpLink("https://github.com/SirMoM", "GitHub"),
    WebsiteHelpLink("https://www.linkedin.com/in/noah-ruben-3013991b7", "linked-in"),
)

fun cliHelpLinks(): List<WebsiteHelpLink> = listOf(
    WebsiteHelpLink("/blog", "blog"),
    WebsiteHelpLink("/projects", "projects"),
    WebsiteHelpLink("https://github.com/SirMoM", "GitHub"),
    WebsiteHelpLink("https://www.linkedin.com/in/noah-ruben-3013991b7", "linked-in"),
)

fun FlowContent.websiteHelpContent(
    links: List<WebsiteHelpLink>,
    subpageClasses: String? = null,
) {
    p { +"Usage: noahruben <subpage>" }
    p { +"noahruben is the personal website of Noah Ruben" }
    p { +"It displays information about Noah Ruben: full-stack development, open source, and game dev interests." }
    h1 { +"SUB-PAGES" }

    if (subpageClasses == null) {
        div {
            renderHelpLinks(links)
        }
        return
    }

    div(classes = subpageClasses) {
        renderHelpLinks(links)
    }
}

private fun DIV.renderHelpLinks(links: List<WebsiteHelpLink>) {
    links.forEachIndexed { index, link ->
        selfLink(link.href, link.label)
        if (index < links.lastIndex) {
            br()
        }
    }
}
