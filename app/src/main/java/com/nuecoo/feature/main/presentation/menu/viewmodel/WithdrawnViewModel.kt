package com.nuecoo.feature.main.presentation.menu.viewmodel

import com.nuecoo.core.base.BaseViewModel
import com.nuecoo.core.di.DefaultDispatcher
import com.nuecoo.core.di.IoDispatcher
import com.nuecoo.core.di.MainDispatcher
import com.nuecoo.feature.main.domain.usecase.LogOutUseCase
import com.nuecoo.feature.main.domain.usecase.WithdrawUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class WithdrawnViewModel @Inject constructor(
    private val withdrawUseCase: WithdrawUseCase,
    private val logoutUseCase: LogOutUseCase,
    @MainDispatcher mainDispatcher: MainCoroutineDispatcher,
    @DefaultDispatcher defaultDispatcher: CoroutineDispatcher,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : BaseViewModel(mainDispatcher, defaultDispatcher, ioDispatcher) {

    private val _isCheckedWithdrawn = MutableStateFlow(false)
    val isCheckedWithdrawn: StateFlow<Boolean> = _isCheckedWithdrawn

    private val _isSuccessWithdrawn = MutableStateFlow(false)
    val isSuccessWithdrawn: StateFlow<Boolean> = _isSuccessWithdrawn

    private val _isWithdrawFailed = MutableStateFlow(false)
    val isWithdrawFailed: StateFlow<Boolean> = _isWithdrawFailed

    fun setCheckedWithdrawn(checked: Boolean) {
        _isCheckedWithdrawn.value = checked
    }

    fun onWithdrawn() = onIoWork {
        _isWithdrawFailed.value = false
        if (withdrawUseCase()) {
            _isSuccessWithdrawn.value = true
        } else {
            _isWithdrawFailed.value = true
        }
    }

    fun consumeWithdrawFailure() {
        _isWithdrawFailed.value = false
    }

    fun moveLogin() = onIoWork{
        logoutUseCase()
    }
}