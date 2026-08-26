package de.noah_ruben.data.blog

import java.time.Instant

data class BlogPostRecord(
    val slug: String,
    val sourcePath: String,
    val sourceLastModified: Long,
    val title: String,
    val summary: String,
    val publishedDate: Instant,
    val tags: List<String>,
    val excerpt: String,
    val htmlPath: String,
    val htmlLastGenerated: Long,
    val isDeleted: Boolean,
)
