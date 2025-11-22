package io.openems.kmp.data.websocket.feature.edge.model

import io.openems.kmp.data.websocket.core.model.JsonRpcParams
import kotlinx.serialization.Serializable

@Serializable
data class GetEdgesParams(
    val limit: Int,
    val page: Int, // starts at 0
    val query: String?,
    // val searchParams:
) : JsonRpcParams()
