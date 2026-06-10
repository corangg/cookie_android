package com.nuecoo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuecoo.core.base.BaseViewModel
import com.nuecoo.core.di.DefaultDispatcher
import com.nuecoo.core.di.IoDispatcher
import com.nuecoo.core.di.MainDispatcher
import com.nuecoo.feature.main.domain.usecase.GetCollectionByTypeUseCase
import com.nuecoo.domain.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CollectionProgress(val collected: Int, val total: Int)

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
    private val cookieTotalSizes = listOf(6, 4, 3, 5)

    init {
        loadCollectionProgress()
    }

    fun loadCollectionProgress() =onIoWork{
        val progressList = cookieTotalSizes.mapIndexed { index, total ->
            val items = getCollectionByTypeUseCase(index, total)
            val collected = items.count { it.isCollected }
            CollectionProgress(collected, total)
        }
        _collectionProgress.value = progressList
    }

    suspend fun logout(): Boolean = logoutUseCase()
}
