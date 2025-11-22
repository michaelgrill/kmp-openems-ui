package io.openems.kmp.user.api

interface AuthenticationService {

    suspend fun authenticateWithPassword(username: String, password: String)

}