package de.noah_ruben.site

import kotlinx.html.DIV
import kotlinx.html.HEAD
import kotlinx.html.a
import kotlinx.html.link
import kotlinx.html.meta
import kotlinx.html.script
import kotlinx.html.title

fun DIV.selfLink(_url: String, text: String) {
    a(href = _url) {
        +" $text"
    }
}

fun DIV.githubLink() {
    a(href = "https://github.com/SirMoM") {
        +" GITHUB"
    }
}

fun HEAD.defaultHeader() {
    link(rel = "stylesheet", href = "/resources/style.css")
    meta(charset = "UTF-8")
    meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
    title("Noah Ruben")
    script {
        src = "https://unpkg.com/htmx.org@1.9.11"
        integrity = "sha384-0gxUXCCR8yv9FM2b+U3FDbsKthCI66oH5IA9fHppQq9DDMHuMauqq1ZHBpJxQ0J0"
        attributes["crossorigin"] = "anonymous"
    }
    script(src = "https://cdn.tailwindcss.com") {}
    link(rel = "stylesheet", href = "https://fonts.cdnfonts.com/css/cascadia-code")
}
