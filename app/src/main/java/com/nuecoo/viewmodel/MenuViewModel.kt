package com.nuecoo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuecoo.core.di.IoDispatcher
import com.nuecoo.domain.usecase.GetCollectionByTypeUseCase
import com.nuecoo.domain.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CollectionProgress(val collected: Int, val total: Int)

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val getCollectionByTypeUseCase: GetCollectionByTypeUseCase,
    private val logoutUseCase: LogoutUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _collectionProgress = MutableStateFlow<List<CollectionProgress>>(emptyList())
    val collectionProgress: StateFlow<List<CollectionProgress>> = _collectionProgress

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val cookieTotalSizes = listOf(6, 4, 3, 5)

    fun loadCollectionProgress() {
        viewModelScope.launch(ioDispatcher) {
            _isLoading.value = true
            val progressList = cookieTotalSizes.mapIndexed { index, total ->
                val items = getCollectionByTypeUseCase(index, total)
                val collected = items.count { it.isCollected }
                CollectionProgress(collected, total)
            }
            _collectionProgress.value = progressList
            _isLoading.value = false
        }
    }

    suspend fun logout(): Boolean = logoutUseCase()
}
