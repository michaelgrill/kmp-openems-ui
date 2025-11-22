package io.openems.kmp.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface EdgeScreen : Screen {

    @Serializable
    data object Overview : EdgeScreen

    @Serializable
    data class Live(val edgeId: String) : EdgeScreen

}