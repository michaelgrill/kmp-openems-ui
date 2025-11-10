package io.openems.kmp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.koin.core.context.startKoin

fun main() = application {

    startKoin {
        modules(coreModules())
    }
    Window(
        onCloseRequest = ::exitApplication,
        title = "KMPOpenemsUI",
    ) {
        App()
    }
}