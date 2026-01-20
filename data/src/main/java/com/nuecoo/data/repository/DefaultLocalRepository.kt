package com.nuecoo.data.repository

import android.content.Context
import com.nuecoo.core.di.IoDispatcher
import com.nuecoo.core.di.LocalDataSources
import com.nuecoo.data.datasource.LocalCookieDataSource
import com.nuecoo.data.mapper.toExternal
import com.nuecoo.data.mapper.toLocal
import com.nuecoo.domain.model.DailyCookieItemData
import com.nuecoo.domain.repository.LocalRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultLocalRepository @Inject constructor(
    @LocalDataSources private val localCookieDataSource: LocalCookieDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context
) : LocalRepository {
    override fun getFlowDailyCookieData(): Flow<DailyCookieItemData?> {
        return localCookieDataSource.observeLastDailyCookieData().map { data ->
            data?.toExternal()
        }
    }

    override fun getFlowCookieDataList(): Flow<List<DailyCookieItemData>> {
        return localCookieDataSource.observeCookieList().map { list ->
            list.map { it.toExternal() }
        }
    }

    override suspend fun getCookieDataList() = withContext(ioDispatcher){
        localCookieDataSource.getCookieList().map { it.toExternal() }
    }

    override suspend fun upsertDailyCookieData(data: DailyCookieItemData)= withContext(ioDispatcher){
        runCatching { localCookieDataSource.upsertCookieData(data.toLocal()) }.isSuccess
    }
}