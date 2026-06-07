package com.nuecoo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuecoo.core.di.IoDispatcher
import com.nuecoo.domain.model.CollectionDisplayItem
import com.nuecoo.feature.main.domain.model.CollectionSortType
import com.nuecoo.feature.main.domain.model.CookieType
import com.nuecoo.feature.main.domain.usecase.GetCollectionByTypeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionViewModel @Inject constructor(
    private val getCollectionByTypeUseCase: GetCollectionByTypeUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _selectedCookieType =
        MutableStateFlow<CookieType?>(null)

    val selectedCookieType: StateFlow<CookieType?> =
        _selectedCookieType




    private val _items = MutableStateFlow<List<CollectionDisplayItem>>(emptyList())
    val items: StateFlow<List<CollectionDisplayItem>> = _items

    private val _selectedType = MutableStateFlow(0)
    val selectedType: StateFlow<Int> = _selectedType

    private val _sortType = MutableStateFlow(CollectionSortType.BY_NO)
    val sortType: StateFlow<CollectionSortType> = _sortType

    private val _showCollectedOnly = MutableStateFlow(false)
    val showCollectedOnly: StateFlow<Boolean> = _showCollectedOnly

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val cookieTotalSizes = mapOf(0 to 6, 1 to 4, 2 to 3, 3 to 5)

    init {
        loadCollection(CookieType.Cheering.type)
    }


    fun setSelectedCookieType(type: CookieType?) {
        _selectedCookieType.value = type

        if (type == null) {
            // 전체 조회
            //loadAllCollection()
        } else {
            loadCollection(type.type)
        }
    }


    fun loadCollection(type: Int) {
        _selectedType.value = type
        viewModelScope.launch(ioDispatcher) {
            _isLoading.value = true
            val totalSize = cookieTotalSizes[type] ?: 4
            val result = getCollectionByTypeUseCase(type, totalSize)
            _items.value = applySortAndFilter(result)
            _isLoading.value = false
        }
    }

    fun setSortType(sort: CollectionSortType) {
        _sortType.value = sort
        _items.value = applySortAndFilter(_items.value)
    }

    fun setShowCollectedOnly(value: Boolean) {
        _showCollectedOnly.value = value
        _items.value = applySortAndFilter(_items.value)
    }

    private fun applySortAndFilter(list: List<CollectionDisplayItem>): List<CollectionDisplayItem> {
        val filtered = if (_showCollectedOnly.value) list.filter { it.isCollected } else list
        return when (_sortType.value) {
            CollectionSortType.BY_NO -> filtered.sortedBy { it.no }
            CollectionSortType.BY_DATE -> filtered.sortedByDescending { it.date }
        }
    }
}
