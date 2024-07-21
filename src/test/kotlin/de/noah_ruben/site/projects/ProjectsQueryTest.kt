@file:Suppress("ktlint:standard:no-wildcard-imports")

package de.noah_ruben.site.projects

import de.noah_ruben.data.model.Project
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import java.time.LocalDateTime.now
import kotlin.test.Test
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProjectsQueryTest {
    private lateinit var projects: List<Project>

    @Test
    fun sortedByPopularity() {
        val result = this.projects.sortedBy(OrderBy.Popularity)

        assertEquals(listOf(projects[1], projects[3], projects[0], projects[2], projects[4]), result, "Lists should be ordered the same")
    }

    @Test
    fun sortedByRelevance() {
        val result = this.projects.sortedBy(OrderBy.Relevance)

        assertEquals(TODO("THIS NEEDS TO BE IMPLEMENTED"), true)
    }

    @Test
    fun sortedByDate() {
        val result = this.projects.sortedBy(OrderBy.Date)
        val expected = listOf(projects[4], projects[0], projects[3], projects[1], projects[2])
        assertEquals(expected = expected, actual = result)
    }

    @Test
    fun filerByTopic() {
        val result = this.projects.filerByTopic("Android")
        assertEquals(listOf(projects[0]), result, "Lists should be equal")
    }

    @Test
    fun filerByLanguage() {
        val result = this.projects.filerByLanguage("Kotlin")
        assertEquals(listOf(projects.first(), projects[1]), result, "Lists should be equal")
    }

    @Test
    fun query() {
    }

    @BeforeEach
    fun setUp() {
        projects = listOf(
            Project(
                stars = 150,
                topics = listOf("Kotlin", "Android", "UI"),
                languages = listOf("Kotlin"),
                releases = "v1.0.0",
                name = "AwesomeApp",
                description = "An awesome app with great UI.",
                githubLink = "https://github.com/user/awesomeapp",
                link = "https://awesomeapp.com",
                created = now().minusYears(1),
                lastModified = now().minusDays(1),
            ),
            Project(
                stars = 300,
                topics = listOf("Web", "Backend", "API"),
                languages = listOf("Kotlin", "CSS", "JS"),
                releases = "v2.3.1",
                name = "SuperAPI",
                description = "A super API for all your needs.",
                githubLink = "https://github.com/user/superapi",
                link = "https://superapi.com",
                created = now().minusYears(2),
                lastModified = now().minusDays(32),
            ),
            Project(
                stars = 75,
                topics = listOf("CLI", "Tools"),
                languages = listOf("C"),
                releases = "v0.9.0",
                name = "CommandTool",
                description = "A powerful command-line tool.",
                githubLink = "https://github.com/user/commandtool",
                link = "https://commandtool.com",
                created = now().minusYears(1).minusMonths(5),
                lastModified = now().minusDays(100),
            ),
            Project(
                stars = 200,
                topics = listOf("Library", "Networking"),
                languages = listOf("go"),
                releases = "v1.2.5",
                name = "NetLib",
                description = "A networking library for Kotlin.",
                githubLink = "https://github.com/user/netlib",
                link = "https://netlib.com",
                created = now().minusYears(5).plusWeeks(8),
                lastModified = now().minusDays(2),
            ),
            Project(
                stars = 50,
                topics = listOf("Game"),
                languages = listOf("gdscript", "C++"),
                releases = "v1.0.0",
                name = "FunGame",
                description = "A fun game for Android devices.",
                githubLink = "https://github.com/user/fungame",
                link = "https://fungame.com",
                created = now().minusDays(1),
                lastModified = now().minusDays(0),
            ),
        )
    }

    @AfterEach
    fun tearDown() {
        this.projects = emptyList()
    }
}
