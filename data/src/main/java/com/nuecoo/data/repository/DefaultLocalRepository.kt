package com.nuecoo.data.repository

import android.content.Context
import com.nuecoo.core.di.IoDispatcher
import com.nuecoo.core.di.LocalDataSources
import com.nuecoo.data.datasource.LocalCookieDataSource
import com.nuecoo.data.mapper.toExternal
import com.nuecoo.domain.DailyCookieItemData
import com.nuecoo.domain.LocalRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultLocalRepository @Inject constructor(
    @LocalDataSources private val localCookieDataSource: LocalCookieDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context
) : LocalRepository {
    override fun getDailyCookieData(): Flow<DailyCookieItemData> {
        return localCookieDataSource.observeLastDailyCookieData().map { data ->
            data.toExternal()
        }
    }
}