package org.example.project.notes

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "m7_settings")

class SettingsRepository(private val context: Context) {
    private val darkModeKey = booleanPreferencesKey("dark_mode")
    private val sortOrderKey = stringPreferencesKey("sort_order")

    val settingsFlow: Flow<UserSettings> = context.dataStore.data.map { pref ->
        val savedOrder = pref[sortOrderKey] ?: SortOrder.NEWEST.name
        val sortOrder = runCatching { SortOrder.valueOf(savedOrder) }.getOrDefault(SortOrder.NEWEST)

        UserSettings(
            darkMode = pref[darkModeKey] ?: false,
            sortOrder = sortOrder
        )
    }

    suspend fun setDarkMode(value: Boolean) {
        context.dataStore.edit { pref ->
            pref[darkModeKey] = value
        }
    }

    suspend fun setSortOrder(value: SortOrder) {
        context.dataStore.edit { pref ->
            pref[sortOrderKey] = value.name
        }
    }
}
