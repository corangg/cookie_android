package com.nuecoo.feature.main.presentation.oven.viewmodel

import androidx.lifecycle.viewModelScope
import com.nuecoo.core.base.BaseViewModel
import com.nuecoo.core.di.DefaultDispatcher
import com.nuecoo.core.di.IoDispatcher
import com.nuecoo.core.di.MainDispatcher
import com.nuecoo.feature.main.domain.model.CookieSlotUi
import com.nuecoo.feature.main.domain.model.CookieUIItemData
import com.nuecoo.feature.main.domain.repository.CookieRepository
import com.nuecoo.feature.main.domain.usecase.ObserveDailyCookieData
import com.nuecoo.feature.main.domain.usecase.ObserveNotOpenedCookies
import com.nuecoo.feature.main.domain.usecase.OpenCookieUseCase
import com.nuecoo.feature.main.domain.usecase.RemainTimeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import toUiItem
import javax.inject.Inject

@HiltViewModel
class OvenViewModel @Inject constructor(
    private val openCookieUseCase: OpenCookieUseCase,
    observeDailyCookieData: ObserveDailyCookieData,
    remainTimeUseCase: RemainTimeUseCase,
    observeNotOpenedCookies: ObserveNotOpenedCookies,
    @MainDispatcher mainDispatcher: MainCoroutineDispatcher,
    @DefaultDispatcher defaultDispatcher: CoroutineDispatcher,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : BaseViewModel(mainDispatcher, defaultDispatcher, ioDispatcher) {

    val remainTime = remainTimeUseCase().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), "00 : 00 : 00"
    )

    val dailyCookieSlots = observeDailyCookieData().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val notOpenedCookies = observeNotOpenedCookies().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0
    )

    private val _selectedCookieType = MutableStateFlow<Int?>(null)

    val selectedCookie: StateFlow<CookieUIItemData?> = combine(
        dailyCookieSlots, _selectedCookieType
    ) { slots, type ->
        if (type == null) return@combine null
        slots.find { it.type == type }?.toUiItem()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun selectCookie(type: Int) {
        _selectedCookieType.value = type
    }

    fun clearSelectedCookie() {
        _selectedCookieType.value = null
    }

    fun openCookie(type: Int) = onIoWork {
        openCookieUseCase(type)
    }
}
