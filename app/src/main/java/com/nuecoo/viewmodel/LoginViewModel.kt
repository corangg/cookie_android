package com.nuecoo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuecoo.domain.model.LoginResult
import com.nuecoo.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun setEmail(value: String) { _email.value = value.trim() }
    fun setPassword(value: String) { _password.value = value }

    suspend fun login(): LoginResult {
        if (_email.value.isEmpty() || _password.value.isEmpty()) return LoginResult.Empty
        _isLoading.value = true
        return try {
            val success = loginUseCase(_email.value, _password.value)
            if (success) LoginResult.Success else LoginResult.Failed
        } finally {
            _isLoading.value = false
        }
    }
}
