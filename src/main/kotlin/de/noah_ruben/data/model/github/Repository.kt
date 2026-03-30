package de.noah_ruben.data.model.github

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Repository(
    val id: Int,
    @SerialName("node_id")
    val nodeId: String,
    val name: String,
    @SerialName("full_name")
    val fullName: String,
    val owner: SimpleUser,
    val private: Boolean,
    val description: String?,
    val fork: Boolean,
    val language: String?,
    @SerialName("html_url")
    val htmlUrl: String?,
    @SerialName("forks_count")
    val forksCount: Int,
    @SerialName("stargazers_count")
    val stargazersCount: Int,
    @SerialName("watchers_count")
    val watchersCount: Int,
    val size: Int,
    @SerialName("default_branch")
    val defaultBranch: String,
    @SerialName("open_issues_count")
    val openIssuesCount: Int,
    @SerialName("is_template")
    val isTemplate: Boolean,
    val topics: List<String>,
    @SerialName("has_issues")
    val hasIssues: Boolean,
    @SerialName("has_projects")
    val hasProjects: Boolean,
    @SerialName("has_wiki")
    val hasWiki: Boolean,
    @SerialName("has_pages")
    val hasPages: Boolean,
    @SerialName("has_downloads")
    val hasDownloads: Boolean,
    @SerialName("has_discussions")
    val hasDiscussions: Boolean,
    val archived: Boolean,
    val disabled: Boolean,
    val visibility: String,
    @SerialName("pushed_at")
    val pushedAt: String?,
    @SerialName("created_at")
    val createdAt: String?,
    @SerialName("updated_at")
    val updatedAt: String?,
    val forks: Int,
    @SerialName("open_issues")
    val openIssues: Int,
    val watchers: Int,
    @SerialName("allow_forking")
    val allowForking: Boolean,
    @SerialName("web_commit_signoff_required")
    val webCommitSignoffRequired: Boolean,
    val homepage: String?,
)
