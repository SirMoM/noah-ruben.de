package de.noah_ruben.site.projects

import de.noah_ruben.data.model.Project
import java.time.LocalDateTime.now
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ProjectsQueryTest {
    private lateinit var projects: List<Project>

    @Test
    fun filterBySearchParameters() {
        val searchParameters = SearchParameters(
            query = "",
            topic = TOPIC_PLACEHOLDER,
            language = "Kotlin",
            orderBy = OrderBy.Popularity,
            descending = false,
            withSearchbar = false,
        )

        val result = this.projects.filterBySearchParameters(searchParameters)

        val expected = listOf(projects[0], projects[1])

        assertEquals(expected, result, "Lists should be filtered by language and ordered by popularity ascending")

        val result2 = this.projects.filterBySearchParameters(searchParameters.copy(descending = true))
        assertEquals(expected.reversed(), result2, "Lists should be filtered by language and ordered by popularity ascending")
    }

    @Test
    fun sortedByPopularity() {
        val expectedAscending = listOf(projects[4], projects[2], projects[0], projects[3], projects[1])

        // Test descending order (highest stars first)
        val resultDescending = this.projects.sortedBy(OrderBy.Popularity, true)
        assertEquals(expectedAscending.reversed(), resultDescending, "Lists should be ordered the same (descending by popularity)")

        // Test ascending order (lowest stars first)
        val resultAscending = this.projects.sortedBy(OrderBy.Popularity, false)
        assertEquals(expectedAscending, resultAscending, "Lists should be ordered the same (ascending by popularity)")
    }

    @Test
    fun sortedByRelevance() {
        val expectedAscending = listOf(projects[4], projects[2], projects[0], projects[3], projects[1])

        // Test descending order (based on the original test's expected list for descending=false)
        val resultDescending = this.projects.sortedBy(OrderBy.Relevance, true)
        assertEquals(expectedAscending.reversed(), resultDescending, "Lists should be ordered the same (descending by relevance)")

        // Test ascending order
        val resultAscending = this.projects.sortedBy(OrderBy.Relevance, false)
        assertEquals(expectedAscending, resultAscending, "Lists should be ordered the same (ascending by relevance)")
    }

    @Test
    fun sortedByDate() {
        val expectedAscending = listOf(projects[3], projects[1], projects[2], projects[0], projects[4])

        // Test descending order (newest created date first)
        val resultDescending = this.projects.sortedBy(OrderBy.Date, true)
        assertEquals(expectedAscending.reversed(), resultDescending, "Lists should be ordered the same (descending by date)")

        // Test ascending order (oldest created date first)
        val resultAscending = this.projects.sortedBy(OrderBy.Date, false)
        assertEquals(expectedAscending, resultAscending, "Lists should be ordered the same (ascending by date)")
    }

    @Test
    fun filerByTopic() {
        val result = this.projects.filerByTopic("Android")
        assertEquals(listOf(projects[0]), result, "Lists should be equal")
    }

    @Test
    fun filerByLanguage() {
        val result = this.projects.filerByLanguage("Kotlin")
        // Projects 0 and 1 use Kotlin
        assertEquals(listOf(projects[0], projects[1]), result, "Lists should be equal")
    }

    @Test
    fun query() {
        val result = this.projects.query("Android")
        assertEquals(listOf(projects[4]), result, "Lists should be equal")
    }

    @BeforeTest
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
                description = "A networking library for Kotlin.", // Note: uses Kotlin in description but not language list
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

    @AfterTest
    fun tearDown() {
        this.projects = emptyList()
    }
}
