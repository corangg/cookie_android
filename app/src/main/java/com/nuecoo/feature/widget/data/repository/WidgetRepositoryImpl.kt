package com.nuecoo.feature.widget.data.repository

import com.nuecoo.core.di.IoDispatcher
import com.nuecoo.feature.widget.data.datasource.WidgetDataSource
import com.nuecoo.feature.widget.domain.repository.WidgetRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WidgetRepositoryImpl @Inject constructor(
    private val dataSource: WidgetDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : WidgetRepository {

    override fun getFlowWidgetEnabled(): Flow<Boolean> =
        dataSource.observeWidgetEnabled()

    override suspend fun saveWidgetEnabled(enabled: Boolean) =
        withContext(ioDispatcher) {
            dataSource.saveWidgetEnabled(enabled)
        }
}
