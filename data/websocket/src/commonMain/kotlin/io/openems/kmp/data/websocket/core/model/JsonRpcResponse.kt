package io.openems.kmp.data.websocket.core.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class JsonRpcResponse(
    val jsonrpc: String,
    val id: String? = null,
    val result: JsonElement? = null,
    val error: JsonElement? = null,

    // notification
    val method: String? = null,
    val params: JsonElement? = null,
)
