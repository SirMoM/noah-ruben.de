package com.dvag.noah.ruben.build.quality

import org.jetbrains.amper.plugins.Input
import org.jetbrains.amper.plugins.TaskAction
import java.nio.file.Path

/**
 * Represents the different output formats that can be used for reporting.
 *
 * Each format specifies how the output should be structured and displayed,
 * allowing for compatibility with various tools and ease of readability.
 *
 * @param value The string representation of the format.
 */
enum class Format(val value: String) {
    BASELINE("baseline"),
    CHECKSTYLE("checkstyle"),
    FORMAT("format"),
    HTML("html"),
    JSON("json"),
    PLAIN("plain"),
    PLAIN_SUMMARY("plain-summary"),
    SARIF("sarif"), ;

    override fun toString(): String = this.value
}

@TaskAction
fun runKtlint(
    @Input moduleRootDir: Path,
    patterns: List<String>,
) {
    val command = buildList {
        add("ktlint")
        add("--color")
        add("--reporter")
        add(Format.PLAIN.value)
        add("-F")
        add("--editorconfig")
        add("$moduleRootDir/.editorconfig")
        add("--relative")
        addAll(patterns)
    }

    println(command.joinToString(" "))

    val process = ProcessBuilder(command).directory(moduleRootDir.toFile()).inheritIO().start()

    val exitCode = process.waitFor()
    check(exitCode == 0) { "ktlint failed with exit code $exitCode" }
}
