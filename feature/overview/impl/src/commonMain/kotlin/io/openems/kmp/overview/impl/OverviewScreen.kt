package io.openems.kmp.overview.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.openems.kmp.edge.api.EdgeMetadataService
import io.openems.kmp.edge.api.model.Edge
import org.koin.compose.koinInject

@Composable
fun OverviewScreen(
    onNavigateToEdge: (edgeId: String) -> Unit = {},
) {

    val edgeService = koinInject<EdgeMetadataService>()
    var edges by remember { mutableStateOf<List<Edge>>(emptyList()) }

    var searchString by remember { mutableStateOf("") }

    LaunchedEffect(searchString) {
        edges = edgeService.getEdges(
            searchString = searchString.ifEmpty { null }
        )
    }

    OverviewView(
        edges = edges,
        onFilterChanged = { searchString = it },
        onNavigateToEdge = onNavigateToEdge,
    )
}