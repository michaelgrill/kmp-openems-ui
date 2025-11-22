package io.openems.kmp.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface UserScreen {

    @Serializable
    data object Authentication : UserScreen

}