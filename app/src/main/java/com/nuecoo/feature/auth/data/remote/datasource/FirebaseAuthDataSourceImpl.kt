package com.nuecoo.feature.auth.data.remote.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.nuecoo.core.data.model.remote.RemoteAuthModel
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

    override suspend fun trySignUp(authModel: RemoteAuthModel) {
        ensureAuthenticated()
        functions
            .getHttpsCallable("signUp")
            .call(
                mapOf(
                    "email" to authModel.email,
                    "password" to authModel.password,
                    "phone" to authModel.phone,
                    "nickname" to authModel.nickname,
                    "birth" to authModel.birth,
                    "gender" to authModel.gender
                )
            )
            .await()
    }

    override suspend fun logIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun logOut() = auth.signOut()

    override suspend fun sendVerificationCode(phoneNumber: String, purpose: String) {
        ensureAuthenticated()
        functions
            .getHttpsCallable("sendVerificationCode")
            .call(mapOf("phoneNumber" to phoneNumber, "purpose" to purpose))
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

    override suspend fun verifyCodeAndFindEmail(phoneNumber: String, code: String): String {
        ensureAuthenticated()
        val result = functions
            .getHttpsCallable("verifyCodeAndFindEmail")
            .call(mapOf("phoneNumber" to phoneNumber, "code" to code))
            .await()

        @Suppress("UNCHECKED_CAST")
        val data = result.getData() as? Map<*, *>
            ?: throw IllegalStateException("응답 형식 오류: 데이터가 없습니다")
        return data["maskedEmail"] as? String
            ?: throw IllegalStateException("응답 형식 오류: maskedEmail이 없습니다")
    }

    override suspend fun verifyCodeForResetPassword(phoneNumber: String, code: String) {
        ensureAuthenticated()
        functions
            .getHttpsCallable("verifyCodeForResetPassword")
            .call(mapOf("phoneNumber" to phoneNumber, "code" to code))
            .await()
    }

    override suspend fun resetPassword(phoneNumber: String, newPassword: String) {
        ensureAuthenticated()
        functions
            .getHttpsCallable("resetPassword")
            .call(mapOf("phoneNumber" to phoneNumber, "newPassword" to newPassword))
            .await()
    }
}