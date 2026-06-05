package com.nuecoo.feature.main.data.repository

import android.content.Context
import com.nuecoo.core.di.IoDispatcher
import com.nuecoo.core.di.LocalDataSources
import com.nuecoo.feature.main.data.datasource.cookie.CookieDataSource
import com.nuecoo.feature.main.data.mapper.toExternal
import com.nuecoo.feature.main.data.mapper.toLocal
import com.nuecoo.feature.main.domain.model.DailyCookieItemData
import com.nuecoo.feature.main.domain.repository.CookieRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CookieRepositoryImpl @Inject constructor(
    @LocalDataSources private val cookieDataSource: CookieDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context
) : CookieRepository {
    override fun getFlowDailyCookieData(): Flow<DailyCookieItemData?> {
        return cookieDataSource.observeLastDailyCookieData().map { data ->
            data?.toExternal()
        }
    }

    override fun getFlowCookieDataList(): Flow<List<DailyCookieItemData>> {
        return cookieDataSource.observeCookieList().map { list ->
            list.map { it.toExternal() }
        }
    }

    override suspend fun getCookieDataList() = withContext(ioDispatcher) {
        cookieDataSource.getCookieList().map { it.toExternal() }
    }

    override suspend fun upsertDailyCookieData(data: DailyCookieItemData)=
        withContext(ioDispatcher) {
            runCatching { cookieDataSource.upsertCookieData(data.toLocal()) }.isSuccess
        }
}