package com.nuecoo.feature.main.presentation.menu.viewmodel

import com.nuecoo.core.base.BaseViewModel
import com.nuecoo.core.di.DefaultDispatcher
import com.nuecoo.core.di.IoDispatcher
import com.nuecoo.core.di.MainDispatcher
import com.nuecoo.feature.main.domain.usecase.LogOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class WithdrawnViewModel @Inject constructor(
    private val logoutUseCase: LogOutUseCase,
    @MainDispatcher mainDispatcher: MainCoroutineDispatcher,
    @DefaultDispatcher defaultDispatcher: CoroutineDispatcher,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : BaseViewModel(mainDispatcher, defaultDispatcher, ioDispatcher) {

    private val _isCheckedWithdrawn = MutableStateFlow(false)
    val isCheckedWithdrawn: StateFlow<Boolean> = _isCheckedWithdrawn

    private val _isSuccessWithdrawn = MutableStateFlow(false)
    val isSuccessWithdrawn: StateFlow<Boolean> = _isSuccessWithdrawn

    fun setCheckedWithdrawn(checked: Boolean) {
        _isCheckedWithdrawn.value = checked
    }

    fun onWithdrawn() {
        _isSuccessWithdrawn.value = true
    }

    fun moveLogin() = onIoWork{
        logoutUseCase()
    }
}