package de.noah_ruben.data.blog

import org.slf4j.LoggerFactory
import java.nio.file.ClosedWatchServiceException
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
import java.nio.file.StandardWatchEventKinds.ENTRY_DELETE
import java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY
import java.nio.file.WatchService
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.extension
import kotlin.io.path.nameWithoutExtension

class BlogIngestionService(
    private val paths: BlogPaths,
    private val repository: BlogRepository = BlogRepository(paths.databasePath),
    private val converter: BlogConverter = BlogConverter(),
    private val cache: BlogIndexCache = BlogIndexCache(),
) : AutoCloseable {
    private val logger = LoggerFactory.getLogger(BlogIngestionService::class.java)
    private val queue = LinkedBlockingQueue<Path>()
    private val queuedSlugs = mutableSetOf<String>()
    private val queueLock = Any()
    private val workerExecutor = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "blog-ingestion-worker").apply { isDaemon = true }
    }
    private val initialized = AtomicBoolean(false)
    private val stopRequested = AtomicBoolean(false)
    private var watchService: WatchService? = null
    private var watchThread: Thread? = null

    fun initialize() {
        if (!initialized.compareAndSet(false, true)) {
            return
        }

        paths.sourceDir.createDirectories()
        paths.outputDir.createDirectories()

        repository.initialize()
        cache.refresh(repository.listVisible())
        markMissingSourcesDeleted()
        startWorker()
        scanSourceDirectory()
        startWatcher()
    }

    fun visiblePosts(): List<BlogPostRecord> = cache.visiblePosts()

    fun repository(): BlogRepository = repository

    fun ensurePostAvailable(slug: String): BlogPostRecord? {
        val sourcePath = paths.sourceDir.resolve("$slug.md")
        if (!sourcePath.exists()) {
            repository.findBySlug(slug)?.let {
                logger.error("Blog source disappeared while serving slug '{}': {}", slug, it.sourcePath)
                repository.markDeleted(slug)
                refreshCache()
            }
            return null
        }

        val existing = repository.findBySlug(slug)
        if (existing?.isDeleted == true) {
            return null
        }

        if (existing == null || BlogStorage.isStale(sourceLastModified(sourcePath), existing.htmlLastGenerated, paths.htmlPathFor(slug))) {
            processSourceNow(sourcePath)
        }

        return repository.findBySlug(slug)?.takeUnless(BlogPostRecord::isDeleted)
    }

    fun awaitIdle(timeoutMillis: Long = 3_000L): Boolean {
        val deadline = System.currentTimeMillis() + timeoutMillis
        while (System.currentTimeMillis() < deadline) {
            synchronized(queueLock) {
                if (queue.isEmpty() && queuedSlugs.isEmpty()) {
                    return true
                }
            }
            Thread.sleep(50)
        }

        synchronized(queueLock) {
            return queue.isEmpty() && queuedSlugs.isEmpty()
        }
    }

    override fun close() {
        stopRequested.set(true)
        watchService?.close()
        watchThread?.join(500)
        workerExecutor.shutdownNow()
        workerExecutor.awaitTermination(1, TimeUnit.SECONDS)
    }

    private fun markMissingSourcesDeleted() {
        repository.listVisible()
            .filterNot { Path.of(it.sourcePath).exists() }
            .forEach { missing ->
                logger.error("Blog source file missing at startup: {}", missing.sourcePath)
                repository.markDeleted(missing.slug)
            }

        refreshCache()
    }

    private fun startWorker() {
        workerExecutor.submit {
            while (!stopRequested.get()) {
                val sourcePath = queue.poll(250, TimeUnit.MILLISECONDS) ?: continue
                processQueuedSource(sourcePath)
            }
        }
    }

    private fun scanSourceDirectory() {
        Files.list(paths.sourceDir).use { stream ->
            stream
                .filter { Files.isRegularFile(it) && it.extension == "md" }
                .forEach(::enqueueSource)
        }
    }

    private fun startWatcher() {
        val service = FileSystems.getDefault().newWatchService()
        paths.sourceDir.register(service, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE)
        watchService = service
        watchThread = thread(
            start = true,
            isDaemon = true,
            name = "blog-ingestion-watcher",
        ) {
            while (!stopRequested.get()) {
                val key = try {
                    service.poll(250, TimeUnit.MILLISECONDS)
                } catch (_: ClosedWatchServiceException) {
                    break
                } ?: continue

                key.pollEvents().forEach { event ->
                    val changedPath = paths.sourceDir.resolve(event.context() as Path)
                    if (changedPath.extension != "md") {
                        return@forEach
                    }

                    when (event.kind()) {
                        ENTRY_DELETE -> {
                            logger.error("Blog source file deleted while watched: {}", changedPath)
                            repository.markDeleted(changedPath.nameWithoutExtension)
                            refreshCache()
                        }

                        ENTRY_CREATE,
                        ENTRY_MODIFY,
                        -> enqueueSource(changedPath)
                    }
                }

                key.reset()
            }
        }
    }

    private fun enqueueSource(sourcePath: Path) {
        val slug = sourcePath.nameWithoutExtension
        synchronized(queueLock) {
            if (!queuedSlugs.add(slug)) {
                return
            }
            queue.put(sourcePath)
        }
    }

    private fun processQueuedSource(sourcePath: Path) {
        val slug = sourcePath.nameWithoutExtension
        try {
            processSourceNow(sourcePath)
        } finally {
            synchronized(queueLock) {
                queuedSlugs.remove(slug)
            }
        }
    }

    private fun processSourceNow(sourcePath: Path) {
        if (!sourcePath.exists()) {
            repository.markDeleted(sourcePath.nameWithoutExtension)
            refreshCache()
            return
        }

        val converted = converter.convert(sourcePath)
        val htmlPath = paths.htmlPathFor(converted.slug)
        BlogStorage.writeContent(htmlPath, converted.renderedHtml)

        repository.upsert(
            BlogPostRecord(
                slug = converted.slug,
                sourcePath = sourcePath.toAbsolutePath().toString(),
                sourceLastModified = sourceLastModified(sourcePath),
                title = converted.title,
                summary = converted.summary,
                publishedDate = converted.publishedDate,
                tags = converted.tags,
                excerpt = converted.excerpt,
                htmlPath = htmlPath.toAbsolutePath().toString(),
                htmlLastGenerated = System.currentTimeMillis(),
                isDeleted = false,
            ),
        )

        refreshCache()
    }

    private fun refreshCache() {
        cache.refresh(repository.listVisible())
    }

    private fun sourceLastModified(sourcePath: Path): Long = Files.getLastModifiedTime(sourcePath).toMillis()
}
