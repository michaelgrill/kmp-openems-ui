package io.openems.kmp.data.websocket.core.model

import kotlinx.serialization.Serializable

@Serializable
data class EdgeRpcRequest(
    val edgeId: String,
    val payload: JsonRpcRequest,
) : JsonRpcParams()