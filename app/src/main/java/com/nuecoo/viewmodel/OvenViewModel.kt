package com.nuecoo.viewmodel

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.nuecoo.core.di.DefaultDispatcher
import com.nuecoo.core.di.IoDispatcher
import com.nuecoo.core.di.MainDispatcher
import com.nuecoo.core.viewmodel.BaseViewModel
import com.nuecoo.domain.model.CookieUIItemData
import com.nuecoo.domain.usecase.GetNewCookieNumberUseCase
import com.nuecoo.domain.usecase.ObserveDailyCookieData
import com.nuecoo.domain.usecase.RemainTimeUseCase
import com.nuecoo.domain.usecase.UpdateOpenCookieDataUseCase
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
    private val _cookieNameMap = MutableStateFlow<Map<Int, List<String>>>(emptyMap())

    val remainTime: StateFlow<String> = remainTimeUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "00 : 00 : 00")

    val dailyCookieData = observeDailyCookieData().asLiveData(viewModelScope.coroutineContext)

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
