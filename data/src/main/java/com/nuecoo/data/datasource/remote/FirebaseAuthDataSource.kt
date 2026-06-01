package com.nuecoo.data.datasource.remote

import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseAuthDataSource @Inject constructor(
    private val auth: FirebaseAuth
) {
    fun isLoggedIn(): Boolean = auth.currentUser != null

    suspend fun login(email: String, password: String): Boolean = runCatching {
        auth.signInWithEmailAndPassword(email, password).await()
        true
    }.getOrElse { false }

    suspend fun logout(): Boolean = runCatching {
        auth.signOut()
        true
    }.getOrElse { false }

    suspend fun checkEmailExists(email: String): Boolean = runCatching {
        val result = auth.fetchSignInMethodsForEmail(email).await()
        result.signInMethods?.isNotEmpty() == true
    }.getOrElse { false }

    suspend fun sendVerificationCode(phoneNumber: String): String = suspendCoroutine { cont ->
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {}
            override fun onVerificationFailed(e: FirebaseException) {
                cont.resumeWithException(e)
            }
            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                cont.resume(verificationId)
            }
        }
        // Phone verification requires Activity context for reCAPTCHA; returns empty string as stub
        cont.resume("")
    }

    suspend fun verifySmsCode(verificationId: String, code: String): Boolean = runCatching {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        auth.signInWithCredential(credential).await()
        true
    }.getOrElse { false }

    suspend fun createAccount(email: String, password: String): Boolean = runCatching {
        auth.createUserWithEmailAndPassword(email, password).await()
        true
    }.getOrElse { false }
}
