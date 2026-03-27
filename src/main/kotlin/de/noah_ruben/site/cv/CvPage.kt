@file:Suppress("ktlint:standard:no-wildcard-imports")

package de.noah_ruben.site.cv

import de.noah_ruben.misc.CssClasses.CONTENT_CONTAINER
import de.noah_ruben.misc.CssClasses.Form.LOADING_SPINNER
import de.noah_ruben.misc.CssClasses.PAGE_BASE
import de.noah_ruben.misc.CssClasses.Shared.ERROR_MESSAGE_BOX
import de.noah_ruben.misc.CssClasses.Shared.HTMX_INDICATOR_INLINE
import de.noah_ruben.site.commandLineEmulation
import de.noah_ruben.site.commandPrompt
import de.noah_ruben.site.defaultBody
import de.noah_ruben.site.defaultHeader
import de.noah_ruben.site.themeToggleButton
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.html.respondHtml
import io.ktor.server.response.header
import io.ktor.server.response.respondOutputStream
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.html.*

private const val CV_TOGGLE_ROW = "fixed right-4 top-20 z-40 flex flex-wrap justify-end gap-3"
private const val CV_TOGGLE_LINK_BASE = "inline-flex rounded-full border px-4 py-2 text-sm font-semibold transition-colors duration-200"
private const val CV_TOGGLE_LINK_ACTIVE = "border-ctp-blue bg-ctp-blue text-ctp-base"
private const val CV_TOGGLE_LINK_INACTIVE = "border-ctp-overlay1 bg-ctp-base text-ctp-text hover:bg-ctp-surface0"
private const val CV_VIEWER_SECTION = "mx-auto mt-6 flex max-w-5xl flex-col"
private const val CV_VIEWER_PAGES = "flex w-full flex-col items-center gap-6"
private const val CV_VIEWER_ERROR = "w-full rounded-[1.5rem] border border-ctp-red bg-ctp-base px-6 py-5 text-center font-semibold text-ctp-red"
private const val CV_VIEWER_LOADING = "flex justify-center py-12"
private const val CV_VIEWER_PAGE_SLOT = "cv-pdf-page-slot"
private const val CV_VIEWER_TEXT_LAYER = "cv-pdf-text-layer"
private const val CV_VIEWER_CANVAS_COUNT = 3
private const val CV_PDF_CACHE_CONTROL = "public, max-age=3600, stale-while-revalidate=86400"
private const val CV_TERMINAL_SECTION = "mt-10"

fun Application.cvPageRouting() {
    routing {
        get("/cv") {
            val language = try {
                CvLanguage.fromQueryParameter(call.request.queryParameters["lang"])
            } catch (error: IllegalArgumentException) {
                call.respondHtml(HttpStatusCode.BadRequest) {
                    lang = "en"
                    cvPageHtml(
                        CvPageState(
                            selectedLanguage = CvLanguage.English,
                            errorMessage = error.message,
                        ),
                    )
                }
                return@get
            }

            call.respondHtml(HttpStatusCode.OK) {
                lang = "en"
                cvPageHtml(
                    buildCvPageState(
                        config = environment.config,
                        language = language,
                    ),
                )
            }
        }

        get("/cv/pdf") {
            val language = try {
                CvLanguage.fromQueryParameter(call.request.queryParameters["lang"])
            } catch (error: IllegalArgumentException) {
                call.respondText(
                    text = error.message.orEmpty(),
                    contentType = ContentType.Text.Plain,
                    status = HttpStatusCode.BadRequest,
                )
                return@get
            }

            val mode = CvMode.fromQueryParameter(call.request.queryParameters["mode"])
            val asset = try {
                CvPdfResolver(environment.config).resolve(language, mode)
            } catch (error: CvConfigurationException) {
                call.respondText(
                    text = error.message.orEmpty(),
                    contentType = ContentType.Text.Plain,
                    status = HttpStatusCode.ServiceUnavailable,
                )
                return@get
            } catch (error: CvFileUnavailableException) {
                call.respondText(
                    text = error.message.orEmpty(),
                    contentType = ContentType.Text.Plain,
                    status = HttpStatusCode.NotFound,
                )
                return@get
            }

            call.response.header(
                HttpHeaders.CacheControl,
                CV_PDF_CACHE_CONTROL,
            )
            call.response.header(
                HttpHeaders.ContentDisposition,
                "inline; filename=\"${asset.file.name}\"",
            )
            call.respondOutputStream(
                contentType = ContentType.Application.Pdf,
                status = HttpStatusCode.OK,
            ) {
                asset.file.inputStream().use { input ->
                    input.copyTo(this)
                }
            }
        }
    }
}

fun HTML.cvPageHtml(pageState: CvPageState) {
    head {
        defaultHeader()
        link(rel = "stylesheet", href = "/resources/cv-pdf-viewer.css")
    }
    defaultBody {
        id = "body"
        classes = setOf(PAGE_BASE)
        themeToggleButton()
        cvPageBody(pageState)
        if (pageState.errorMessage == null) {
            script {
                attributes["type"] = "module"
                src = "/resources/cv-pdf-viewer.mjs"
            }
        }
    }
}

fun BODY.cvPageBody(pageState: CvPageState) {
    div(classes = CONTENT_CONTAINER) {
        commandPrompt(
            command = "noahruben cv ${pageState.selectedLanguage.token}",
            containerRole = "cv-command",
            textRole = "cv-command-text",
        )
        div(classes = CV_TOGGLE_ROW) {
            CvLanguage.entries.forEach { language ->
                cvLanguageLink(
                    language = language,
                    selectedLanguage = pageState.selectedLanguage,
                )
            }
        }

        if (pageState.errorMessage != null) {
            div(classes = ERROR_MESSAGE_BOX) {
                +pageState.errorMessage
            }
        } else {
            div(classes = CV_VIEWER_SECTION) {
                div {
                    id = "cv-pdf-viewer"
                    attributes["data-current-language"] = pageState.selectedLanguage.token
                    attributes["data-pdf-url-base"] = pageState.selectedLanguage.pdfUrlBase()
                    attributes["data-pdf-title"] = "${pageState.selectedLanguage.displayName} CV"
                    attributes["data-toggle-link-base"] = CV_TOGGLE_LINK_BASE
                    attributes["data-toggle-link-active"] = CV_TOGGLE_LINK_ACTIVE
                    attributes["data-toggle-link-inactive"] = CV_TOGGLE_LINK_INACTIVE

                    div(classes = CV_VIEWER_ERROR) {
                        attributes["data-role"] = "error"
                        attributes["hidden"] = ""
                        +"The PDF preview is unavailable right now."
                    }

                    div(classes = CV_VIEWER_LOADING) {
                        attributes["data-role"] = "loading"
                        attributes["hidden"] = ""
                        div(classes = "$LOADING_SPINNER htmx-request") {
                            +"Loading CV "
                            span(classes = HTMX_INDICATOR_INLINE) {
                                img(src = "/resources/bars.svg", alt = "Loading CV...")
                            }
                        }
                    }

                    div(classes = CV_VIEWER_PAGES) {
                        attributes["data-role"] = "pages-active"
                        attributes["hidden"] = ""
                        repeat(CV_VIEWER_CANVAS_COUNT) { pageIndex ->
                            div(classes = CV_VIEWER_PAGE_SLOT) {
                                attributes["data-role"] = "page-slot"
                                attributes["hidden"] = ""
                                canvas {
                                    attributes["data-page-number"] = (pageIndex + 1).toString()
                                }
                                div(classes = CV_VIEWER_TEXT_LAYER) {
                                    attributes["data-role"] = "text-layer"
                                }
                            }
                        }
                    }
                }
            }
        }

        div(classes = CV_TERMINAL_SECTION) {
            commandLineEmulation()
        }
    }
}

private fun FlowContent.cvLanguageLink(
    language: CvLanguage,
    selectedLanguage: CvLanguage,
) {
    val isSelected = language == selectedLanguage
    val selectedClass = if (isSelected) CV_TOGGLE_LINK_ACTIVE else CV_TOGGLE_LINK_INACTIVE

    a(
        href = language.pageUrl(),
        classes = "$CV_TOGGLE_LINK_BASE $selectedClass",
    ) {
        attributes["data-cv-language"] = language.token
        attributes["data-pdf-url-base"] = language.pdfUrlBase()
        attributes["data-pdf-title"] = "${language.displayName} CV"
        attributes["data-selected"] = isSelected.toString()
        +language.toggleLabel
    }
}
