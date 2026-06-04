package com.nuecoo.domain.usecase

import com.nuecoo.core.util.getLocalTimeToString
import com.nuecoo.domain.model.CookieItemData
import com.nuecoo.domain.model.CookieType
import com.nuecoo.domain.model.DailyCookieItemData
import com.nuecoo.domain.repository.LocalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveDailyCookieData @Inject constructor(
    private val repository: LocalRepository
) {
    operator fun invoke(): Flow<DailyCookieItemData?> {
        return repository.getFlowCookieDataList().map { dataList ->
            val today = getLocalTimeToString().take(8)
            if (dataList.lastOrNull()?.date == today) {
                dataList.last()
            } else {
                repository.upsertDailyCookieData(
                    DailyCookieItemData(
                        date = today,
                        list = createCookieItemDataList()
                    )
                )
                null
            }
        }
    }

    private fun createCookieItemDataList(): List<CookieItemData> {
        return CookieType.entries
            .filter { it != CookieType.Unknown }
            .map { cookieType ->
                CookieItemData(
                    type = cookieType.type,
                    isOpened = false,
                    no = null
                )
            }
    }
}
class UpdateOpenCookieDataUseCase @Inject constructor(
    private val repository: LocalRepository
) {
    suspend operator fun invoke(
        type: Int,
        newNo: Int
    ) {
        val today = getLocalTimeToString().take(8)
        val todayData = repository.getCookieDataList().find { it.date == today } ?: return

        val newData = todayData.copy(
            list = todayData.list.map { item ->
                if (item.type == type) {
                    item.copy(no = newNo, isOpened = true)
                } else {
                    item
                }
            }
        )
        repository.upsertDailyCookieData(newData)
    }
}

class ObserveCookieListUseCase @Inject constructor(
    private val repository: LocalRepository
) {
    operator fun invoke(): Flow<List<DailyCookieItemData>> = repository.getFlowCookieDataList()
}

class GetNewCookieNumberUseCase @Inject constructor(
    private val repository: LocalRepository
) {
    suspend operator fun invoke(type: Int, size: Int): Int {
        val list = repository.getCookieDataList()
        val openNoList = list.flatMap { it.list }.filter { it.type == type }.mapNotNull { it.no }.distinct()

        return randomExclude(size, openNoList) ?: 0

    }

    private fun randomExclude(cookieLength: Int, excludeList: List<Int>): Int? {
        val candidates = (1..cookieLength)
            .filterNot { it in excludeList }
        return candidates.randomOrNull()
    }
}