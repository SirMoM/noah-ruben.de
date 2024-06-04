package de.noah_ruben.data

import kotlinx.serialization.Serializable

@Serializable
data class Project(
    val stars: Int,
    val topics: List<String>,
    val languages: List<String>,
    val releases: String,
    val name: String,
    val description: String,
    val githubLink: String,
    val link: String,
)
