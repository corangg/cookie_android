package com.nuecoo.feature.main.domain.usecase

import com.nuecoo.core.util.getLocalTimeToString
import com.nuecoo.feature.main.domain.model.CookieItemData
import com.nuecoo.feature.main.domain.model.CookieType
import com.nuecoo.feature.main.domain.model.DailyCookieItemData
import com.nuecoo.feature.main.domain.repository.CookieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveDailyCookieData @Inject constructor(
    private val cookieRepository: CookieRepository
) {
    operator fun invoke(): Flow<DailyCookieItemData?> {
        return cookieRepository.getFlowCookieDataList().map { dataList ->
            val today = getLocalTimeToString().take(8)
            if (dataList.lastOrNull()?.date == today) {
                dataList.last()
            } else {
                cookieRepository.upsertDailyCookieData(
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
    private val cookieRepository: CookieRepository
) {
    suspend operator fun invoke(
        type: Int,
        newNo: Int
    ) {
        val today = getLocalTimeToString().take(8)
        val todayData = cookieRepository.getCookieDataList().find { it.date == today } ?: return

        val newData = todayData.copy(
            list = todayData.list.map { item ->
                if (item.type == type) {
                    item.copy(no = newNo, isOpened = true)
                } else {
                    item
                }
            }
        )
        cookieRepository.upsertDailyCookieData(newData)
    }
}

