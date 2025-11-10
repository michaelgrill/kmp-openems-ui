package io.openems.kmp.authenticate

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.ktor.client.HttpClient
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.receiveDeserialized
import io.ktor.client.plugins.websocket.sendSerialized
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.openems.kmp.screen.AuthenticateScreen
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.serializer
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun NavGraphBuilder.authenticateNav() {
    composable<AuthenticateScreen> {
        Scaffold {
            val scope = rememberCoroutineScope()

            var connection by remember { mutableStateOf<Connection?>(null) }

            LaunchedEffect(Unit) {
                connection = createConnection()
            }

            var username by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            var response by remember { mutableStateOf<AuthenticateWithPasswordResponse?>(null) }

            Column {
                Row {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = {
                            Text("Username")
                        },
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        visualTransformation = PasswordVisualTransformation(),
                        label = {
                            Text("Password")
                        },
                    )
                }
                Button(
                    enabled = connection != null,
                    onClick = {
                        val c = connection ?: return@Button
                        scope.launch {
                            response = c.request(
                                "authenticateWithPassword", AuthenticateWithPasswordRequest(
                                    username = username,
                                    password = password,
                                )
                            )
                        }
                    },
                ) {
                    Text("Authenticate")
                }
                Button(
                    enabled = connection != null,
                    onClick = {
                        val c = connection ?: return@Button
                        scope.launch {
                            val subscriber = ChannelSubscriber(c)
                            subscriber.subscribeChannels(
                                "fems74135",
                                listOf("_sum/GridActivePower")
                            )
                        }
                    },
                ) {
                    Text("Subscribe Channels")
                }
                Text("Response: $response")

                val c = connection ?: return@Column
                val currentData by c.currentData.collectAsState()
                Text("CurrentData: $currentData")

            }
        }
    }
}

val json = Json {
    ignoreUnknownKeys = true
}

fun createHttpClient() = HttpClient {
    install(WebSockets) {
        // pingIntervalMillis = 20_000
        contentConverter = KotlinxWebsocketSerializationConverter(json)
    }
    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                println("HTTP Client $message")
            }
        }
        level = LogLevel.ALL
    }
}

suspend fun createConnection(): Connection {
    val client = createHttpClient()
    val session = client.webSocketSession("ws://localhost:8082")
    return Connection(client, session)
}

class Connection(
    private val client: HttpClient,
    private val session: DefaultClientWebSocketSession,
) {

    private val scope: CoroutineScope = CoroutineScope(CoroutineName("Backend-Connection"))
    private val requests: MutableMap<String, CompletableDeferred<JsonRpcResponse>> = mutableMapOf()

    private val _currentData: MutableStateFlow<JsonElement?> = MutableStateFlow(null)
    val currentData = _currentData // TODO readonly

    init {
        scope.launch {
            while (true) {
                val response = session.receiveDeserialized<JsonRpcResponse>()
                // TODO logging/event client.monitor.raise()
                println("Response: $response")
                if (response.id == null) {
                    _currentData.value = response.params
                    continue
                }

                val deferred = requests.remove(response.id) ?: continue
                deferred.complete(response)
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun <REQUEST, RESPONSE> request(
        method: String,
        request: REQUEST,
        requestSerializer: SerializationStrategy<REQUEST>,
        deserializer: DeserializationStrategy<RESPONSE>
    ): RESPONSE? {
        val deferred = CompletableDeferred<JsonRpcResponse>()
        val id = Uuid.random().toString()

        requests[id] = deferred

        session.sendSerialized(
            JsonRpcRequest(
                id = id,
                method = method,
                params = json.encodeToJsonElement(requestSerializer, request)
            )
        )

        val response = deferred.await()

        if (response.result == null) {
            return null
        }

        return json.decodeFromJsonElement(deserializer, response.result)
    }

}

suspend inline fun <reified REQUEST, reified RESPONSE> Connection.request(
    method: String,
    request: REQUEST,
) = request(method, request, serializer<REQUEST>(), serializer<RESPONSE>())


class ChannelSubscriber(
    private val connection: Connection,
) {

    @OptIn(ExperimentalUuidApi::class)
    suspend fun subscribeChannels(edgeId: String, channelAddresses: List<String>) {
        val getEdge: GenericSuccessResult? = connection.request(
            "getEdge", GetEdgeRequest(
                edgeId = edgeId,
            )
        )
        val resultEdgeSub: GenericSuccessResult? = connection.request(
            "subscribeEdges", SubscribeEdgesRequest(
                edges = listOf(edgeId),
            )
        )
        val resultChannelSub: GenericSuccessResult? = connection.request(
            "edgeRpc", EdgeRpcRequest(
                edgeId = edgeId,
                payload = JsonRpcRequest(
                    id = Uuid.NIL.toString(),
                    method = "subscribeChannels",
                    params = json.encodeToJsonElement(
                        SubscribeChannelsRequest(
                            count = 1,
                            channels = channelAddresses,
                        )
                    )
                )
            )
        )
    }

}

@Serializable
data class JsonRpcRequest(
    val jsonrpc: String = "2.0",
    val id: String? = null,
    val method: String,
    val params: JsonElement,
)

@Serializable
data class JsonRpcResponse(
    val jsonrpc: String,
    val id: String? = null,
    val result: JsonElement? = null,
    val error: JsonElement? = null,

    // notification
    val method: String? = null,
    val params: JsonElement? = null,
)

@Serializable
object GenericSuccessResult

@Serializable
data class EdgeRpcRequest(
    val edgeId: String,
    val payload: JsonRpcRequest,
)

@Serializable
data class GetEdgeRequest(
    val edgeId: String,
)

@Serializable
data class SubscribeChannelsRequest(
    val count: Int,
    val channels: List<String>,
)

@Serializable
data class SubscribeEdgesRequest(
    val edges: List<String>,
)

@Serializable
data class AuthenticateWithPasswordRequest(
    val username: String,
    val password: String,
)

@Serializable
data class AuthenticateWithPasswordResponse(
    val token: String,
    val user: User,
    // val edges: List<Edge>, deprecated
)

@Serializable
data class User(
    val id: String,
    val name: String,
    val language: String,
    val hasMultipleEdges: Boolean,
    // val settings: Map<String, String>,
    val globalRole: String,
)