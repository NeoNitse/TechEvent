package com.sv.techevent.ui.theme

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ThemePreferences(private val dataStore: DataStore<Preferences>) {

    companion object {
        val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")
        val OFFLINE_MODE_KEY = booleanPreferencesKey("offline_mode")
    }

    val isDarkTheme: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[DARK_THEME_KEY] ?: false
    }

    val isOfflineMode: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[OFFLINE_MODE_KEY] ?: false
    }

    suspend fun toggleTheme(isDark: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_THEME_KEY] = isDark
        }
    }

    suspend fun toggleOfflineMode(isOffline: Boolean) {
        dataStore.edit { preferences ->
            preferences[OFFLINE_MODE_KEY] = isOffline
        }
    }
}