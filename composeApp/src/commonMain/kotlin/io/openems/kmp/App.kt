package io.openems.kmp

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.plugins.websocket.wss
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.openems.kmp.authenticate.authenticateNav
import io.openems.kmp.demo.demoNav
import io.openems.kmp.screen.ScreenSelectScreen
import io.openems.kmp.screenselect.screenSelectNav
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.core.module.Module

@Composable
@Preview
fun App() {
    MaterialTheme(
        colorScheme = colorScheme(),
    ) {

        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = ScreenSelectScreen) {
            screenSelectNav(navController)
            demoNav()
            authenticateNav()
        }

    }
}

@Composable
fun colorScheme(): ColorScheme {
    val isDarkTheme = isSystemInDarkTheme()
    val lightColorScheme = lightColorScheme(primary = Color(0xFF1EB980))
    val darkColorScheme = darkColorScheme(primary = Color(0xFF66ffc7))

    return when {
        isDarkTheme -> darkColorScheme
        else -> lightColorScheme
    }
}

fun coreModules(): List<Module> = listOf(

)
