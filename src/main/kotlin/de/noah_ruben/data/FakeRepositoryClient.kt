package de.noah_ruben.data

import de.noah_ruben.data.model.github.Repository
import de.noah_ruben.data.model.github.SimpleUser

private val fakeUser = SimpleUser(
    login = "user",
    id = 123,
    avatar_url = "https://example.com/avatar.jpg",
    gravatar_id = null,
    site_admin = false,
    node_id = "123",
)

private val fakeRepositoryData = Repository(
    id = 1,
    nodeId = "abc123",
    name = "dummy-repo",
    fullName = "user/dummy-repo",
    owner = fakeUser,
    private = false,
    description = "A dummy repository",
    fork = false,
    language = "Kotlin",
    htmlUrl = "https://example.com/user/dummy-repo",
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

class FakeRepositoryClient : RepositoryClient {

    override suspend fun getRepositories(): List<Repository> = listOf(fakeRepositoryData)

    override suspend fun getRepositoryLanguages(repositoryName: String): List<String> = listOf("Lua")
}
