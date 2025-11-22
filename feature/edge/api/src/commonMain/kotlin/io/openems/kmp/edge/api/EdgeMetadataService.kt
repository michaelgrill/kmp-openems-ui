package io.openems.kmp.edge.api

import io.openems.kmp.edge.api.model.Edge

interface EdgeMetadataService {

    suspend fun getEdge(edgeId: String): Edge

    suspend fun getEdges(searchString: String? = null, page: Int = 0): List<Edge>

}