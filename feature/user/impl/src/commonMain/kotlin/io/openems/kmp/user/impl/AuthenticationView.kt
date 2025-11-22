package io.openems.kmp.user.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun AuthenticationView(
    loading: Boolean,
    onAuthenticate: (username: String, password: String) -> Unit
) {
    Scaffold { pv ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(pv),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {

            var username by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                readOnly = loading,
                label = {
                    Text("Username")
                },
                singleLine = true,
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                readOnly = loading,
                visualTransformation = PasswordVisualTransformation(),
                label = {
                    Text("Password")
                },
                keyboardActions = KeyboardActions {
                    defaultKeyboardAction(ImeAction.Done)
                },
                singleLine = true,
            )
            Button(
                onClick = {
                    onAuthenticate(username, password)
                },
                enabled = !loading,
            ) {
                if (loading) {
                    CircularProgressIndicator(Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                }
                Text("Authenticate")
            }
        }
    }
}