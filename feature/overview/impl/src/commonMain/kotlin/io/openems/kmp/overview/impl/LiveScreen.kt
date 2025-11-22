package io.openems.kmp.overview.impl

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import io.openems.kmp.edge.api.ChannelDataService
import org.koin.compose.koinInject

@Composable
fun LiveScreen(
    edgeId: String,
) {

    val channelDataService = koinInject<ChannelDataService>()
    val values by channelDataService.channelValues(
        edgeId, listOf(
            "_sum/ConsumptionActivePower",
            "_sum/EssActivePower",
            "_sum/GridActivePower",
            "_sum/ProductionActivePower",
        )
    ).collectAsState(emptyMap())

    Scaffold { pv ->
        Column(
            modifier = Modifier.padding(pv),
        ) {
            Text(edgeId)

            values.forEach {
                Text("${it.key}: ${it.value}")
            }

            IsometricHouseCanvas()
        }
    }
}