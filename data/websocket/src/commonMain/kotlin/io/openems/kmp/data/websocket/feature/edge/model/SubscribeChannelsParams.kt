package io.openems.kmp.data.websocket.feature.edge.model

import io.openems.kmp.data.websocket.core.model.JsonRpcParams
import kotlinx.serialization.Serializable

@Serializable
data class SubscribeChannelsParams(
    val count: Int,
    val channels: List<String>,
) : JsonRpcParams()
