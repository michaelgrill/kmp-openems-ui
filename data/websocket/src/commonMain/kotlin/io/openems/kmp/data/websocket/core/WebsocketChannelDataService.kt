package io.openems.kmp.data.websocket.core

import io.openems.kmp.data.websocket.feature.edge.model.SubscribeChannelsParams
import io.openems.kmp.data.websocket.feature.edge.model.SubscribeEdgesParams
import io.openems.kmp.data.websocket.feature.edge.subscribeChannels
import io.openems.kmp.data.websocket.feature.edge.subscribeEdges
import io.openems.kmp.edge.api.ChannelDataService
import io.openems.kmp.edge.api.model.ChannelValue
import io.openems.kmp.edge.api.model.StringChannelValue
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.fetchAndIncrement

@OptIn(ExperimentalAtomicApi::class)
class WebsocketChannelDataService(
    private val connection: WebsocketConnection,
) : ChannelDataService {

    private val scope = CoroutineScope(CoroutineName("Websocket-Connection-ChannelDataService"))
    private val dispatcher = Dispatchers.Default.limitedParallelism(1)
    private val subscribeChannelsCounter = AtomicInt(1)
    private val subscribedChannels: MutableMap<String, MutableMap<String, Int>> = mutableMapOf()

    override fun channelValues(
        edgeId: String,
        channelAddresses: List<String>
    ): Flow<Map<String, ChannelValue>> {
        return channelFlow {
            subscribe(edgeId, channelAddresses)

            val job = launch {
                connection.currentData.collect { currentData ->
                    val edgeData = currentData[edgeId] ?: return@collect

                    val data = channelAddresses.associateWith { channel -> edgeData[channel] }
                        .mapValues { StringChannelValue(it.value ?: "") }

                    send(data)
                }
            }

            awaitClose {
                job.cancel()
                unsubscribe(edgeId, channelAddresses)
            }
        }
    }

    private fun subscribe(
        edgeId: String,
        channelAddresses: List<String>
    ) {
        scope.launch(dispatcher) {
            val initialEdgeSize = subscribedChannels.size
            val channels = subscribedChannels.getOrPut(edgeId) {
                mutableMapOf()
            }
            if (initialEdgeSize != subscribedChannels.size) {
                connection.subscribeEdges(SubscribeEdgesParams(subscribedChannels.keys.toList()))
            }

            val initialSize = channels.size
            channelAddresses.forEach { channel ->
                val counter = channels.getOrElse(channel) { 0 }
                channels[channel] = counter + 1
            }

            if (initialSize != channels.size) {
                connection.subscribeChannels(
                    edgeId, SubscribeChannelsParams(
                        count = subscribeChannelsCounter.fetchAndIncrement(),
                        channels = channels.keys.toList(),
                    )
                )
            }
        }
    }

    private fun unsubscribe(
        edgeId: String,
        channelAddresses: List<String>
    ) {
        scope.launch(dispatcher) {
            val channels = subscribedChannels[edgeId]
            if (channels == null) {
                println("Unexpected error. Edge not subscribed $edgeId")
                return@launch
            }

            val initialSize = channels.size
            channelAddresses.forEach { channel ->
                val counter = channels[channel] ?: 0
                if (counter == 1) {
                    channels.remove(channel)
                } else {
                    channels[channel] = counter - 1
                }
            }

            if (initialSize != channels.size) {
                connection.subscribeChannels(
                    edgeId, SubscribeChannelsParams(
                        count = subscribeChannelsCounter.fetchAndIncrement(),
                        channels = channels.keys.toList(),
                    )
                )
            }

            if (channels.isEmpty()) {
                subscribedChannels.remove(edgeId)
                connection.subscribeEdges(SubscribeEdgesParams(subscribedChannels.keys.toList()))
            }
        }
    }

}