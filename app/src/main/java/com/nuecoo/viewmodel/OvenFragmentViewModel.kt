package com.nuecoo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.nuecoo.core.di.DefaultDispatcher
import com.nuecoo.core.di.IoDispatcher
import com.nuecoo.core.di.MainDispatcher
import com.nuecoo.core.viewmodel.BaseViewModel
import com.nuecoo.domain.CookieType
import com.nuecoo.domain.CookieUIItemData
import com.nuecoo.domain.usecase.ObserveDailyCookieData
import com.nuecoo.domain.usecase.RemainTimeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class OvenFragmentViewModel @Inject constructor(
    observeDailyCookieData: ObserveDailyCookieData,
    remainTimeUseCase: RemainTimeUseCase,
    @MainDispatcher mainDispatcher: MainCoroutineDispatcher,
    @DefaultDispatcher defaultDispatcher: CoroutineDispatcher,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : BaseViewModel(mainDispatcher, defaultDispatcher, ioDispatcher) {
    val dailyCookieData = observeDailyCookieData().map { it.list }.asLiveData(viewModelScope.coroutineContext)
    val remainTime = remainTimeUseCase().asLiveData(viewModelScope.coroutineContext)

    private val _selectCookieType = MutableLiveData<CookieUIItemData>()
    val selectCookieType: LiveData<CookieUIItemData> = _selectCookieType

    fun setSelectCookieType(data: CookieUIItemData) {
        _selectCookieType.value = data
    }

    fun updateOpenCookieData(type: Int) = onUiWork {

    }
}