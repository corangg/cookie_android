package com.nuecoo.feature.main.presentation.oven.viewmodel

import androidx.lifecycle.viewModelScope
import com.nuecoo.core.base.BaseViewModel
import com.nuecoo.core.di.DefaultDispatcher
import com.nuecoo.core.di.IoDispatcher
import com.nuecoo.core.di.MainDispatcher
import com.nuecoo.feature.main.domain.usecase.GetNewCookieNumberUseCase
import com.nuecoo.feature.main.domain.model.CookieUIItemData
import com.nuecoo.feature.main.domain.usecase.ObserveDailyCookieData
import com.nuecoo.feature.main.domain.usecase.RemainTimeUseCase
import com.nuecoo.feature.main.domain.usecase.UpdateOpenCookieDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class OvenViewModel @Inject constructor(
    observeDailyCookieData: ObserveDailyCookieData,
    remainTimeUseCase: RemainTimeUseCase,
    private val updateOpenCookieDataUseCase: UpdateOpenCookieDataUseCase,
    private val getNewCookieNumberUseCase: GetNewCookieNumberUseCase,
    @MainDispatcher mainDispatcher: MainCoroutineDispatcher,
    @DefaultDispatcher defaultDispatcher: CoroutineDispatcher,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : BaseViewModel(mainDispatcher, defaultDispatcher, ioDispatcher) {
    val remainTime = remainTimeUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        "00 : 00 : 00"
    )

    val dailyCookieData =
        observeDailyCookieData().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _selectedCookie = MutableStateFlow<CookieUIItemData?>(null)
    val selectedCookie: StateFlow<CookieUIItemData?> = _selectedCookie

    fun selectCookie(data: CookieUIItemData) {
        _selectedCookie.value = data
    }

    fun clearSelectedCookie() {
        _selectedCookie.value = null
    }

    fun updateOpenCookieData(type: Int, size: Int) = onUiWork {
        val newNo = getNewCookieNumberUseCase(type, size)
        updateOpenCookieDataUseCase(type = type, newNo = newNo)
        _selectedCookie.value = _selectedCookie.value?.copy(
            no = newNo,
            isOpened = true
        )
    }
}