package com.nuecoo.viewmodel

import com.nuecoo.core.base.BaseViewModel
import com.nuecoo.core.di.DefaultDispatcher
import com.nuecoo.core.di.IoDispatcher
import com.nuecoo.core.di.MainDispatcher
import com.nuecoo.domain.model.CollectionDisplayItem
import com.nuecoo.feature.main.domain.model.CollectionSortType
import com.nuecoo.feature.main.domain.model.CookieType
import com.nuecoo.feature.main.domain.usecase.GetCollectionListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CollectionViewModel @Inject constructor(
    private val getCollectionListUseCase: GetCollectionListUseCase,
    @MainDispatcher mainDispatcher: MainCoroutineDispatcher,
    @DefaultDispatcher defaultDispatcher: CoroutineDispatcher,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : BaseViewModel(mainDispatcher, defaultDispatcher, ioDispatcher) {
    private val _selectedCookieType = MutableStateFlow<CookieType?>(null)
    val selectedCookieType: StateFlow<CookieType?> = _selectedCookieType

    private val allItems = MutableStateFlow<List<CollectionDisplayItem>>(emptyList())
    private val _items = MutableStateFlow<List<CollectionDisplayItem>>(emptyList())
    val items: StateFlow<List<CollectionDisplayItem>> = _items

    private val _selectedType = MutableStateFlow(0)
    val selectedType: StateFlow<Int> = _selectedType

    private val _sortType = MutableStateFlow(CollectionSortType.BY_NO)
    val sortType: StateFlow<CollectionSortType> = _sortType

    private val _showCollectedOnly = MutableStateFlow(false)
    val showCollectedOnly: StateFlow<Boolean> = _showCollectedOnly

    fun initCollectionList(list: List<Pair<CookieType, Int>>) = onIoWork {
        allItems.value = getCollectionListUseCase(list)
        _items.value = allItems.value
    }


    fun setSelectedCookieType(type: CookieType?) {
        _selectedCookieType.value = type

        val selectList = if (type == null) allItems.value else allItems.value.filter { it.type == type.type }
        _items.value = applySortAndFilter(selectList, _showCollectedOnly.value, _sortType.value)
    }

    fun setSortType(sort: CollectionSortType) {
        _sortType.value = sort
        _items.value = applySortAndFilter(_items.value, _showCollectedOnly.value, _sortType.value)
    }

    fun setShowCollectedOnly(value: Boolean) {
        _showCollectedOnly.value = value
        setSelectedCookieType(selectedCookieType.value)
    }

    private fun applySortAndFilter(
        list: List<CollectionDisplayItem>,
        showCollectedOnly: Boolean,
        sortType: CollectionSortType
    ): List<CollectionDisplayItem> {
        val filtered = if (showCollectedOnly) {
            list.filter { it.isCollected }
        } else {
            list
        }

        return when (sortType) {
            CollectionSortType.BY_NO -> filtered.sortedBy { it.no }
            CollectionSortType.BY_DATE ->
                filtered.sortedWith(
                    compareByDescending<CollectionDisplayItem> { it.date != null }
                        .thenBy { it.date }
                        .thenBy { it.no }
                )
        }
    }
}
