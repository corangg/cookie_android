package com.nuecoo.feature.auth.presentation.signup.viewmodel

import androidx.lifecycle.viewModelScope
import com.nuecoo.core.base.BaseViewModel
import com.nuecoo.core.di.DefaultDispatcher
import com.nuecoo.core.di.IoDispatcher
import com.nuecoo.core.di.MainDispatcher
import com.nuecoo.domain.model.EmailCheckResult
import com.nuecoo.domain.model.PwCheckResult
import com.nuecoo.domain.usecase.CheckEmailExistsUseCase
import com.nuecoo.domain.usecase.SendVerificationCodeUseCase
import com.nuecoo.domain.usecase.SignUpUseCase
import com.nuecoo.domain.usecase.VerifySmsCodeUseCase
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

    // Terms step
    private val _checkedPrivacy = MutableStateFlow(false)
    val checkedPrivacy: StateFlow<Boolean> = _checkedPrivacy

    private val _checkedTerms = MutableStateFlow(false)
    val checkedTerms: StateFlow<Boolean> = _checkedTerms

    val allTermsChecked: StateFlow<Boolean> = combine(_checkedPrivacy, _checkedTerms) { privacy, terms ->
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
    // Terms step end












    // Email step
    private var _email = ""
    private var _domain = ""
    private val _isEmailValid = MutableStateFlow(false)
    val isEmailValid: StateFlow<Boolean> = _isEmailValid

    fun setEmail(value: String) {
        _email = value
        _updateEmailValidity()
    }

    fun setDomain(value: String) {
        _domain = value
        _updateEmailValidity()
    }

    private fun _updateEmailValidity() {
        val full = "${_email.trim()}@${_domain.trim()}"
        val emailRegex = Regex("^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$")
        _isEmailValid.value = emailRegex.matches(full)
    }

    suspend fun checkEmailExists(): EmailCheckResult {
        return try {
            val exists = checkEmailExistsUseCase("${_email}@${_domain}")
            if (exists) EmailCheckResult.Duplicated else EmailCheckResult.Available
        } catch (e: Exception) {
            EmailCheckResult.Error
        } finally {

        }
    }

    // Password step
    private var _pw = ""
    private var _pwCheck = ""
    private val _isPwValid = MutableStateFlow(true)
    val isPwValid: StateFlow<Boolean> = _isPwValid
    private val _isPwCheckEnabled = MutableStateFlow(false)
    val isPwCheckEnabled: StateFlow<Boolean> = _isPwCheckEnabled

    fun setPw(value: String) {
        _pw = value
        val regex =
            Regex("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#\$%^&*(),.?\":{}|<>])[A-Za-z\\d!@#\$%^&*(),.?\":{}|<>]{8,20}$")
        _isPwValid.value = _pw.isEmpty() || regex.matches(_pw)
        _updatePwCheckEnabled()
    }

    fun setPwCheck(value: String) {
        _pwCheck = value
        _updatePwCheckEnabled()
    }

    private fun _updatePwCheckEnabled() {
        _isPwCheckEnabled.value = _pw.isNotEmpty() && _pwCheck.isNotEmpty()
    }

    fun checkPw(): PwCheckResult = when {
        !_isPwValid.value -> PwCheckResult.NotValid
        _pw == _pwCheck -> PwCheckResult.Accordance
        else -> PwCheckResult.NotAccordance
    }

    // Phone step
    private val _isCodeSent = MutableStateFlow(false)
    val isCodeSent: StateFlow<Boolean> = _isCodeSent
    private val _isPhoneOkEnabled = MutableStateFlow(false)
    val isPhoneOkEnabled: StateFlow<Boolean> = _isPhoneOkEnabled

    private var _phoneNumber = ""
    private var _verificationCode = ""
    private var _verificationId = ""

    fun setPhoneNumber(value: String) {
        _phoneNumber = value.trim()
    }

    fun setVerificationCode(value: String) {
        _verificationCode = value.trim()
        _isPhoneOkEnabled.value = _verificationCode.length >= 6
    }

    suspend fun sendVerificationCode(isResend: Boolean = false): Boolean {
        if (_phoneNumber.isEmpty()) return false
        return try {
            _verificationId = sendVerificationCodeUseCase(_phoneNumber)
            _isCodeSent.value = true
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun verifySmsCode(): Boolean {
        if (_verificationId.isEmpty()) return true // Skip if no verification ID (development)
        return verifySmsCodeUseCase(_verificationId, _verificationCode)
    }

    // Birth step
    private val _isGender = MutableStateFlow(true)
    val isGender: StateFlow<Boolean> = _isGender
    private val _birthDate = MutableStateFlow<String?>(null)
    val birthDate: StateFlow<String?> = _birthDate

    fun setGender(gender: Boolean) {
        _isGender.value = gender
    }

    fun setBirthDate(date: String) {
        _birthDate.value = date
    }

    suspend fun trySignUp(): Boolean {

        return try {
            signUpUseCase(
                email = "${_email}@${_domain}",
                password = _pw,
                verificationId = _verificationId,
                smsCode = _verificationCode,
                phone = _phoneNumber,
                gender = _isGender.value,
                birth = _birthDate.value ?: ""
            )
        } finally {

        }
    }
}