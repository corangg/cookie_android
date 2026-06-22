package com.nuecoo.feature.widget.domain.repository

import kotlinx.coroutines.flow.Flow

interface WidgetRepository {
    fun getFlowWidgetEnabled(): Flow<Boolean>
    suspend fun saveWidgetEnabled(enabled: Boolean)
}
