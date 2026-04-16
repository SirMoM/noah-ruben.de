package de.noah_ruben.site.blog

import de.noah_ruben.data.blog.BlogPostRecord
import de.noah_ruben.misc.CssClasses.CONTENT_CONTAINER
import de.noah_ruben.misc.CssClasses.LINK
import de.noah_ruben.misc.CssClasses.PAGE_BASE
import de.noah_ruben.misc.CssClasses.PAGE_TITLE
import de.noah_ruben.misc.CssClasses.ProjectPage.EMPTY_STATE
import de.noah_ruben.misc.CssClasses.ProjectPage.EMPTY_STATE_MESSAGE
import de.noah_ruben.misc.CssClasses.ProjectPage.EMPTY_STATE_TITLE
import de.noah_ruben.site.commandLineEmulation
import de.noah_ruben.site.commandPrompt
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
import kotlinx.html.code
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.head
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.li
import kotlinx.html.p
import kotlinx.html.span
import kotlinx.html.ul
import kotlinx.html.unsafe

internal const val BLOG_COMMAND = "noahruben blog"
internal const val BLOG_QUERY_PARAMETER = "query"

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
    div(classes = CONTENT_CONTAINER) {
        commandPrompt(command = BLOG_COMMAND, containerRole = "blog-command")
        h1(classes = PAGE_TITLE) { +"Blog" }
        blogSearchForm(query)

        if (posts.isEmpty()) {
            div(classes = EMPTY_STATE) {
                h2(classes = EMPTY_STATE_TITLE) { +"Nothing found" }
                p(classes = EMPTY_STATE_MESSAGE) { +"No blog posts matched the current query." }
            }
        } else {
            ul {
                posts.forEach { post ->
                    li {
                        blogOverviewCard(post)
                    }
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
    div(classes = CONTENT_CONTAINER) {
        commandPrompt(command = "$BLOG_COMMAND ${post.slug}", containerRole = "blog-post-command")
        h1(classes = PAGE_TITLE) { +post.title }
        p { +post.summary }
        p { +"Published: ${post.publishedDate}" }
        article {
            unsafe {
                +articleHtml
            }
        }
        commandLineEmulation()
    }
}

private fun FlowContent.blogSearchForm(query: String) {
    form(action = "/blog", method = FormMethod.get) {
        input(type = InputType.text, name = BLOG_QUERY_PARAMETER) {
            value = query
            placeholder = "search title, summary, tags"
        }
        button(type = ButtonType.submit) {
            +"search"
        }
    }
}

private fun FlowContent.blogOverviewCard(post: BlogPostRecord) {
    article {
        h2 {
            a(href = "/blog/${post.slug}", classes = LINK) {
                +post.title
            }
        }
        p { +post.summary }
        p { +post.excerpt }
        p {
            +"Published: ${post.publishedDate}"
        }
        div {
            post.tags.forEachIndexed { index, tag ->
                if (index > 0) {
                    span { +" " }
                }
                code { +tag }
            }
        }
    }
}
