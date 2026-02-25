package de.noah_ruben.data

import org.junit.jupiter.api.Assertions.assertArrayEquals
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

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
        assertArrayEquals(arrayOf("dummy", "example"), result.toTypedArray())
    }

    @Test
    fun getAllLanguages() {
        val result = Cache.getAllLanguages()
        assertArrayEquals(arrayOf("Lua"), result.toTypedArray())
    }
}
