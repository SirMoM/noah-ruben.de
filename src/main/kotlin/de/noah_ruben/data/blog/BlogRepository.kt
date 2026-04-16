package de.noah_ruben.data.blog

import kotlin.io.path.createDirectories
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.nio.file.Path

class BlogRepository(
    private val databasePath: Path,
) {
    private val database: Database by lazy {
        databasePath.toAbsolutePath().parent?.createDirectories()
        Database.connect(
            url = "jdbc:sqlite:${databasePath.toAbsolutePath()}",
            driver = "org.sqlite.JDBC",
        )
    }

    fun initialize() {
        transaction(database) {
            SchemaUtils.create(BlogPostTable)
        }
    }

    fun upsert(record: BlogPostRecord) {
        transaction(database) {
            val existing = BlogPostEntity.all().firstOrNull { it.slug == record.slug }
            if (existing == null) {
                BlogPostEntity.new {
                    updateFrom(record)
                }
                return@transaction
            }
            existing.updateFrom(record)
        }
    }

    fun markDeleted(slug: String) {
        transaction(database) {
            BlogPostEntity.all()
                .firstOrNull { it.slug == slug }
                ?.isDeleted = true
        }
    }

    fun findBySlug(slug: String): BlogPostRecord? = transaction(database) {
        BlogPostEntity.all()
            .firstOrNull { it.slug == slug }
            ?.toRecord()
    }

    fun listVisible(): List<BlogPostRecord> = transaction(database) {
        BlogPostEntity.all()
            .filterNot { it.isDeleted }
            .map(BlogPostEntity::toRecord)
            .sortedByDescending(BlogPostRecord::publishedDate)
    }
}
