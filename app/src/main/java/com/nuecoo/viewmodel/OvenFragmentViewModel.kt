package com.nuecoo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.nuecoo.core.di.DefaultDispatcher
import com.nuecoo.core.di.IoDispatcher
import com.nuecoo.core.di.MainDispatcher
import com.nuecoo.core.viewmodel.BaseViewModel
import com.nuecoo.domain.model.CookieItemData
import com.nuecoo.domain.model.CookieUIItemData
import com.nuecoo.domain.model.DailyCookieItemData
import com.nuecoo.domain.usecase.ObserveDailyCookieData
import com.nuecoo.domain.usecase.RemainTimeUseCase
import com.nuecoo.domain.usecase.UpdateOpenCookieData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class OvenFragmentViewModel @Inject constructor(
    private val observeDailyCookieData: ObserveDailyCookieData,
    remainTimeUseCase: RemainTimeUseCase,
    private val updateOpenCookieData: UpdateOpenCookieData,
    @MainDispatcher mainDispatcher: MainCoroutineDispatcher,
    @DefaultDispatcher defaultDispatcher: CoroutineDispatcher,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : BaseViewModel(mainDispatcher, defaultDispatcher, ioDispatcher) {
    private val cookieValueMap = MutableLiveData<Map<Int, List<String>>>()

    val dailyCookieData: LiveData<DailyCookieItemData> = cookieValueMap.switchMap { list ->
        observeDailyCookieData(list).asLiveData(viewModelScope.coroutineContext)
    }
    val remainTime = remainTimeUseCase().asLiveData(viewModelScope.coroutineContext)

    private val _selectCookieType = MutableLiveData<CookieUIItemData>()
    val selectCookieType: LiveData<CookieUIItemData> = _selectCookieType

    fun setSelectCookieType(data: CookieUIItemData) {
        _selectCookieType.value = data
    }

    fun updateOpenCookieData(type: Int, list: Map<Int, List<String>>) = onUiWork {
       val baseDailyCookieData = dailyCookieData.value?:return@onUiWork
        updateOpenCookieData(type, baseDailyCookieData, list)
    }

    fun getDailyCookieData(list: Map<Int, List<String>>) = onUiWork {
        cookieValueMap.value = list
    }
}