package de.noah_ruben.data.blog

import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText
import java.nio.file.Path

object BlogStorage {
    fun writeContent(htmlPath: Path, renderedHtml: String) {
        htmlPath.toAbsolutePath().parent?.createDirectories()
        htmlPath.writeText(renderedHtml)
    }

    fun readContent(htmlPath: Path): String = htmlPath.readText()

    fun isStale(
        sourceLastModified: Long,
        htmlLastGenerated: Long,
        htmlPath: Path,
    ): Boolean = !htmlPath.exists() || sourceLastModified > htmlLastGenerated
}
