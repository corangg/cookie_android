package com.nuecoo.feature.auth.data.datasource

import com.google.firebase.auth.AuthResult

interface FirebaseAuthDataSource {
    suspend fun trySignUp(email: String, password: String): AuthResult
}