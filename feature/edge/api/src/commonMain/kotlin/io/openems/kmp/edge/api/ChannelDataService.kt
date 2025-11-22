package io.openems.kmp.edge.api

import io.openems.kmp.edge.api.model.ChannelValue
import kotlinx.coroutines.flow.Flow

interface ChannelDataService {

    fun channelValues(
        edgeId: String,
        channelAddresses: List<String>
    ): Flow<Map<String, ChannelValue>>

}