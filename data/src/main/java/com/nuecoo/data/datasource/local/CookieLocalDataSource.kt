package com.nuecoo.data.datasource.local

import com.nuecoo.data.datasource.LocalCookieDataSource
import com.nuecoo.data.datasource.local.room.CookieDao
import com.nuecoo.data.datasource.local.room.LocalDailyCookieData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CookieLocalDataSource @Inject constructor(
    private val cookieDao: CookieDao
) : LocalCookieDataSource {
    override suspend fun upsertCookieData(entity: LocalDailyCookieData) = cookieDao.upsertCookieData(entity)
    override suspend fun upsertCookieListData(entity: List<LocalDailyCookieData>) = cookieDao.upsertCookieListData(entity)
    override suspend fun getLastDailyCookieData(): LocalDailyCookieData? = cookieDao.getLastDailyCookieData()
    override suspend fun getCookieList(): List<LocalDailyCookieData> = cookieDao.getCookieList()
    override fun observeLastDailyCookieData(): Flow<LocalDailyCookieData?> = cookieDao.observeLastDailyCookieData()
    override fun observeCookieList(): Flow<List<LocalDailyCookieData>> = cookieDao.observeCookieList()
    override suspend fun deleteCookieData() = cookieDao.deleteCookieData()
}