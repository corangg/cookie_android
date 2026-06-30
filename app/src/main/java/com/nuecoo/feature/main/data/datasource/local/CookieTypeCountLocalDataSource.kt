package com.nuecoo.feature.main.data.datasource.local

import com.nuecoo.core.data.model.local.CookieTypeCountEntity
import kotlinx.coroutines.flow.Flow

interface CookieTypeCountLocalDataSource {
    suspend fun getCookieTypeCount(): List<CookieTypeCountEntity>
    suspend fun upsertCookieTypeCount(entity: List<CookieTypeCountEntity>)
    suspend fun getMaxCount(type: Int): Int?
    fun getCookieTypeCountFlow(): Flow<List<CookieTypeCountEntity>>
}