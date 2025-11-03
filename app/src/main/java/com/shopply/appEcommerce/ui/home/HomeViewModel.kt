package com.shopply.appEcommerce.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopply.appEcommerce.data.preferences.UserPreferences
import com.shopply.appEcommerce.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * HomeViewModel - ViewModel para la pantalla principal
 *
 * Gestiona:
 * - Preferencias de tema
 * - Información del usuario actual
 * - Navegación y opciones principales
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val userRepository: UserRepository
) : ViewModel() {

    /**
     * Estado del tema oscuro
     * Observa los cambios en las preferencias
     */
    val isDarkTheme: StateFlow<Boolean> = userPreferences.isDarkTheme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    /**
     * Cambiar tema
     * @param isDark true para tema oscuro, false para tema claro
     */
    fun setTheme(isDark: Boolean) {
        viewModelScope.launch {
            userPreferences.setDarkTheme(isDark)
        }
    }

    /**
     * Alternar entre tema claro y oscuro
     */
    fun toggleTheme() {
        viewModelScope.launch {
            userPreferences.toggleTheme()
        }
    }

    /**
     * Cerrar sesión
     */
    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }
}

