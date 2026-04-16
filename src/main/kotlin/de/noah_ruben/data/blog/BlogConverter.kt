package de.noah_ruben.data.blog

import org.commonmark.ext.front.matter.YamlFrontMatterExtension
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import java.nio.file.Path
import java.time.Instant
import java.time.OffsetDateTime
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.readText

data class ConvertedBlogPost(
    val slug: String,
    val title: String,
    val summary: String,
    val publishedDate: Instant,
    val tags: List<String>,
    val excerpt: String,
    val renderedHtml: String,
)

class BlogConverter {
    private val extensions = listOf(YamlFrontMatterExtension.create())
    private val parser = Parser.builder().extensions(extensions).build()
    private val renderer = HtmlRenderer.builder().extensions(extensions).build()

    fun convert(sourcePath: Path): ConvertedBlogPost {
        val markdown = sourcePath.readText()
        val document = parser.parse(markdown)
        val frontMatter = YamlFrontMatterVisitor().also(document::accept).data
        val renderedHtml = renderer.render(document).trim()
        val excerpt = renderedHtml.firstParagraphText()

        return ConvertedBlogPost(
            slug = sourcePath.nameWithoutExtension,
            title = frontMatter.requiredValue("title"),
            summary = frontMatter.optionalValue("summary") ?: excerpt,
            publishedDate = frontMatter.requiredValue("date").toInstant(),
            tags = frontMatter["tags"].orEmpty().map(String::trim).filter(String::isNotEmpty),
            excerpt = excerpt,
            renderedHtml = renderedHtml,
        )
    }
}

private fun Map<String, List<String>>.requiredValue(key: String): String =
    this[key]?.firstOrNull()?.takeIf(String::isNotBlank)
        ?: throw IllegalArgumentException("Missing required front matter key '$key'.")

private fun Map<String, List<String>>.optionalValue(key: String): String? =
    this[key]?.firstOrNull()?.trim()?.takeIf(String::isNotEmpty)

private fun String.toInstant(): Instant = try {
    Instant.parse(this)
} catch (_: Exception) {
    OffsetDateTime.parse(this).toInstant()
}

private fun String.firstParagraphText(): String {
    val paragraph = Regex("<p>(.*?)</p>", setOf(RegexOption.DOT_MATCHES_ALL))
        .find(this)
        ?.groupValues
        ?.getOrNull(1)
        .orEmpty()

    return paragraph
        .replace(Regex("<.*?>"), "")
        .replace(Regex("\\s+"), " ")
        .trim()
}
