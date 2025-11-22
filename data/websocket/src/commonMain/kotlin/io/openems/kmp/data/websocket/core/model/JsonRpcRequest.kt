package io.openems.kmp.data.websocket.core.model

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@Serializable
data class JsonRpcRequest(
    val jsonrpc: String,
    val id: String,
    val method: String,
    val params: JsonRpcParams,
) {
    @OptIn(ExperimentalUuidApi::class)
    constructor(
        id: String = Uuid.NIL.toString(),
        method: String,
        params: JsonRpcParams,
    ) : this(
        jsonrpc = "2.0",
        id = id,
        method = method,
        params = params,
    )
}
