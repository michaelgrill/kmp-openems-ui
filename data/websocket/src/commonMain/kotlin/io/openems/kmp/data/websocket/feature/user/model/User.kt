package io.openems.kmp.data.websocket.feature.user.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val name: String,
    val language: String,
    val hasMultipleEdges: Boolean,
    // val settings: Map<String, String>,
    val globalRole: String,
)