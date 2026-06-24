package com.nuecoo.feature.auth.domain.model

sealed class LoginResult {
    object Success : LoginResult()
    object Empty : LoginResult()
    object Failed : LoginResult()
}

sealed class EmailCheckResult {
    object Available : EmailCheckResult()
    object Duplicated : EmailCheckResult()
    object NotValid : EmailCheckResult()
    object Error : EmailCheckResult()
}

sealed class PwCheckResult {
    object Success : PwCheckResult()
    object NotAccordance : PwCheckResult()
    object NotValid : PwCheckResult()
}

data class AuthModel(
    val email: String,
    val password: String,
    val nickname: String,
    val phone: String,
    val birth: String,
    val gender: Boolean,
)