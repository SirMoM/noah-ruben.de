package de.noah_ruben.data.blog

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object BlogPostTable : IntIdTable("blog_posts") {
    val slug = varchar("slug", 255).uniqueIndex()
    val sourcePath = text("source_path")
    val sourceLastModified = long("source_last_modified")
    val title = text("title")
    val summary = text("summary")
    val publishedDate = varchar("published_date", 64)
    val tagsJson = text("tags_json")
    val excerpt = text("excerpt")
    val htmlPath = text("html_path")
    val htmlLastGenerated = long("html_last_generated")
    val isDeleted = bool("is_deleted").default(false)
}
