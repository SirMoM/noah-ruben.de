package de.noah_ruben.site.cv

import de.noah_ruben.config.HEALTH_DEGRADED
import de.noah_ruben.config.HEALTH_OK
import de.noah_ruben.config.HealthCheckResult
import io.ktor.server.config.ApplicationConfig
import org.slf4j.LoggerFactory
import java.io.File

private val logger = LoggerFactory.getLogger("CV")

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
        fun fromQueryParameter(rawMode: String?): CvMode = when (rawMode?.trim()?.lowercase()) {
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
                    mode.matches(file.name)
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

fun cvAssetsHealthCheck(config: ApplicationConfig): HealthCheckResult {
    val rootDirectoryPath = config.propertyOrNull("cv")?.getString()?.trim()
    if (rootDirectoryPath.isNullOrBlank()) {
        return HealthCheckResult(
            status = HEALTH_DEGRADED,
            message = "CV root directory is not configured.",
        )
    }

    val rootDirectory = File(rootDirectoryPath)
    if (!rootDirectory.exists()) {
        return HealthCheckResult(
            status = HEALTH_DEGRADED,
            message = "CV root directory '$rootDirectoryPath' does not exist.",
        )
    }

    if (!rootDirectory.isDirectory) {
        return HealthCheckResult(
            status = HEALTH_DEGRADED,
            message = "CV root directory '$rootDirectoryPath' is not a directory.",
        )
    }

    if (!rootDirectory.canRead()) {
        return HealthCheckResult(
            status = HEALTH_DEGRADED,
            message = "CV root directory '$rootDirectoryPath' is not readable.",
        )
    }

    val resolver = CvPdfResolver(config)
    val validationErrors = CvLanguage.entries.mapNotNull { language ->
        try {
            resolver.validate(language)
            null
        } catch (error: IllegalStateException) {
            error.message
        }
    }

    if (validationErrors.isNotEmpty()) {
        return HealthCheckResult(
            status = HEALTH_DEGRADED,
            message = validationErrors.joinToString(" "),
        )
    }

    return HealthCheckResult(
        status = HEALTH_OK,
        message = "CV assets are available in '$rootDirectoryPath'.",
    )
}

fun logCvAssetsStartupStatus(config: ApplicationConfig) {
    val health = cvAssetsHealthCheck(config)
    if (health.status == HEALTH_OK) {
        logger.info("CV assets ready. {}", health.message)
    } else {
        logger.warn("CV assets degraded at startup. {}", health.message)
    }
}

fun buildCvPageState(
    config: ApplicationConfig,
    language: CvLanguage,
): CvPageState = try {
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

fun unsupportedCvLanguageMessage(rawLanguage: String): String = "Unsupported CV language '$rawLanguage'. Use 'eng' or 'ger'."

private fun CvMode.description(): String = when (this) {
    CvMode.Dark -> "dark"
    CvMode.Light -> "light"
}

private fun CvMode.matches(fileName: String): Boolean = when (this) {
    CvMode.Dark -> fileName.contains("dark", ignoreCase = true)
    CvMode.Light -> fileName.contains("_light", ignoreCase = true)
}
