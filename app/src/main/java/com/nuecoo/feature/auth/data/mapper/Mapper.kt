package com.nuecoo.feature.auth.data.mapper

import com.google.firebase.functions.FirebaseFunctionsException
import com.nuecoo.core.data.model.remote.RemoteAuthModel
import com.nuecoo.feature.auth.domain.model.AuthModel
import com.nuecoo.feature.auth.domain.model.SignUpResult
import com.nuecoo.feature.auth.domain.model.VerificationResult

fun Throwable.toVerificationResult(): VerificationResult {
    if (this !is FirebaseFunctionsException) return VerificationResult.Unknown

    return when (this.code) {
        FirebaseFunctionsException.Code.ALREADY_EXISTS -> VerificationResult.AlreadyRegistered
        FirebaseFunctionsException.Code.RESOURCE_EXHAUSTED -> VerificationResult.TooManyAttempts
        FirebaseFunctionsException.Code.INVALID_ARGUMENT -> VerificationResult.InvalidPhoneFormat
        FirebaseFunctionsException.Code.INTERNAL -> VerificationResult.SmsSendFailed
        FirebaseFunctionsException.Code.DEADLINE_EXCEEDED -> VerificationResult.CodeExpired
        FirebaseFunctionsException.Code.PERMISSION_DENIED -> VerificationResult.CodeMismatch
        FirebaseFunctionsException.Code.NOT_FOUND -> VerificationResult.RequestNotFound
        FirebaseFunctionsException.Code.FAILED_PRECONDITION -> VerificationResult.InvalidRequest
        FirebaseFunctionsException.Code.UNAUTHENTICATED -> VerificationResult.Unauthenticated
        else -> VerificationResult.Unknown
    }
}


fun AuthModel.toRemote() = RemoteAuthModel(
    email = this.email,
    password = this.password,
    nickname = this.nickname,
    phone = this.phone,
    birth = this.birth,
    gender = this.gender,
)

fun Throwable.toSignUpResult(): SignUpResult {
    val e = this as? FirebaseFunctionsException ?: return SignUpResult.Failed
    val reason = (e.details as? Map<*, *>)?.get("reason") as? String

    return when (e.code) {
        FirebaseFunctionsException.Code.ALREADY_EXISTS -> SignUpResult.AlreadyExists
        FirebaseFunctionsException.Code.INVALID_ARGUMENT -> when (reason) {
            "WEAK_PASSWORD" -> SignUpResult.WeakPassword
            else -> SignUpResult.InvalidEmail
        }
        FirebaseFunctionsException.Code.INTERNAL -> when (reason) {
            "DB_SAVE_FAILED" -> SignUpResult.DbSaveFailed
            else -> SignUpResult.Failed
        }
        else -> SignUpResult.Failed
    }
}