package de.noah_ruben.data

import io.opentelemetry.api.GlobalOpenTelemetry
import io.opentelemetry.api.common.AttributeKey
import io.opentelemetry.api.common.Attributes
import java.util.concurrent.atomic.AtomicLong

enum class CacheAccessResult {
    HIT,
    MISS,
    STALE_FALLBACK,
}

enum class CacheRefreshResult {
    SUCCESS,
    FAILURE,
}

interface CacheTelemetry {
    fun recordAccess(result: CacheAccessResult)

    fun recordRefresh(result: CacheRefreshResult, durationNanos: Long)

    fun recordProjectCount(count: Int)
}

object NoopCacheTelemetry : CacheTelemetry {
    override fun recordAccess(result: CacheAccessResult) = Unit

    override fun recordRefresh(result: CacheRefreshResult, durationNanos: Long) = Unit

    override fun recordProjectCount(count: Int) = Unit
}

class OtelCacheTelemetry private constructor() : CacheTelemetry {
    private val meter = GlobalOpenTelemetry.getMeter("de.noah_ruben.cache")
    private val resultAttribute = AttributeKey.stringKey("result")
    private val projectCount = AtomicLong(0)
    private val accessCounter = meter.counterBuilder("noah_ruben.cache.access.total")
        .setDescription("Number of cache access outcomes.")
        .build()
    private val refreshCounter = meter.counterBuilder("noah_ruben.cache.refresh.total")
        .setDescription("Number of cache refresh outcomes.")
        .build()
    private val refreshDuration = meter.histogramBuilder("noah_ruben.cache.refresh.duration")
        .setDescription("Duration of cache refresh attempts.")
        .setUnit("ms")
        .build()

    init {
        meter.gaugeBuilder("noah_ruben.cache.projects.count")
            .ofLongs()
            .setDescription("Number of projects currently held in cache.")
            .buildWithCallback { measurement ->
                measurement.record(projectCount.get())
            }
    }

    override fun recordAccess(result: CacheAccessResult) {
        accessCounter.add(1, Attributes.of(resultAttribute, result.name.lowercase()))
    }

    override fun recordRefresh(result: CacheRefreshResult, durationNanos: Long) {
        val resultAttributes = Attributes.of(resultAttribute, result.name.lowercase())
        refreshCounter.add(1, resultAttributes)
        refreshDuration.record(durationNanos / 1_000_000.0, resultAttributes)
    }

    override fun recordProjectCount(count: Int) {
        projectCount.set(count.toLong())
    }

    companion object {
        fun create(): CacheTelemetry = OtelCacheTelemetry()
    }
}
