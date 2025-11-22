package io.openems.kmp.user.di

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.openems.kmp.navigation.EdgeScreen
import io.openems.kmp.navigation.UserScreen
import io.openems.kmp.user.impl.AuthenticationScreen


fun NavGraphBuilder.userNav(navController: NavController) {
    composable<UserScreen.Authentication> {
        AuthenticationScreen(
            onAuthenticationSuccess = {
                navController.navigate(EdgeScreen.Overview)
            }
        )
    }
}