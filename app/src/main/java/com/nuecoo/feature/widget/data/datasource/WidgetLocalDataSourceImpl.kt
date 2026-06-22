package com.nuecoo.feature.widget.data.datasource

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.widgetDataStore by preferencesDataStore(name = "widget_settings")

class WidgetLocalDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : WidgetDataSource {

    override fun observeWidgetEnabled(): Flow<Boolean> =
        context.widgetDataStore.data.map { prefs ->
            prefs[KEY_WIDGET_ENABLED] ?: true
        }

    override suspend fun saveWidgetEnabled(enabled: Boolean) {
        context.widgetDataStore.edit { prefs ->
            prefs[KEY_WIDGET_ENABLED] = enabled
        }
    }

    companion object {
        private val KEY_WIDGET_ENABLED = booleanPreferencesKey("widget_enabled")
    }
}
