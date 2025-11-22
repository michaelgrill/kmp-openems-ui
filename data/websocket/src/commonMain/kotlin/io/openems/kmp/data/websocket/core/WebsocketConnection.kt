package io.openems.kmp.data.websocket.core

import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.receiveDeserialized
import io.ktor.client.plugins.websocket.sendSerialized
import io.ktor.client.plugins.websocket.webSocketSession
import io.openems.kmp.data.websocket.core.model.CurrentDataParams
import io.openems.kmp.data.websocket.core.model.EdgeRpcResponse
import io.openems.kmp.data.websocket.core.model.JsonRpcParams
import io.openems.kmp.data.websocket.core.model.JsonRpcRequest
import io.openems.kmp.data.websocket.core.model.JsonRpcResponse
import io.openems.kmp.data.websocket.di.json
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.serializer
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class WebsocketConnection(
    private val client: HttpClient,
) {

    private val scope: CoroutineScope = CoroutineScope(CoroutineName("Backend-Connection"))
    private val requests: MutableMap<String, CompletableDeferred<JsonRpcResponse>> = mutableMapOf()

    private val _currentData: MutableStateFlow<Map<String, Map<String, String>>> = MutableStateFlow(
        emptyMap()
    )
    val currentData = _currentData // TODO readonly

    private val session: Deferred<DefaultClientWebSocketSession> =
        scope.async(start = CoroutineStart.LAZY) {
            val session = client.webSocketSession("ws://localhost:8082")

            scope.launch {
                while (true) {
                    val response = session.receiveDeserialized<JsonRpcResponse>()
                    // TODO logging/event client.monitor.raise()
                    println("Response: $response")
                    if (response.id == null) {
                        when (response.method) {
                            "edgeRpc" -> {
                                val edgeRpcResponse =
                                    json.decodeFromJsonElement<EdgeRpcResponse>(response.params!!)
                                when (edgeRpcResponse.payload.method) {
                                    "currentData" -> {
                                        val currentData =
                                            json.decodeFromJsonElement<CurrentDataParams>(
                                                edgeRpcResponse.payload.params!!
                                            )

                                        _currentData.update { a ->
                                            val map = a.toMutableMap()
                                            map[edgeRpcResponse.edgeId] =
                                                currentData.mapValues { it.value.toString() }
                                            return@update map
                                        }
                                    }
                                }
                            }
                        }
                        continue
                    }

                    val deferred = requests.remove(response.id) ?: continue
                    deferred.complete(response)
                }
            }

            return@async session
        }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun <REQUEST : JsonRpcParams, RESPONSE> request(
        method: String,
        request: REQUEST,
        requestSerializer: SerializationStrategy<REQUEST>,
        deserializer: DeserializationStrategy<RESPONSE>
    ): RESPONSE? {
        val deferred = CompletableDeferred<JsonRpcResponse>()
        val id = Uuid.random().toString()

        requests[id] = deferred

        val r = JsonRpcRequest(
            id = id,
            method = method,
            params = request,
        )

        println("Request: ${json.encodeToString(r)}")
        session.await().sendSerialized(r)

        val response = deferred.await()

        if (response.result == null) {
            return null
        }

        return json.decodeFromJsonElement(deserializer, response.result)
    }

}

suspend inline fun <reified REQUEST : JsonRpcParams, reified RESPONSE> WebsocketConnection.request(
    method: String,
    request: REQUEST,
) = request(method, request, serializer<REQUEST>(), serializer<RESPONSE>())
