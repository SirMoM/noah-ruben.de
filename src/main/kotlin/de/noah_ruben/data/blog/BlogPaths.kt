package de.noah_ruben.data.blog

import io.ktor.server.config.ApplicationConfig
import java.nio.file.Path
import kotlin.io.path.Path

data class BlogPaths(
    val sourceDir: Path,
    val outputDir: Path,
    val databasePath: Path,
) {
    fun htmlPathFor(slug: String): Path = outputDir.resolve(slug).resolve("content.html")

    companion object {
        fun from(config: ApplicationConfig): BlogPaths = BlogPaths(
            sourceDir = Path(config.property("blog.sourceDir").getString()),
            outputDir = Path(config.property("blog.outputDir").getString()),
            databasePath = Path(config.property("blog.databasePath").getString()),
        )
    }
}
