package de.noah_ruben.site.blog

import de.noah_ruben.data.blog.BlogPostRecord
import de.noah_ruben.misc.CssClasses.CONTENT_CONTAINER
import de.noah_ruben.misc.CssClasses.PAGE_BASE
import de.noah_ruben.misc.CssClasses.ProjectPage.EMPTY_STATE
import de.noah_ruben.misc.CssClasses.ProjectPage.EMPTY_STATE_MESSAGE
import de.noah_ruben.misc.CssClasses.ProjectPage.EMPTY_STATE_TITLE
import de.noah_ruben.site.commandLineEmulation
import de.noah_ruben.site.commandPrompt
import de.noah_ruben.site.defaultBlogCodeHeader
import de.noah_ruben.site.defaultBody
import de.noah_ruben.site.defaultHeader
import de.noah_ruben.site.themeToggleButton
import kotlinx.html.BODY
import kotlinx.html.ButtonType
import kotlinx.html.FlowContent
import kotlinx.html.FormMethod
import kotlinx.html.HTML
import kotlinx.html.InputType
import kotlinx.html.a
import kotlinx.html.article
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.head
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.p
import kotlinx.html.span
import kotlinx.html.unsafe
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

internal const val BLOG_COMMAND = "noahruben blog"
internal const val BLOG_QUERY_PARAMETER = "query"

private const val BLOG_SHELL = "blog-shell"
private const val BLOG_HEADER = "blog-header"
private const val BLOG_SECTION_LABEL = "blog-section-label"
private const val BLOG_TITLE = "blog-title"
private const val BLOG_INTRO = "blog-intro"
private const val BLOG_SEARCH_FORM = "blog-search-form"
private const val BLOG_SEARCH_CONTROL = "blog-search-control"
private const val BLOG_SEARCH_LABEL = "blog-search-label"
private const val BLOG_SEARCH_INPUT = "blog-search-input"
private const val BLOG_SEARCH_BUTTON = "blog-search-button"
private const val BLOG_INDEX = "blog-index"
private const val BLOG_ENTRY = "blog-entry"
private const val BLOG_ENTRY_TITLE = "blog-entry-title"
private const val BLOG_META = "blog-meta"
private const val BLOG_SUMMARY = "blog-summary"
private const val BLOG_OPEN_LINK = "blog-open-link"
private const val BLOG_POST_LAYOUT = "blog-post-layout"
private const val BLOG_POST_RETURN = "blog-post-return"
private const val BLOG_POST_HEADER = "blog-post-header"
private const val BLOG_POST_TITLE = "blog-post-title"
private const val BLOG_POST_SUMMARY = "blog-post-summary"
private const val BLOG_PROSE = "blog-prose"
private const val BLOG_CONTAINER = "$CONTENT_CONTAINER $BLOG_SHELL"

private val blogDateFormatter: DateTimeFormatter = DateTimeFormatter
    .ofPattern("yyyy-MM-dd")
    .withZone(ZoneId.of("Europe/Berlin"))

fun HTML.blogOverviewPage(
    posts: List<BlogPostRecord>,
    query: String,
) {
    head {
        defaultHeader()
    }
    defaultBody {
        id = "body"
        classes = setOf(PAGE_BASE)
        themeToggleButton()
        blogOverviewBody(posts, query)
    }
}

fun HTML.blogPostPage(
    post: BlogPostRecord,
    articleHtml: String,
) {
    head {
        defaultHeader()
        defaultBlogCodeHeader()
    }
    defaultBody {
        id = "body"
        classes = setOf(PAGE_BASE)
        themeToggleButton()
        blogPostBody(post, articleHtml)
    }
}

private fun BODY.blogOverviewBody(
    posts: List<BlogPostRecord>,
    query: String,
) {
    div(classes = BLOG_CONTAINER) {
        commandPrompt(command = BLOG_COMMAND, containerRole = "blog-command")
        div(classes = BLOG_HEADER) {
            p(classes = BLOG_SECTION_LABEL) { +"noahruben /blog" }
            h1(classes = BLOG_TITLE) { +"Blog" }
            p(classes = BLOG_INTRO) { +"Notes, essays, and experiments compiled from Markdown and served inside the site shell." }
        }
        blogSearchForm(query)

        if (posts.isEmpty()) {
            div(classes = EMPTY_STATE) {
                h2(classes = EMPTY_STATE_TITLE) { +"Nothing found" }
                p(classes = EMPTY_STATE_MESSAGE) { +"No blog posts matched the current query." }
            }
        } else {
            div(classes = BLOG_INDEX) {
                posts.forEach { post ->
                    blogOverviewCard(post)
                }
            }
        }

        commandLineEmulation()
    }
}

private fun BODY.blogPostBody(
    post: BlogPostRecord,
    articleHtml: String,
) {
    div(classes = BLOG_CONTAINER) {
        commandPrompt(command = "$BLOG_COMMAND ${post.slug}", containerRole = "blog-post-command")
        div(classes = BLOG_POST_LAYOUT) {
            p(classes = BLOG_POST_RETURN) {
                a(href = "/blog") { +"cd /blog" }
            }
            div(classes = BLOG_POST_HEADER) {
                p(classes = BLOG_SECTION_LABEL) { +"entry" }
                h1(classes = BLOG_POST_TITLE) { +post.title }
                blogMeta(post)
                p(classes = BLOG_POST_SUMMARY) { +post.summary }
            }
        }
        article(classes = BLOG_PROSE) {
            unsafe {
                +articleHtml
            }
        }
        commandLineEmulation()
    }
}

private fun FlowContent.blogSearchForm(query: String) {
    form(action = "/blog", method = FormMethod.get, classes = BLOG_SEARCH_FORM) {
        div(classes = BLOG_SEARCH_CONTROL) {
            span(classes = BLOG_SEARCH_LABEL) { +"query" }
            input(type = InputType.text, name = BLOG_QUERY_PARAMETER, classes = BLOG_SEARCH_INPUT) {
                value = query
                placeholder = "title, summary, tags"
                attributes["aria-label"] = "Search blog posts"
            }
        }
        button(type = ButtonType.submit, classes = BLOG_SEARCH_BUTTON) {
            +"run"
        }
    }
}

private fun FlowContent.blogOverviewCard(post: BlogPostRecord) {
    article(classes = BLOG_ENTRY) {
        blogMeta(post)
        h2(classes = BLOG_ENTRY_TITLE) {
            a(href = "/blog/${post.slug}") {
                +post.title
            }
        }
        p(classes = BLOG_SUMMARY) { +post.summary }
        a(href = "/blog/${post.slug}", classes = BLOG_OPEN_LINK) {
            +"open post ->"
        }
    }
}

private fun FlowContent.blogMeta(post: BlogPostRecord) {
    p(classes = BLOG_META) {
        span { +post.publishedDate.toBlogDate() }
        if (post.tags.isNotEmpty()) {
            span { +"·" }
            span { +post.tags.joinToString(" ") { "#$it" } }
        }
    }
}

private fun Instant.toBlogDate(): String = blogDateFormatter.format(this)
