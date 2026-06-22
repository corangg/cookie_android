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
    operator fun invoke(): Flow<DailyCookieItemData?>{
        return cookieRepository.getFlowDailyCookieData()
    }
}

class InitDailyCookieUseCase @Inject constructor(
    private val cookieRepository: CookieRepository
) {
    suspend operator fun invoke(list: List<Pair<CookieType, Int>>) {
        val today = getLocalTimeToString().take(8)
        val dailyDataList = cookieRepository.getCookieDataList()

        if(dailyDataList.none { it.date == today }){
            cookieRepository.upsertDailyCookieData(
                DailyCookieItemData(
                    date = today,
                    list = createCookieItemDataList(list = list, dailyList = dailyDataList)
                )
            )
        }
    }

    private fun createCookieItemDataList(
        list: List<Pair<CookieType, Int>>,
        dailyList: List<DailyCookieItemData>
    ): List<CookieItemData> {
        return CookieType.entries
            .filter { it != CookieType.Unknown }
            .map { cookieType ->
                val cookieItemList = dailyList.flatMap { dailyData ->
                    dailyData.list.filter { it.type == cookieType.type }
                }
                val isFull = cookieItemList.size == list.find { it.first == cookieType }?.second

                CookieItemData(
                    type = cookieType.type,
                    isFull = isFull,
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

class ObserveNotOpenedCookies @Inject constructor(
    private val repository: CookieRepository
) {
    operator fun invoke(): Flow<Int> {
        return repository.getFlowDailyCookieData().map { data ->
            data?.list?.count { it.no == null } ?: 0
        }
    }
}

