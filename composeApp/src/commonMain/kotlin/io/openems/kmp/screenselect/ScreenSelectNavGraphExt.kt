package io.openems.kmp.screenselect

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import io.openems.kmp.screen.AuthenticateScreen
import io.openems.kmp.screen.DemoScreen
import io.openems.kmp.screen.ScreenSelectScreen

fun NavGraphBuilder.screenSelectNav(navController: NavHostController) {
    composable<ScreenSelectScreen> {
        Scaffold { pv ->
            LazyColumn(
                modifier = Modifier.padding(pv)
            ) {
                items(
                    listOf(
                        Destination(
                            name = "demo",
                            screen = DemoScreen,
                        ),
                        Destination(
                            name = "authenticate",
                            screen = AuthenticateScreen,
                        ),
                    )
                ) { destination ->
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .clickable { navController.navigate(destination.screen) }
                    ) {
                        Text(destination.name)
                    }
                }
            }
        }
    }
}

data class Destination(
    val name: String,
    val screen: Any,
)