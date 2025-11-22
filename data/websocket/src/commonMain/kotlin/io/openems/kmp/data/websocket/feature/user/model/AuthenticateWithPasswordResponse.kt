package io.openems.kmp.data.websocket.feature.user.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthenticateWithPasswordResponse(
    val token: String,
    val user: User,
    // val edges: List<Edge>, deprecated
)