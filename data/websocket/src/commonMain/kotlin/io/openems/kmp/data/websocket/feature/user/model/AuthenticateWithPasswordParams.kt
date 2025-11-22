package io.openems.kmp.data.websocket.feature.user.model

import io.openems.kmp.data.websocket.core.model.JsonRpcParams
import kotlinx.serialization.Serializable

@Serializable
data class AuthenticateWithPasswordParams(
    val username: String,
    val password: String,
) : JsonRpcParams()