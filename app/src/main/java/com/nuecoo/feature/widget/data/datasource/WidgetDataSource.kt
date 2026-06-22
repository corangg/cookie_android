package com.nuecoo.feature.widget.data.datasource

import kotlinx.coroutines.flow.Flow

interface WidgetDataSource {
    fun observeWidgetEnabled(): Flow<Boolean>
    suspend fun saveWidgetEnabled(enabled: Boolean)
}
