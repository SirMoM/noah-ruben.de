package de.noah_ruben.data

import de.noah_ruben.data.model.github.Repository
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class RepositorySerializationTest {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    @Test
    fun deserializesSnakeCaseRepositoryJsonIntoCamelCaseProperties() {
        val repository = json.decodeFromString<Repository>(
            """
            {
              "id": 1,
              "node_id": "node-1",
              "name": "dummy-repo",
              "full_name": "user/dummy-repo",
              "owner": {
                "login": "user",
                "id": 123,
                "node_id": "user-node",
                "avatar_url": "https://example.com/avatar.jpg",
                "gravatar_id": null,
                "site_admin": false
              },
              "private": false,
              "description": "A dummy repository",
              "fork": false,
              "language": "Kotlin",
              "html_url": "https://example.com/user/dummy-repo",
              "forks_count": 0,
              "stargazers_count": 7,
              "watchers_count": 8,
              "size": 1024,
              "default_branch": "main",
              "open_issues_count": 2,
              "is_template": false,
              "topics": ["dummy", "example"],
              "has_issues": true,
              "has_projects": false,
              "has_wiki": false,
              "has_pages": false,
              "has_downloads": false,
              "has_discussions": false,
              "archived": false,
              "disabled": false,
              "visibility": "public",
              "pushed_at": "2024-04-20T12:00:00Z",
              "created_at": "2024-04-20T00:00:00Z",
              "updated_at": "2024-04-20T12:00:00Z",
              "forks": 0,
              "open_issues": 2,
              "watchers": 8,
              "allow_forking": true,
              "web_commit_signoff_required": false,
              "homepage": "https://example.com"
            }
            """.trimIndent(),
        )

        assertEquals("user/dummy-repo", repository.fullName)
        assertEquals("https://example.com/user/dummy-repo", repository.htmlUrl)
        assertEquals(7, repository.stargazersCount)
        assertEquals("2024-04-20T00:00:00Z", repository.createdAt)
    }
}
