package io.openems.kmp.data.websocket.feature.user

import io.openems.kmp.data.websocket.core.WebsocketConnection
import io.openems.kmp.data.websocket.feature.user.model.AuthenticateWithPasswordParams
import io.openems.kmp.user.api.AuthenticationService

class WebsocketAuthenticationService(
    private val connection: WebsocketConnection,
) : AuthenticationService {

    override suspend fun authenticateWithPassword(username: String, password: String) {
        connection.authenticateWithPassword(
            AuthenticateWithPasswordParams(
                username = username,
                password = password,
            )
        )
    }

}