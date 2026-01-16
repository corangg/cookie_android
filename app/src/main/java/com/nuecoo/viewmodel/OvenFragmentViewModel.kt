package com.nuecoo.viewmodel

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.nuecoo.core.di.DefaultDispatcher
import com.nuecoo.core.di.IoDispatcher
import com.nuecoo.core.di.MainDispatcher
import com.nuecoo.core.viewmodel.BaseViewModel
import com.nuecoo.domain.ObserveDailyCookieData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class OvenFragmentViewModel @Inject constructor(
    observeDailyCookieData: ObserveDailyCookieData,
    @MainDispatcher mainDispatcher: MainCoroutineDispatcher,
    @DefaultDispatcher defaultDispatcher: CoroutineDispatcher,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : BaseViewModel(mainDispatcher, defaultDispatcher, ioDispatcher) {
    val dailyCookieData = observeDailyCookieData().map { it.list }.asLiveData(viewModelScope.coroutineContext)
}