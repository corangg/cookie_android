package com.nuecoo.feature.main.data.datasource.local

import com.nuecoo.core.data.database.dao.CookieTypeCountDao
import com.nuecoo.core.data.model.local.CookieTypeCountEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CookieTypeCountLocalDataSourceImpl @Inject constructor(
    private val cookieTypeCountDao: CookieTypeCountDao,
) : CookieTypeCountLocalDataSource {
    override suspend fun upsertCookieTypeCount(entity: List<CookieTypeCountEntity>) = cookieTypeCountDao.upsertCookieTypeCounts(entity)
    override suspend fun getCookieTypeCount() = cookieTypeCountDao.getCookieTypeCount()
    override suspend fun getMaxCount(type: Int) = cookieTypeCountDao.getMaxCount(type)
    override fun getCookieTypeCountFlow(): Flow<List<CookieTypeCountEntity>> {
        return cookieTypeCountDao.getCookieTypeCountFlow()
    }
}