package com.nuecoo.domain.usecase

import com.nuecoo.core.util.getLocalTimeToString
import com.nuecoo.domain.model.CookieItemData
import com.nuecoo.domain.model.DailyCookieItemData
import com.nuecoo.domain.repository.LocalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveDailyCookieData @Inject constructor(
    private val repository: LocalRepository
) {
    operator fun invoke(list: Map<Int, List<String>>): Flow<DailyCookieItemData> {
        return repository.getFlowCookieDataList().map { dataList ->
            val today = getLocalTimeToString().take(8)
            if (dataList.lastOrNull()?.date == today) {
                dataList.last()
            } else {
                DailyCookieItemData(
                    date = today,
                    list = createCookieItemDataList(list, dataList)
                )
            }

        }
    }

    private fun createCookieItemDataList(
        cookieList: Map<Int, List<String>>,
        dataList: List<DailyCookieItemData>
    ): List<CookieItemData> {
        return cookieList.map { (type, names) ->
            val openNoList = filterOpenedCookeList(type, dataList)
            val openCookie = hasUnopenedCookie(names.size, openNoList)

            CookieItemData(
                type = type,
                isOpened = if (openCookie) false else null
            )
        }
    }

    private fun filterOpenedCookeList(type: Int, cookieList: List<DailyCookieItemData>): List<Int> {
        return cookieList.mapNotNull { a ->
            a.list.find { it.type == type }
        }.mapNotNull { it.no }
    }

    private fun hasUnopenedCookie(cookieLength: Int, excludeList: List<Int>): Boolean{
        val excludeSet = excludeList.toSet()

        return (1..cookieLength).any { it !in excludeSet }
    }
}

class UpdateOpenCookieData @Inject constructor(
    private val repository: LocalRepository
) {
    suspend operator fun invoke(type: Int, dailyCookieData: DailyCookieItemData, list: Map<Int, List<String>>): Int? {
        val totalCookieData = repository.getCookieDataList()
        val cookieListSize = getCookieListSize(list,type)
        val openNoList = filterOpenedCookeList(type, totalCookieData)
        val openCookieNo = randomExclude(cookieListSize, openNoList) ?: return null
        val updateCookieData = newCookieData(openCookieNo, type, dailyCookieData)
        return if (repository.upsertDailyCookieData(updateCookieData)) {
            openCookieNo
        } else {
            null
        }
    }

    private fun getCookieListSize(list: Map<Int, List<String>>, type: Int): Int {
        return list[type]?.size ?: 0
    }

    private fun filterOpenedCookeList(type: Int, cookieList: List<DailyCookieItemData>): List<Int>{
        return cookieList.mapNotNull { a->
            a.list.find { it.type == type }
        }.mapNotNull { it.no }
    }

    private fun randomExclude(cookieLength:Int, excludeList: List<Int>): Int? {
        val candidates = (1..cookieLength)
            .filterNot { it in excludeList }
        return candidates.randomOrNull()
    }

    private fun newCookieData(
        no: Int,
        type: Int,
        baseData : DailyCookieItemData
    ): DailyCookieItemData {
        val time = getLocalTimeToString()

        val updatedList = baseData.list.map { cookie ->
            if (cookie.type == type) {
                cookie.copy(time = time, no = no, isOpened = true)
            } else {
                cookie
            }
        }

        return baseData.copy(list = updatedList)
    }
}