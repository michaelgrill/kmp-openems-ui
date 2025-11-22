package io.openems.kmp.overview.di

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import io.openems.kmp.navigation.EdgeScreen
import io.openems.kmp.overview.impl.LiveScreen
import io.openems.kmp.overview.impl.OverviewScreen

fun NavGraphBuilder.overviewNav(navController: NavController) {
    composable<EdgeScreen.Overview> {
        OverviewScreen(
            onNavigateToEdge = {
                navController.navigate(EdgeScreen.Live(it))
            }
        )
    }
    composable<EdgeScreen.Live> {
        val route: EdgeScreen.Live= it.toRoute()
        LiveScreen(route.edgeId)
    }
}