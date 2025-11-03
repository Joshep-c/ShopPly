package com.shopply.appEcommerce.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * UserPreferences - Gestión de preferencias del usuario
 *
 * Guarda:
 * - Preferencia de tema (claro/oscuro)
 * - Otras configuraciones de la app
 */
@Singleton
class UserPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")
    }

    /**
     * Obtener preferencia de tema oscuro
     * @return Flow<Boolean> - true si el tema oscuro está activado
     */
    val isDarkTheme: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[DARK_THEME_KEY] ?: false // Por defecto tema claro
    }

    /**
     * Guardar preferencia de tema
     * @param isDark true para tema oscuro, false para tema claro
     */
    suspend fun setDarkTheme(isDark: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_THEME_KEY] = isDark
        }
    }

    /**
     * Alternar entre tema claro y oscuro
     */
    suspend fun toggleTheme() {
        dataStore.edit { preferences ->
            val currentTheme = preferences[DARK_THEME_KEY] ?: false
            preferences[DARK_THEME_KEY] = !currentTheme
        }
    }
}


