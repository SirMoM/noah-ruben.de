package de.noah_ruben.data

import kotlinx.coroutines.runBlocking
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class CacheTest {

    @BeforeTest
    fun init() {
        Cache.githubClient = FakeRepositoryClient()
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
}
