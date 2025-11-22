package io.openems.kmp.data.websocket.feature.edge

import io.openems.kmp.data.websocket.core.WebsocketConnection
import io.openems.kmp.data.websocket.core.model.EdgeRpcRequest
import io.openems.kmp.data.websocket.core.model.GenericSuccessResult
import io.openems.kmp.data.websocket.core.model.JsonRpcRequest
import io.openems.kmp.data.websocket.core.request
import io.openems.kmp.data.websocket.feature.edge.model.GetEdgeParams
import io.openems.kmp.data.websocket.feature.edge.model.GetEdgesParams
import io.openems.kmp.data.websocket.feature.edge.model.GetEdgesResponse
import io.openems.kmp.data.websocket.feature.edge.model.SubscribeChannelsParams
import io.openems.kmp.data.websocket.feature.edge.model.SubscribeEdgesParams

suspend fun WebsocketConnection.getEdge(params: GetEdgeParams): GenericSuccessResult? =
    request(
        "getEdge", params
    )

suspend fun WebsocketConnection.getEdges(params: GetEdgesParams): GetEdgesResponse? =
    request(
        "getEdges", params
    )

suspend fun WebsocketConnection.subscribeEdges(params: SubscribeEdgesParams): GenericSuccessResult? =
    request(
        method = "subscribeEdges",
        request = params,
    )

suspend fun WebsocketConnection.subscribeChannels(
    edgeId: String,
    params: SubscribeChannelsParams
): GenericSuccessResult? = request(
    "edgeRpc", EdgeRpcRequest(
        edgeId = edgeId,
        payload = JsonRpcRequest(
            method = "subscribeChannels",
            params = params
        )
    )
)