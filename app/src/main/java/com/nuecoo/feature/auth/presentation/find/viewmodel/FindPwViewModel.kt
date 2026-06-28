package com.nuecoo.feature.auth.presentation.find.viewmodel

import android.util.Patterns
import com.nuecoo.core.base.BaseViewModel
import com.nuecoo.core.di.DefaultDispatcher
import com.nuecoo.core.di.IoDispatcher
import com.nuecoo.core.di.MainDispatcher
import com.nuecoo.feature.auth.domain.model.EmailCheckResult
import com.nuecoo.feature.auth.domain.model.FindEmailResult
import com.nuecoo.feature.auth.domain.model.PwCheckResult
import com.nuecoo.feature.auth.domain.model.VerificationResult
import com.nuecoo.feature.auth.domain.usecase.CheckEmailExistsUseCase
import com.nuecoo.feature.auth.domain.usecase.ResetPwUseCase
import com.nuecoo.feature.auth.domain.usecase.SendFindEmailPhoneCodeUseCase
import com.nuecoo.feature.auth.domain.usecase.SendResetPasswordPhoneCodeUseCase
import com.nuecoo.feature.auth.domain.usecase.VerifyResetPwCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class FindPwViewModel @Inject constructor(
    private val checkEmailExistsUseCase: CheckEmailExistsUseCase,
    private val sendResetPasswordPhoneCodeUseCase: SendResetPasswordPhoneCodeUseCase,
    private val verifyResetPasswordCodeUseCase: VerifyResetPwCodeUseCase,
    private val resetPwUseCase: ResetPwUseCase,
    @MainDispatcher mainDispatcher: MainCoroutineDispatcher,
    @DefaultDispatcher defaultDispatcher: CoroutineDispatcher,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : BaseViewModel(mainDispatcher, defaultDispatcher, ioDispatcher) {
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _isEmailResult = MutableStateFlow<EmailCheckResult?>(null)
    val isEmailResult: StateFlow<EmailCheckResult?> = _isEmailResult

    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone
    private val _code = MutableStateFlow("")
    val code: StateFlow<String> = _code

    private val _pw = MutableStateFlow("")
    val pw: StateFlow<String> = _pw
    private val _pwCheck = MutableStateFlow("")
    val pwCheck: StateFlow<String> = _pwCheck

    private val _isCodeSent: MutableStateFlow<VerificationResult?> = MutableStateFlow(null)
    val isCodeSent: StateFlow<VerificationResult?> = _isCodeSent

    private val _isFindResult: MutableStateFlow<VerificationResult?> = MutableStateFlow(null)
    val isFindResult: StateFlow<VerificationResult?> = _isFindResult

    private val _isPwResult = MutableStateFlow<PwCheckResult?>(null)
    val isPwResult: StateFlow<PwCheckResult?> = _isPwResult


    fun setEmail(email: String) {
        _email.value = email
    }

    fun setPhone(phone: String) {
        _phone.value = phone
    }

    fun setCode(code: String) {
        _code.value = code
    }

    fun setPw(pw: String) {
        _pw.value = pw
    }

    fun setPwCheck(pwCheck: String) {
        _pwCheck.value = pwCheck
    }

    fun checkEmail()= onIoWork {
        _isEmailResult.value = if (!Patterns.EMAIL_ADDRESS.matcher(_email.value).matches()) {
            EmailCheckResult.NotValid
        } else if (checkEmailExistsUseCase(email.value)) {
            EmailCheckResult.Duplicated
        } else {
            EmailCheckResult.Available
        }
    }

    fun sendCode() = onIoWork {
        _isCodeSent.value = sendResetPasswordPhoneCodeUseCase(phone.value)
    }

    fun checkCode() = onIoWork {
        _isFindResult.value = verifyResetPasswordCodeUseCase(phoneNumber = phone.value, code = code.value)
    }

    fun checkPw() = onIoWork{
        val passwordRegex = Regex("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,32}$")
        _isPwResult.value = when {
            !passwordRegex.matches(pw.value) || !passwordRegex.matches(pwCheck.value) -> {
                PwCheckResult.NotValid
            }

            pw.value != pwCheck.value -> {
                PwCheckResult.NotAccordance
            }

            else -> {
                if(resetPwUseCase(phoneNumber = phone.value, password = pw.value)== VerificationResult.Success){
                    PwCheckResult.Success
                }else{
                    PwCheckResult.Error
                }
            }
        }
    }

}