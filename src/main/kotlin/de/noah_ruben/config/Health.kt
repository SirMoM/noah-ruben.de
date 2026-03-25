package de.noah_ruben.config

import kotlinx.serialization.Serializable

const val HEALTH_OK = "ok"
const val HEALTH_DEGRADED = "degraded"
const val HEALTH_DOWN = "down"

@Serializable
data class HealthCheckResult(
    val status: String,
    val message: String,
)

fun overallHealthStatus(checks: Map<String, HealthCheckResult>): String =
    when {
        checks.values.any { it.status == HEALTH_DOWN } -> HEALTH_DOWN
        checks.values.any { it.status == HEALTH_DEGRADED } -> HEALTH_DEGRADED
        else -> HEALTH_OK
    }
