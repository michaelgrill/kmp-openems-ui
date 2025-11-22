package io.openems.kmp.edge.api.model

import io.github.z4kn4fein.semver.Version
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class Edge(
    val id: String,
    val comment: String,
    val isOnline: Boolean,
    val lastMessage: Instant?,
    val productType: String?,
    val role: Role,
    val sumState: Level,
    val version: Version,
)