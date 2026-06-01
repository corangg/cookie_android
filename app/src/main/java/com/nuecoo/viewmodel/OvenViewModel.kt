package com.nuecoo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuecoo.core.di.IoDispatcher
import com.nuecoo.domain.model.CookieUIItemData
import com.nuecoo.domain.model.DailyCookieItemData
import com.nuecoo.domain.usecase.ObserveDailyCookieData
import com.nuecoo.domain.usecase.RemainTimeUseCase
import com.nuecoo.domain.usecase.UpdateOpenCookieData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OvenViewModel @Inject constructor(
    private val observeDailyCookieData: ObserveDailyCookieData,
    remainTimeUseCase: RemainTimeUseCase,
    private val updateOpenCookieData: UpdateOpenCookieData,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _cookieNameMap = MutableStateFlow<Map<Int, List<String>>>(emptyMap())

    val remainTime: StateFlow<String> = remainTimeUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "00 : 00 : 00")

    private val _dailyCookieData = MutableStateFlow<DailyCookieItemData?>(null)
    val dailyCookieData: StateFlow<DailyCookieItemData?> = _dailyCookieData

    private val _selectedCookie = MutableStateFlow<CookieUIItemData?>(null)
    val selectedCookie: StateFlow<CookieUIItemData?> = _selectedCookie

    fun initCookieData(nameMap: Map<Int, List<String>>) {
        _cookieNameMap.value = nameMap
        viewModelScope.launch(ioDispatcher) {
            observeDailyCookieData(nameMap).collect { data ->
                _dailyCookieData.value = data
            }
        }
    }

    fun selectCookie(data: CookieUIItemData) {
        _selectedCookie.value = data
    }

    fun clearSelectedCookie() {
        _selectedCookie.value = null
    }

    fun updateOpenCookieData(type: Int) {
        viewModelScope.launch(ioDispatcher) {
            val base = _dailyCookieData.value ?: return@launch
            updateOpenCookieData(type, base, _cookieNameMap.value)
        }
    }
}
