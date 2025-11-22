package io.openems.kmp.data.websocket.feature.edge.model

import kotlinx.serialization.Serializable

@Serializable
data class GetEdgesResponse(
    val edges: List<WebsocketEdge>
)
