package de.noah_ruben.build.run

import org.jetbrains.amper.plugins.Input
import org.jetbrains.amper.plugins.TaskAction
import java.nio.file.Path

@TaskAction
fun runLocal(
    @Input moduleRootDir: Path,
    githubToken: String,
    githubUrl: String,
) {
    val command = listOf("./amper", "run")

    println(command.joinToString(" "))

    val process = ProcessBuilder(command)
        .directory(moduleRootDir.toFile())
        .apply {
            environment()["GITHUB_TOKEN"] = githubToken
            environment()["GITHUB_URL"] = githubUrl
        }
        .inheritIO()
        .start()

    val exitCode = process.waitFor()
    check(exitCode == 0) { "runLocal failed with exit code $exitCode" }
}
