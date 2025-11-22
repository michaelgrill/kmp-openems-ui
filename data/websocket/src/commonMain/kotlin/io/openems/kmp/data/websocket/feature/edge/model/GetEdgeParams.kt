package io.openems.kmp.data.websocket.feature.edge.model

import io.openems.kmp.data.websocket.core.model.JsonRpcParams
import kotlinx.serialization.Serializable

@Serializable
data class GetEdgeParams(
    val edgeId: String,
) : JsonRpcParams()
