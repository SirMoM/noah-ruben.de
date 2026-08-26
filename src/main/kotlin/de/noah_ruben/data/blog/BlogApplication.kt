package de.noah_ruben.data.blog

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopped
import io.ktor.util.AttributeKey

private val BLOG_INGESTION_ATTRIBUTE = AttributeKey<BlogIngestionService>("blog-ingestion-service")

fun Application.initializeBlogIngestion() {
    if (attributes.contains(BLOG_INGESTION_ATTRIBUTE)) {
        return
    }

    val service = BlogIngestionService(BlogPaths.from(environment.config))
    service.initialize()
    attributes.put(BLOG_INGESTION_ATTRIBUTE, service)
    monitor.subscribe(ApplicationStopped) {
        service.close()
    }
}

fun Application.blogIngestionService(): BlogIngestionService = attributes[BLOG_INGESTION_ATTRIBUTE]
