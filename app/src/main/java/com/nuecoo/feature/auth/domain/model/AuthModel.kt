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

sealed interface VerificationResult {
    data object Success: VerificationResult
    data object AlreadyRegistered : VerificationResult   // 이미 가입된 휴대폰 번호 (서버: ALREADY_EXISTS)
    data object TooManyAttempts : VerificationResult     // 재발송 쿨다운 또는 인증 시도 횟수 초과 (서버: RESOURCE_EXHAUSTED)
    data object InvalidPhoneFormat : VerificationResult  // 전화번호 형식 오류 (서버: INVALID_ARGUMENT)
    data object SmsSendFailed : VerificationResult       // Solapi SMS 발송 실패 (서버: INTERNAL)
    data object CodeExpired : VerificationResult         // 인증번호 유효시간(3분) 만료 (서버: DEADLINE_EXCEEDED)
    data object CodeMismatch : VerificationResult        // 인증번호 불일치 (서버: PERMISSION_DENIED)
    data object RequestNotFound : VerificationResult     // 인증 요청 내역 없음, 발송 단계를 거치지 않고 검증 시도한 경우 (서버: NOT_FOUND)
    data object InvalidRequest : VerificationResult        // 잘못된 인증 요청 - 목적(purpose) 불일치 (서버: FAILED_PRECONDITION)
    data object Unauthenticated : VerificationResult     // 익명 인증 토큰이 없거나 무효 (서버: UNAUTHENTICATED)
    data object Unknown : VerificationResult             // 위 케이스에 해당하지 않는 예외 (네트워크 오류, 알 수 없는 서버 응답 등)
}

sealed interface FindEmailResult {
    data class Success(val maskedEmail: String) : FindEmailResult
    data class Failure(val reason: VerificationResult) : FindEmailResult
}