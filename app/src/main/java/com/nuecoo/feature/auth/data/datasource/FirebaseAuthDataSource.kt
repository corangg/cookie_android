package com.nuecoo.feature.auth.data.datasource

import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow

interface FirebaseAuthDataSource {
    fun observeAuthState(): Flow<Boolean?>
    suspend fun trySignUp(email: String, password: String): AuthResult
    suspend fun logIn(email: String, password: String)
    suspend fun logOut()
}