package io.openems.kmp.data.websocket.feature.edge.model

import io.github.z4kn4fein.semver.Version
import io.ktor.util.toUpperCasePreservingASCIIRules
import io.openems.kmp.edge.api.model.Edge
import io.openems.kmp.edge.api.model.Level
import io.openems.kmp.edge.api.model.Role
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
@OptIn(ExperimentalTime::class)
data class WebsocketEdge(
    val id: String,
    val comment: String,
    val isOnline: Boolean,
    @Contextual
    val lastMessage: Instant? = null,
    val productType: String? = null,
    val role: String,
    val sumState: String,
    val version: Version,
)

@OptIn(ExperimentalTime::class)
fun WebsocketEdge.toEdge() = Edge(
    id = id,
    comment = comment,
    isOnline = isOnline,
    lastMessage = lastMessage,
    productType = productType,
    role = Role.valueOf(role.toUpperCasePreservingASCIIRules()),
    sumState = Level.valueOf(sumState.toUpperCasePreservingASCIIRules()),
    version = version,
)