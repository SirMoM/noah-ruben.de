package de.noah_ruben.site.cv

import io.ktor.server.config.ApplicationConfig
import java.io.File

enum class CvLanguage(
    val token: String,
    val toggleLabel: String,
    val displayName: String,
) {
    English(
        token = "eng",
        toggleLabel = "English",
        displayName = "English",
    ),
    German(
        token = "ger",
        toggleLabel = "Deutsch",
        displayName = "German",
    ),
    ;

    fun pageUrl(): String = "/cv?lang=$token"

    fun pdfUrlBase(): String = "/cv/pdf?lang=$token"

    companion object {
        fun fromQueryParameter(rawLanguage: String?): CvLanguage {
            val normalizedLanguage = rawLanguage?.trim()?.lowercase().orEmpty()

            return when {
                normalizedLanguage.isEmpty() -> English
                normalizedLanguage == English.token -> English
                normalizedLanguage == German.token -> German
                else -> throw IllegalArgumentException(unsupportedCvLanguageMessage(rawLanguage?.trim().orEmpty()))
            }
        }

        fun fromCommandArguments(args: List<String>): CvLanguage {
            if (args.isEmpty()) return English
            if (args.size > 1) throw IllegalArgumentException("Usage: noahruben cv [eng|ger]")

            return fromQueryParameter(args.single())
        }
    }
}

enum class CvMode(
    val token: String,
) {
    Dark("dark"),
    Light("light"),
    ;

    companion object {
        fun fromQueryParameter(rawMode: String?): CvMode =
            when (rawMode?.trim()?.lowercase()) {
                Light.token -> Light
                else -> Dark
            }
    }
}

data class CvPageState(
    val selectedLanguage: CvLanguage,
    val errorMessage: String? = null,
)

data class CvPdfAsset(
    val language: CvLanguage,
    val mode: CvMode,
    val file: File,
)

class CvConfigurationException(message: String) : IllegalStateException(message)

class CvFileUnavailableException(message: String) : IllegalStateException(message)

class CvPdfResolver(
    private val config: ApplicationConfig,
) {
    fun resolve(language: CvLanguage, mode: CvMode): CvPdfAsset {
        val rootDirectoryPath = config.propertyOrNull("cv")?.getString()?.trim()
        if (rootDirectoryPath.isNullOrBlank()) {
            throw CvConfigurationException("CV root directory is not configured.")
        }

        val languageDirectory = File(rootDirectoryPath, language.token)
        if (!languageDirectory.isDirectory || !languageDirectory.canRead()) {
            throw CvFileUnavailableException("CV directory for ${language.displayName} is unavailable right now.")
        }

        val matchingPdfFiles = languageDirectory
            .listFiles()
            .orEmpty()
            .filter { file ->
                file.isFile &&
                    file.canRead() &&
                    file.name.lowercase().endsWith(".pdf") &&
                    file.name.contains("dark", ignoreCase = true) == (mode == CvMode.Dark)
            }

        val pdfFile = matchingPdfFiles.singleOrNull()
            ?: throw CvFileUnavailableException(
                "Expected exactly one ${mode.description()} CV PDF for ${language.displayName}.",
            )

        return CvPdfAsset(
            language = language,
            mode = mode,
            file = pdfFile,
        )
    }

    fun validate(language: CvLanguage) {
        resolve(language, CvMode.Dark)
        resolve(language, CvMode.Light)
    }
}

fun buildCvPageState(
    config: ApplicationConfig,
    language: CvLanguage,
): CvPageState {
    return try {
        CvPdfResolver(config).validate(language)
        CvPageState(
            selectedLanguage = language,
        )
    } catch (error: CvConfigurationException) {
        CvPageState(
            selectedLanguage = language,
            errorMessage = error.message,
        )
    } catch (error: CvFileUnavailableException) {
        CvPageState(
            selectedLanguage = language,
            errorMessage = error.message,
        )
    }
}

fun unsupportedCvLanguageMessage(rawLanguage: String): String =
    "Unsupported CV language '$rawLanguage'. Use 'eng' or 'ger'."

private fun CvMode.description(): String =
    when (this) {
        CvMode.Dark -> "dark"
        CvMode.Light -> "light"
    }
