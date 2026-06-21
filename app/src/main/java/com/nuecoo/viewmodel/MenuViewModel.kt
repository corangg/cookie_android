package com.nuecoo.viewmodel

import com.nuecoo.core.base.BaseViewModel
import com.nuecoo.core.di.DefaultDispatcher
import com.nuecoo.core.di.IoDispatcher
import com.nuecoo.core.di.MainDispatcher
import com.nuecoo.domain.usecase.LogoutUseCase
import com.nuecoo.feature.main.domain.model.CookieType
import com.nuecoo.feature.main.domain.usecase.GetCollectionByTypeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class CollectionProgress(val type: Int, val collected: Int, val total: Int)

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val getCollectionByTypeUseCase: GetCollectionByTypeUseCase,
    private val logoutUseCase: LogoutUseCase,
    @MainDispatcher mainDispatcher: MainCoroutineDispatcher,
    @DefaultDispatcher defaultDispatcher: CoroutineDispatcher,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : BaseViewModel(mainDispatcher, defaultDispatcher, ioDispatcher) {

    private val _collectionProgress = MutableStateFlow<List<CollectionProgress>>(emptyList())
    val collectionProgress: StateFlow<List<CollectionProgress>> = _collectionProgress

    fun loadCollectionProgress(list: List<Pair<CookieType, Int>>) = onIoWork {
        val progressList = list.map { cookieData ->
            val items = getCollectionByTypeUseCase(cookieData.first.type, cookieData.second)
            val collected = items.count { it.isCollected }
            CollectionProgress(
                type = cookieData.first.type,
                collected = collected,
                total = cookieData.second
            )
        }

        _collectionProgress.value = progressList
    }

    suspend fun logout(): Boolean = logoutUseCase()
}
