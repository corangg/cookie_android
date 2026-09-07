package com.nuecoo.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface WidgetSettingsRepository {
    fun getFlowWidgetEnabled(): Flow<Boolean>
    suspend fun saveWidgetEnabled(enabled: Boolean)
}
