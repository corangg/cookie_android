package com.nuecoo.feature.main.data.datasource.cookie

import com.nuecoo.core.database.dao.CookieDao
import com.nuecoo.core.database.entity.LocalDailyCookieData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CookieLocalDataSourceImpl @Inject constructor(
    private val cookieDao: CookieDao
) : CookieDataSource {
    override suspend fun upsertCookieData(entity: LocalDailyCookieData) = cookieDao.upsertCookieData(entity)
    override suspend fun upsertCookieListData(entity: List<LocalDailyCookieData>) = cookieDao.upsertCookieListData(entity)
    override suspend fun getLastDailyCookieData(): LocalDailyCookieData? = cookieDao.getLastDailyCookieData()
    override suspend fun getCookieList(): List<LocalDailyCookieData> = cookieDao.getCookieList()
    override fun observeLastDailyCookieData(): Flow<LocalDailyCookieData?> = cookieDao.observeLastDailyCookieData()
    override fun observeCookieList(): Flow<List<LocalDailyCookieData>> = cookieDao.observeCookieList()
    override suspend fun deleteCookieData() = cookieDao.deleteCookieData()
}