package io.openems.kmp.user.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import io.openems.kmp.user.api.AuthenticationService
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun AuthenticationScreen(
    onAuthenticationSuccess: () -> Unit = {},
) {
    val authService = koinInject<AuthenticationService>()
    val scope = rememberCoroutineScope()

    var loading by remember { mutableStateOf(false) }

    AuthenticationView(
        loading = loading,
        onAuthenticate = { username, password ->
            scope.launch {
                loading = true
                try {
                    authService.authenticateWithPassword(username, password)
                    onAuthenticationSuccess()
                } finally {
                    loading = false
                }
            }
        }
    )
}