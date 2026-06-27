package com.nuecoo.feature.auth.data.datasource

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthDataSourceImpl @Inject constructor(
    private val functions: FirebaseFunctions,
    private val auth: FirebaseAuth
) : FirebaseAuthDataSource {
    override fun observeAuthState(): Flow<Boolean?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            trySend(user != null && !user.isAnonymous)
        }
        auth.addAuthStateListener(listener)
        awaitClose {
            auth.removeAuthStateListener(listener)
        }
    }
    override suspend fun trySignUp(email: String, password: String): AuthResult = auth.createUserWithEmailAndPassword(email, password).await()
    override suspend fun logIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun logOut() = auth.signOut()

    override suspend fun sendVerificationCode(phoneNumber: String) {
        ensureAuthenticated()
        functions
            .getHttpsCallable("sendVerificationCode")
            .call(mapOf("phoneNumber" to phoneNumber, "purpose" to "SIGNUP"))
            .await()
    }

    override suspend fun verifyCode(phoneNumber: String, code: String) {
        ensureAuthenticated()
        functions
            .getHttpsCallable("verifyPhoneForSignup")
            .call(mapOf("phoneNumber" to phoneNumber, "code" to code))
            .await()
    }

    private suspend fun ensureAuthenticated() {
        if (auth.currentUser == null) {
            auth.signInAnonymously().await()
        }
    }
}