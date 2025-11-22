package io.openems.kmp.data.websocket.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.openems.kmp.data.websocket.core.WebsocketChannelDataService
import io.openems.kmp.data.websocket.core.WebsocketConnection
import io.openems.kmp.data.websocket.core.model.EdgeRpcRequest
import io.openems.kmp.data.websocket.core.model.JsonRpcParams
import io.openems.kmp.data.websocket.feature.edge.WebsocketEdgeMetadataService
import io.openems.kmp.data.websocket.feature.edge.model.GetEdgeParams
import io.openems.kmp.data.websocket.feature.edge.model.GetEdgesParams
import io.openems.kmp.data.websocket.feature.edge.model.SubscribeChannelsParams
import io.openems.kmp.data.websocket.feature.edge.model.SubscribeEdgesParams
import io.openems.kmp.data.websocket.feature.user.WebsocketAuthenticationService
import io.openems.kmp.data.websocket.feature.user.model.AuthenticateWithPasswordParams
import io.openems.kmp.edge.api.ChannelDataService
import io.openems.kmp.edge.api.EdgeMetadataService
import io.openems.kmp.user.api.AuthenticationService
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.ClassDiscriminatorMode
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun websocketModule() = module {
    singleOf(::createHttpClient)
    singleOf(::WebsocketConnection)
    singleOf(::WebsocketChannelDataService) bind ChannelDataService::class
    factoryOf(::WebsocketAuthenticationService) bind AuthenticationService::class
    factoryOf(::WebsocketEdgeMetadataService) bind EdgeMetadataService::class
}

@OptIn(ExperimentalSerializationApi::class)
internal val json = Json {
    ignoreUnknownKeys = true
    classDiscriminatorMode = ClassDiscriminatorMode.NONE
    serializersModule = SerializersModule {
        polymorphic(JsonRpcParams::class) {
            subclass(EdgeRpcRequest::class)
            subclass(GetEdgeParams::class)
            subclass(GetEdgesParams::class)
            subclass(SubscribeChannelsParams::class)
            subclass(SubscribeEdgesParams::class)
            subclass(AuthenticateWithPasswordParams::class)
        }
    }
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