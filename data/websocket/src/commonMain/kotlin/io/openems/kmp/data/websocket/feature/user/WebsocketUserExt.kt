package io.openems.kmp.data.websocket.feature.user

import io.openems.kmp.data.websocket.core.WebsocketConnection
import io.openems.kmp.data.websocket.core.request
import io.openems.kmp.data.websocket.feature.user.model.AuthenticateWithPasswordParams
import io.openems.kmp.data.websocket.feature.user.model.AuthenticateWithPasswordResponse

suspend fun WebsocketConnection.authenticateWithPassword(params: AuthenticateWithPasswordParams): AuthenticateWithPasswordResponse? =
    request(
        "authenticateWithPassword", params
    )