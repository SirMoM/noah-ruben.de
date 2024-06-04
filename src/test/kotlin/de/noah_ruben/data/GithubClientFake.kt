package de.noah_ruben.data

import de.noah_ruben.data.model.Repository
import de.noah_ruben.data.model.SimpleUser

val fakeUser = SimpleUser(
    login = "user",
    id = 123,
    avatar_url = "https://example.com/avatar.jpg",
    gravatar_id = null,
    site_admin = false,
    node_id = "123",
)

val fakeRepositoryData = Repository(
    id = 1,
    node_id = "abc123",
    name = "dummy-repo",
    full_name = "user/dummy-repo",
    owner = fakeUser,
    private = false,
    description = "A dummy repository",
    fork = false,
    language = "Kotlin",
    html_url = "https://example.com/user/dummy-repo",
    forks_count = 0,
    stargazers_count = 0,
    watchers_count = 0,
    size = 1024,
    default_branch = "main",
    open_issues_count = 0,
    is_template = false,
    topics = listOf("dummy", "example"),
    has_issues = true,
    has_projects = false,
    has_wiki = false,
    has_pages = false,
    has_downloads = false,
    has_discussions = false,
    archived = false,
    disabled = false,
    visibility = "public",
    pushed_at = "2024-04-20T12:00:00Z",
    created_at = "2024-04-20T00:00:00Z",
    updated_at = "2024-04-20T12:00:00Z",
    forks = 0,
    open_issues = 0,
    watchers = 0,
    allow_forking = true,
    web_commit_signoff_required = false,
)

class GithubClientFake : RepositoryClient {

    override suspend fun getRepositories(): List<Repository> {
        return listOf(fakeRepositoryData)
    }

    override suspend fun getRepositoryLanguages(repositoryName: String, unit: () -> Unit): List<String> {
        return listOf("Lua")
    }
}
