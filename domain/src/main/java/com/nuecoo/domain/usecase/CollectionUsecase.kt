package com.nuecoo.domain.usecase

import com.nuecoo.domain.model.CollectionDisplayItem
import com.nuecoo.domain.repository.LocalRepository
import javax.inject.Inject

class GetCollectionByTypeUseCase @Inject constructor(
    private val repository: LocalRepository
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
