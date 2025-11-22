package io.openems.kmp.data.websocket.feature.edge

import io.openems.kmp.data.websocket.core.WebsocketConnection
import io.openems.kmp.data.websocket.feature.edge.model.GetEdgeParams
import io.openems.kmp.data.websocket.feature.edge.model.GetEdgesParams
import io.openems.kmp.data.websocket.feature.edge.model.toEdge
import io.openems.kmp.edge.api.EdgeMetadataService
import io.openems.kmp.edge.api.model.Edge

class WebsocketEdgeMetadataService(
    private val connection: WebsocketConnection,
) : EdgeMetadataService {

    override suspend fun getEdge(edgeId: String): Edge {
        connection.getEdge(GetEdgeParams(edgeId))
        TODO("Not yet implemented")
    }

    override suspend fun getEdges(searchString: String?, page: Int): List<Edge> {
        val result = connection.getEdges(
            GetEdgesParams(
                limit = 20,
                query = searchString,
                page = page
            )
        ) ?: return emptyList()

        return result.edges.map { it.toEdge() }
    }


}