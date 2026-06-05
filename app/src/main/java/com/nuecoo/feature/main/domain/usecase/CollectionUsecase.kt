package com.nuecoo.feature.main.domain.usecase

import com.nuecoo.domain.model.CollectionDisplayItem
import com.nuecoo.feature.main.domain.model.DailyCookieItemData
import com.nuecoo.feature.main.domain.repository.CookieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCollectionByTypeUseCase @Inject constructor(
    private val repository: CookieRepository
) {
    suspend operator fun invoke(type: Int, totalSize: Int): List<CollectionDisplayItem> {
        val allData = repository.getCookieDataList()

        val collectedNos = allData.flatMap { daily ->
            daily.list
                .filter { it.type == type && it.isOpened == true && it.no != null }
                .map { Pair(it.no!!, daily.date) }
        }.distinctBy { it.first }

        val collectedMap = collectedNos.associate { it.first to it.second }

        return (1..totalSize).map { no ->
            CollectionDisplayItem(
                no = no,
                isCollected = collectedMap.containsKey(no),
                type = type,
                date = collectedMap[no]
            )
        }
    }
}

class ObserveCookieListUseCase @Inject constructor(
    private val repository: CookieRepository
) {
    operator fun invoke(): Flow<List<DailyCookieItemData>> = repository.getFlowCookieDataList()
}

class GetNewCookieNumberUseCase @Inject constructor(
    private val repository: CookieRepository
) {
    suspend operator fun invoke(type: Int, size: Int): Int {
        val list = repository.getCookieDataList()
        val openNoList =
            list.flatMap { it.list }.filter { it.type == type }.mapNotNull { it.no }.distinct()

        return randomExclude(size, openNoList) ?: 0

    }

    private fun randomExclude(cookieLength: Int, excludeList: List<Int>): Int? {
        val candidates = (1..cookieLength)
            .filterNot { it in excludeList }
        return candidates.randomOrNull()
    }
}