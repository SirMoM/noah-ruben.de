package de.noah_ruben.data

import org.junit.jupiter.api.Assertions.assertArrayEquals
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CacheTest {

    @BeforeTest
    fun init() {
        Cache.githubClient = WiremockClient()
        Cache.initialize()
    }

    @Test
    fun getProjects() {
        val projects = Cache.getProjects()

        assertEquals(projects.size, 26)
    }

    @Test
    fun getAllTopics() {
        val result = Cache.getAllTopics()
        assertArrayEquals(result.toTypedArray(), arrayOf("game", "game-development", "python", "strategy", "strategy-game", "discord", "discord-bot", "discord-js", "godot-logger", "gdscript", "godot", "godot-engine", "godot3", "mit-license", "program", "robocopy", "windows", "wip", "lejos-ev3", "ur"))
    }

    @Test
    fun getAllLanguages() {
        val result = Cache.getAllLanguages()
        println(result.joinToString("\", \""))
        assertArrayEquals(result.toTypedArray(), arrayOf("TeX", "Java", "CSS", "HTML", "JavaScript", "C++", "CMake", "GDScript", "C", "Python", "TypeScript", "Shell", "Batchfile", "GAP"))
    }
}
