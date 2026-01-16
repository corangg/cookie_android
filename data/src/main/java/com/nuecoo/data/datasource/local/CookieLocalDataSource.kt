package com.nuecoo.data.datasource.local

import com.nuecoo.core.util.getLocalTimeToString
import com.nuecoo.data.datasource.LocalCookieDataSource
import com.nuecoo.data.datasource.local.room.CookieDao
import com.nuecoo.data.datasource.local.room.LocalCookieData
import com.nuecoo.data.datasource.local.room.LocalDailyCookieData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CookieLocalDataSource @Inject constructor(
    private val cookieDao: CookieDao
) : LocalCookieDataSource {
    override suspend fun upsertCookieData(entity: LocalDailyCookieData) = cookieDao.upsertCookieData(entity)
    override suspend fun upsertCookieListData(entity: List<LocalDailyCookieData>) = cookieDao.upsertCookieListData(entity)
    override suspend fun getLastDailyCookieData(): LocalDailyCookieData? = cookieDao.getLastDailyCookieData()
    override suspend fun getCookieList(): List<LocalDailyCookieData> = cookieDao.getCookieList()
    override fun observeLastDailyCookieData(): Flow<LocalDailyCookieData> =
        cookieDao.observeCookieList().map { list ->
            val today = getLocalTimeToString().substring(0, 8)
            list.find { it.date == today } ?: createDailyCookieData(today)
        }.distinctUntilChanged()
    override fun observeCookieList(): Flow<List<LocalDailyCookieData>> = cookieDao.observeCookieList()
    override suspend fun deleteCookieData() = cookieDao.deleteCookieData()

    private fun createDailyCookieData(dailyDate: String): LocalDailyCookieData {
        return LocalDailyCookieData(
            date = dailyDate,
            list = listOf(
                LocalCookieData(type = 0),
                LocalCookieData(type = 1),
                LocalCookieData(type = 2),
                LocalCookieData(type = 3),
            )
        )
    }
}