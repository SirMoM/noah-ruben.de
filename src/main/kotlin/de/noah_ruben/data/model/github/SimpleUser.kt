package de.noah_ruben.data.model.github

import kotlinx.serialization.Serializable

@Serializable
data class SimpleUser(
    val name: String? = null,
    val email: String? = null,
    val login: String,
    val id: Int,
    val node_id: String,
    val avatar_url: String,
    val gravatar_id: String?,
    val site_admin: Boolean,
    val starred_at: String? = null,
)
