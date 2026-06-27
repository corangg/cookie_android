package com.nuecoo.feature.auth.presentation.signup.viewmodel

import com.nuecoo.core.base.BaseViewModel
import com.nuecoo.core.di.DefaultDispatcher
import com.nuecoo.core.di.IoDispatcher
import com.nuecoo.core.di.MainDispatcher
import com.nuecoo.feature.auth.domain.model.FindEmailResult
import com.nuecoo.feature.auth.domain.model.VerificationResult
import com.nuecoo.feature.auth.domain.usecase.SendFindEmailPhoneCodeUseCase
import com.nuecoo.feature.auth.domain.usecase.VerifyFindEmailCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class FindEmailViewModel @Inject constructor(
    private val sendFindEmailPhoneCodeUseCase: SendFindEmailPhoneCodeUseCase,
    private val verifyFindEmailCodeUseCase: VerifyFindEmailCodeUseCase,
    @MainDispatcher mainDispatcher: MainCoroutineDispatcher,
    @DefaultDispatcher defaultDispatcher: CoroutineDispatcher,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : BaseViewModel(mainDispatcher, defaultDispatcher, ioDispatcher) {

    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone

    private val _code = MutableStateFlow("")
    val code: StateFlow<String> = _code

    private val _isCodeSent: MutableStateFlow<VerificationResult?> = MutableStateFlow(null)
    val isCodeSent: StateFlow<VerificationResult?> = _isCodeSent

    private val _isFindResult: MutableStateFlow<FindEmailResult?> = MutableStateFlow(null)
    val isFindResult: StateFlow<FindEmailResult?> = _isFindResult

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    fun setPhone(value: String) {
        _phone.value = value
    }

    fun setCode(value: String) {
        _code.value = value.trim()
    }

    fun sendCode() = onIoWork {
        _isCodeSent.value = sendFindEmailPhoneCodeUseCase(phone.value)
    }

    fun checkCode() = onIoWork {
        _isFindResult.value = verifyFindEmailCodeUseCase(phoneNumber = phone.value, code = code.value)
    }

}