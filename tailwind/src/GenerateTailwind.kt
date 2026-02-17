package de.noah_ruben.build.tailwind

import org.jetbrains.amper.plugins.Input
import org.jetbrains.amper.plugins.Output
import org.jetbrains.amper.plugins.TaskAction
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteRecursively
import kotlin.io.path.exists
import kotlin.io.path.pathString

@TaskAction
@OptIn(ExperimentalPathApi::class)
fun generateTailwind(
    @Input inputCss: Path,
    @Input packageJson: Path,
    @Input packageLock: Path,
    @Output generatedResourceDir: Path,
) {
    println("[tailwind] Starting Tailwind generation")
    println("[tailwind] Input css: ${inputCss.pathString}")
    println("[tailwind] Output dir: ${generatedResourceDir.pathString}")

    validateRequiredFiles(inputCss, packageJson, packageLock)

    val outputCss = generatedResourceDir.resolve("static/style.css")
    val tailwindDir = packageJson.parent

    println("[tailwind] Cleaning old output")
    generatedResourceDir.deleteRecursively()
    outputCss.parent.createDirectories()

    runCommand(listOf("npm", "ci"), tailwindDir, "install dependencies")
    runCommand(
        listOf("npx", "@tailwindcss/cli", "-i", inputCss.pathString, "-o", outputCss.pathString),
        tailwindDir,
        "compile css",
    )

    println("[tailwind] Generated css: ${outputCss.pathString}")
    println("[tailwind] Tailwind generation complete")
}

private fun validateRequiredFiles(inputCss: Path, packageJson: Path, packageLock: Path) {
    require(inputCss.exists()) { "Missing required input css file: ${inputCss.pathString}" }
    require(packageJson.exists()) { "Missing required package.json: ${packageJson.pathString}" }
    require(packageLock.exists()) { "Missing required package-lock.json: ${packageLock.pathString}" }
    println("[tailwind] Required files are present")
}

private fun runCommand(command: List<String>, workingDirectory: Path, step: String) {
    println("[tailwind] Running $step: ${command.joinToString(" ")}")
    val process = ProcessBuilder(command).directory(workingDirectory.toFile()).inheritIO().start()
    val exitCode = process.waitFor()
    check(exitCode == 0) {
        "Tailwind step '$step' failed with exit code $exitCode"
    }
    println("[tailwind] Finished $step")
}
