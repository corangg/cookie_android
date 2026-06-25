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

data class UserInfo(
    val email: String,
    val nickname: String,
    val phone: String,
    val birth: String,
    val gender: Boolean,
)

sealed interface SignUpResult {
    data object Success : SignUpResult//성공
    data object AlreadyExists : SignUpResult//이미 가입된 이메일
    data object InvalidEmail : SignUpResult//이메일 형식 오류
    data object WeakPassword : SignUpResult//비밀번호 안전 단계 낮음
    data object DbSaveFailed : SignUpResult//DB 저장 실패
    data object Failed : SignUpResult//기타실패
}