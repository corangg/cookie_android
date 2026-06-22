package com.nuecoo.feature.main.presentation.oven.viewmodel

import androidx.lifecycle.viewModelScope
import com.nuecoo.core.base.BaseViewModel
import com.nuecoo.core.di.DefaultDispatcher
import com.nuecoo.core.di.IoDispatcher
import com.nuecoo.core.di.MainDispatcher
import com.nuecoo.feature.main.domain.model.CookieType
import com.nuecoo.feature.main.domain.model.CookieUIItemData
import com.nuecoo.feature.main.domain.usecase.GetNewCookieNumberUseCase
import com.nuecoo.feature.main.domain.usecase.InitDailyCookieUseCase
import com.nuecoo.feature.main.domain.usecase.ObserveDailyCookieData
import com.nuecoo.feature.main.domain.usecase.ObserveNotOpenedCookies
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
    observeNotOpenedCookies: ObserveNotOpenedCookies,
    private val initDailyCookieUseCase: InitDailyCookieUseCase,
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

    val dailyCookieData = observeDailyCookieData().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val notOpenedCookies = observeNotOpenedCookies().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _selectedCookie = MutableStateFlow<CookieUIItemData?>(null)
    val selectedCookie: StateFlow<CookieUIItemData?> = _selectedCookie

    fun initDailyCookie(list: List<Pair<CookieType, Int>>) = onIoWork {
        initDailyCookieUseCase(list)
    }

    fun selectCookie(data: CookieUIItemData) {
        _selectedCookie.value = data
    }

    fun clearSelectedCookie() {
        _selectedCookie.value = null
    }

    fun updateOpenCookieData(type: Int, size: Int) = onIoWork {
        val newNo = getNewCookieNumberUseCase(type, size)
        updateOpenCookieDataUseCase(type = type, newNo = newNo)
        _selectedCookie.value = _selectedCookie.value?.copy(
            no = newNo,
            isOpened = true
        )
    }
}