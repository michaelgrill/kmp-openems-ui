package io.openems.kmp.data.websocket.core.model

import kotlinx.serialization.Serializable

@Serializable
data class EdgeRpcResponse(
    val edgeId: String,
    val payload: JsonRpcResponse,
)