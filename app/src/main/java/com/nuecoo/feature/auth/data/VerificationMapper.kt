package com.nuecoo.feature.auth.data

import com.google.firebase.functions.FirebaseFunctionsException
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