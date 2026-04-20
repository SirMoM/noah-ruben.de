package de.noah_ruben.data

import de.noah_ruben.data.model.github.Repository
import de.noah_ruben.data.model.github.SimpleUser
import kotlinx.coroutines.runBlocking
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDateTime
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith

class CacheTest {

    @BeforeTest
    fun init() {
        Cache.githubClient = FakeRepositoryClient()
        Cache.telemetry = NoopCacheTelemetry
        setProjectsCache(lastCached = LocalDateTime.now().minusMinutes(16), projects = emptyList())
        Cache.initialize()
    }

    @Test
    fun getProjects() {
        val projects = Cache.getProjects()

        assertEquals(1, projects.size)
    }

    @Test
    fun getAllTopics() {
        val result = Cache.getAllTopics()

        assertEquals(setOf("dummy", "example"), result)
    }

    @Test
    fun getAllLanguages() {
        val result = Cache.getAllLanguages()

        assertEquals(setOf("Lua"), result)
    }

    @Test
    fun repositoryClientLanguagesCanBeRequestedWithoutCallback() = runBlocking {
        val result = FakeRepositoryClient().getRepositoryLanguages("dummy-repo")

        assertEquals(listOf("Lua"), result)
    }

    @Test
    fun cacheDoesNotUseRedundantToStringForReleases() {
        val cacheSource = Files.readString(Path.of("src/main/kotlin/de/noah_ruben/data/Cache.kt"))

        assertFalse(cacheSource.contains("releases = it.createdAt.toString()"))
    }

    @Test
    fun staleCacheRefreshReturnsFreshProjects() {
        primeCacheWith("cached-repo")
        setProjectsCache(lastCached = LocalDateTime.now().minusMinutes(16), projects = Cache.getProjects())

        Cache.githubClient = namedRepositoryClient("fresh-repo")

        val refreshedProjects = Cache.getProjects()

        assertEquals(listOf("fresh-repo"), refreshedProjects.map { it.name })
    }

    @Test
    fun staleCacheRefreshKeepsLastKnownGoodProjectsWhenRefreshFails() {
        primeCacheWith("cached-repo")
        val cachedProjects = Cache.getProjects()
        setProjectsCache(lastCached = LocalDateTime.now().minusMinutes(16), projects = cachedProjects)

        Cache.githubClient = object : RepositoryClient {
            override suspend fun getRepositories(): List<Repository> = error("backend unavailable")

            override suspend fun getRepositoryLanguages(repositoryName: String): List<String> = error("backend unavailable")
        }

        val projectsAfterFailedRefresh = Cache.getProjects()

        assertEquals(listOf("cached-repo"), projectsAfterFailedRefresh.map { it.name })
    }

    @Test
    fun initialFetchFailureStillFailsWhenNoCacheExists() {
        Cache.githubClient = object : RepositoryClient {
            override suspend fun getRepositories(): List<Repository> = error("backend unavailable")

            override suspend fun getRepositoryLanguages(repositoryName: String): List<String> = error("backend unavailable")
        }
        setProjectsCache(lastCached = LocalDateTime.now().minusMinutes(16), projects = emptyList())

        val error = assertFailsWith<IllegalStateException> {
            Cache.getProjects()
        }

        assertEquals("Project cache is empty and refresh failed.", error.message)
    }

    @Test
    fun freshCacheHitRecordsTelemetry() {
        val telemetry = RecordingCacheTelemetry()
        Cache.telemetry = telemetry

        val projects = Cache.getProjects()

        assertEquals(1, projects.size)
        assertEquals(listOf(CacheAccessResult.HIT), telemetry.accessResults)
        assertEquals(emptyList(), telemetry.refreshResults)
        assertEquals(listOf(1), telemetry.projectCounts)
    }

    @Test
    fun staleCacheRefreshFailureRecordsFallbackTelemetry() {
        primeCacheWith("cached-repo")
        val cachedProjects = Cache.getProjects()
        setProjectsCache(lastCached = LocalDateTime.now().minusMinutes(16), projects = cachedProjects)

        val telemetry = RecordingCacheTelemetry()
        Cache.telemetry = telemetry
        Cache.githubClient = object : RepositoryClient {
            override suspend fun getRepositories(): List<Repository> = error("backend unavailable")

            override suspend fun getRepositoryLanguages(repositoryName: String): List<String> = error("backend unavailable")
        }

        val projectsAfterFailedRefresh = Cache.getProjects()

        assertEquals(listOf("cached-repo"), projectsAfterFailedRefresh.map { it.name })
        assertEquals(listOf(CacheAccessResult.STALE_FALLBACK), telemetry.accessResults)
        assertEquals(listOf(CacheRefreshResult.FAILURE), telemetry.refreshResults)
        assertEquals(listOf(1), telemetry.projectCounts)
    }
}

private fun namedRepositoryClient(repositoryName: String): RepositoryClient = object : RepositoryClient {
    override suspend fun getRepositories(): List<Repository> = listOf(repository(repositoryName))

    override suspend fun getRepositoryLanguages(repositoryName: String): List<String> = listOf("Lua")
}

private fun repository(name: String): Repository = Repository(
    id = 1,
    nodeId = "node-$name",
    name = name,
    fullName = "user/$name",
    owner = SimpleUser(
        login = "user",
        id = 123,
        avatar_url = "https://example.com/avatar.jpg",
        gravatar_id = null,
        site_admin = false,
        node_id = "123",
    ),
    private = false,
    description = "Repository for $name",
    fork = false,
    language = "Kotlin",
    htmlUrl = "https://example.com/user/$name",
    forksCount = 0,
    stargazersCount = 0,
    watchersCount = 0,
    size = 1024,
    defaultBranch = "main",
    openIssuesCount = 0,
    isTemplate = false,
    topics = listOf("dummy", "example"),
    hasIssues = true,
    hasProjects = false,
    hasWiki = false,
    hasPages = false,
    hasDownloads = false,
    hasDiscussions = false,
    archived = false,
    disabled = false,
    visibility = "public",
    pushedAt = "2024-04-20T12:00:00Z",
    createdAt = "2024-04-20T00:00:00Z",
    updatedAt = "2024-04-20T12:00:00Z",
    forks = 0,
    openIssues = 0,
    watchers = 0,
    allowForking = true,
    webCommitSignoffRequired = false,
    homepage = "https://example.com",
)

private fun setProjectsCache(lastCached: LocalDateTime, projects: List<de.noah_ruben.data.model.Project>) {
    val field = Cache::class.java.getDeclaredField("projectsCache")
    field.isAccessible = true
    field.set(Cache, lastCached to projects)
}

private fun primeCacheWith(repositoryName: String) {
    Cache.githubClient = namedRepositoryClient(repositoryName)
    setProjectsCache(lastCached = LocalDateTime.now().minusMinutes(16), projects = emptyList())
    Cache.initialize()
}

private class RecordingCacheTelemetry : CacheTelemetry {
    val accessResults = mutableListOf<CacheAccessResult>()
    val refreshResults = mutableListOf<CacheRefreshResult>()
    val projectCounts = mutableListOf<Int>()

    override fun recordAccess(result: CacheAccessResult) {
        accessResults += result
    }

    override fun recordRefresh(result: CacheRefreshResult, durationNanos: Long) {
        refreshResults += result
    }

    override fun recordProjectCount(count: Int) {
        projectCounts += count
    }
}
