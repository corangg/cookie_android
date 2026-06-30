package com.nuecoo.feature.main.domain.usecase

import com.nuecoo.feature.main.domain.model.CollectionDisplayItem
import com.nuecoo.feature.main.domain.model.CookieType
import com.nuecoo.feature.main.domain.model.isSaved
import com.nuecoo.feature.main.domain.repository.CookieRepository
import javax.inject.Inject

class GetCollectionByTypeUseCase @Inject constructor(
    private val repository: CookieRepository
) {
    suspend operator fun invoke(type: Int, totalSize: Int): List<CollectionDisplayItem> {
        val savedEvents = repository.getAllEvents()
            .filter { it.type == type && it.isSaved && it.cookieNo != null }
            .distinctBy { it.cookieNo }
        val collectedMap = savedEvents.associate { it.cookieNo!! to it.claimDate }

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

class GetCollectionListUseCase @Inject constructor(
    private val repository: CookieRepository
) {
    suspend operator fun invoke(collectionSize: List<Pair<CookieType, Int>>): List<CollectionDisplayItem> {
        val savedEvents = repository.getAllEvents()
            .filter { it.isSaved && it.cookieNo != null }
            .distinctBy { it.type to it.cookieNo }
        val collectedMap = savedEvents.associate { (it.type to it.cookieNo!!) to it.claimDate }

        return collectionSize.flatMap { (cookieType, size) ->
            (1..size).map { no ->
                val date = collectedMap[cookieType.type to no]
                CollectionDisplayItem(
                    no = no,
                    type = cookieType.type,
                    isCollected = date != null,
                    date = date
                )
            }
        }
    }
}
