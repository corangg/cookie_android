package com.nuecoo.feature.auth.presentation.signup.viewmodel

import android.util.Patterns
import androidx.lifecycle.viewModelScope
import com.nuecoo.core.base.BaseViewModel
import com.nuecoo.core.di.DefaultDispatcher
import com.nuecoo.core.di.IoDispatcher
import com.nuecoo.core.di.MainDispatcher
import com.nuecoo.feature.auth.domain.usecase.CheckEmailExistsUseCase
import com.nuecoo.feature.auth.domain.usecase.SendVerificationCodeUseCase
import com.nuecoo.feature.auth.domain.usecase.SignUpUseCase
import com.nuecoo.feature.auth.domain.usecase.VerifySmsCodeUseCase
import com.nuecoo.feature.auth.domain.model.AuthModel
import com.nuecoo.feature.auth.domain.model.EmailCheckResult
import com.nuecoo.feature.auth.domain.model.PwCheckResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val checkEmailExistsUseCase: CheckEmailExistsUseCase,
    private val sendVerificationCodeUseCase: SendVerificationCodeUseCase,
    private val verifySmsCodeUseCase: VerifySmsCodeUseCase,
    private val signUpUseCase: SignUpUseCase,
    @MainDispatcher mainDispatcher: MainCoroutineDispatcher,
    @DefaultDispatcher defaultDispatcher: CoroutineDispatcher,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : BaseViewModel(mainDispatcher, defaultDispatcher, ioDispatcher) {
    private val _signUpStep = MutableStateFlow(0)
    val signUpStep: StateFlow<Int> = _signUpStep

    fun updateSignUpStep(step: Int) {
        _signUpStep.value = step
    }

    // Terms step
    private val _checkedPrivacy = MutableStateFlow(false)
    val checkedPrivacy: StateFlow<Boolean> = _checkedPrivacy

    private val _checkedTerms = MutableStateFlow(false)
    val checkedTerms: StateFlow<Boolean> = _checkedTerms

    val isAllTermsChecked: StateFlow<Boolean> =
        combine(checkedPrivacy, checkedTerms) { privacy, terms ->
            privacy && terms
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    fun setCheckedPrivacy(checked: Boolean) {
        _checkedPrivacy.value = checked
    }

    fun setCheckedTerms(checked: Boolean) {
        _checkedTerms.value = checked
    }

    fun setAllTermsChecked(checked: Boolean) {
        _checkedPrivacy.value = checked
        _checkedTerms.value = checked
    }

    //phone step
    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone

    private val _code = MutableStateFlow("")
    val code: StateFlow<String> = _code

    private val _isCodeSent = MutableStateFlow(false)
    val isCodeSent: StateFlow<Boolean> = _isCodeSent

    private val _isPhoneOk: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val isPhoneOk: StateFlow<Boolean?> = _isPhoneOk

    fun setPhone(value: String) {
        _phone.value = value.trim()
    }

    fun setCode(value: String) {
        _code.value = value.trim()
    }

    fun sendCode() = onIoWork {
        _isCodeSent.value = true//추후 실제 기능 구현 필요
    }

    fun checkCode() = onIoWork {
        _isPhoneOk.value = true//추후 실제 기능 구현 필요
    }

    // Email step
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _isEmailResult = MutableStateFlow<EmailCheckResult?>(null)

    val isEmailResult: StateFlow<EmailCheckResult?> = _isEmailResult

    fun setEmail(value: String) {
        _email.value = value
    }

    fun checkEmail() = onIoWork {
        _isEmailResult.value = if (!Patterns.EMAIL_ADDRESS.matcher(_email.value).matches()) {
            EmailCheckResult.NotValid
        } else if (checkEmailExistsUseCase(email.value)) {
            EmailCheckResult.Duplicated
        } else {
            EmailCheckResult.Available
        }
    }//중복 이메일인지 체크 기능 구현 필요

    // password step
    private val _pw = MutableStateFlow("")
    val pw: StateFlow<String> = _pw
    private val _pwCheck = MutableStateFlow("")
    val pwCheck: StateFlow<String> = _pwCheck

    private val _isPwResult = MutableStateFlow<PwCheckResult?>(null)
    val isPwResult: StateFlow<PwCheckResult?> = _isPwResult

    fun setPw(value: String) {
        _pw.value = value
    }

    fun setPwCheck(value: String) {
        _pwCheck.value = value
    }

    fun checkPw() {
        val passwordRegex =
            Regex("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,32}$")
        _isPwResult.value = when {
            !passwordRegex.matches(pw.value) || !passwordRegex.matches(pwCheck.value) -> {
                PwCheckResult.NotValid
            }

            pw.value != pwCheck.value -> {
                PwCheckResult.NotAccordance
            }

            else -> {
                PwCheckResult.Success
            }
        }
    }

    //nickname step
    private val _nickname = MutableStateFlow("")
    val nickname: StateFlow<String> = _nickname

    fun setNickname(value: String) {
        _nickname.value = value
    }

    //birth step
    private val _gender: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val gender: StateFlow<Boolean?> = _gender

    private val _year = MutableStateFlow(2000)
    val year: StateFlow<Int> = _year

    private val _month = MutableStateFlow(1)
    val month: StateFlow<Int> = _month

    private val _day = MutableStateFlow(1)
    val day: StateFlow<Int> = _day

    fun setGender(value: Boolean?) {
        _gender.value = value
    }

    fun setYear(value: Int) {
        _year.value = value
    }

    fun setMonth(value: Int) {
        _month.value = value
    }

    fun setDay(value: Int) {
        _day.value = value
    }

    //complete step

    private val _isSignupResult = MutableStateFlow(false)
    val isSignupResult: StateFlow<Boolean> = _isSignupResult

    fun trySignUp() = onIoWork {
        val authData = AuthModel(
            email = email.value,
            password = pw.value,
            nickname = nickname.value,
            phone = phone.value,
            birth = "${year.value}.${month.value}.${day.value}",
            gender = gender.value ?: false
        )
        _isSignupResult.value = signUpUseCase(authData)
    }
}