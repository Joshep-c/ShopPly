package com.shopply.appEcommerce.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopply.appEcommerce.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * MainViewModel - ViewModel principal de la aplicación
 *
 * Responsabilidades:
 * - Verificar estado de autenticación al iniciar la app
 * - Determinar la pantalla inicial apropiada
 * - Gestionar el estado de carga inicial (splash screen)
 *
 * Buenas prácticas:
 * - Separa la lógica de negocio de la UI (MainActivity)
 * - Usa StateFlow para estados reactivos
 * - Maneja estados de loading/success/error
 * - Sobrevive a cambios de configuración
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        checkAuthenticationStatus()
    }

    /**
     * Verifica el estado de autenticación al iniciar la app
     *
     * Flujo:
     * 1. Consulta UserRepository para verificar si hay sesión activa
     * 2. Si hay sesión -> Navegar a Home (auto-login)
     * 3. Si no hay sesión -> Mostrar pantalla de bienvenida
     */
    private fun checkAuthenticationStatus() {
        viewModelScope.launch {
            try {
                // Simular un pequeño delay para splash screen (opcional)
                // kotlinx.coroutines.delay(500)

                val isAuthenticated = userRepository.isLoggedIn()

                _uiState.value = if (isAuthenticated) {
                    MainUiState.Authenticated
                } else {
                    MainUiState.Unauthenticated
                }
            } catch (e: Exception) {
                // En caso de error, mostrar pantalla de login por seguridad
                _uiState.value = MainUiState.Unauthenticated
            }
        }
    }

    /**
     * Forzar re-verificación de autenticación
     * Útil después de logout o cambios en la sesión
     */
    fun refreshAuthenticationStatus() {
        _uiState.value = MainUiState.Loading
        checkAuthenticationStatus()
    }
}

/**
 * Estados de la UI principal
 */
sealed class MainUiState {
    /**
     * Estado inicial: Cargando (verificando sesión)
     * Mostrar splash screen
     */
    data object Loading : MainUiState()

    /**
     * Usuario autenticado: Tiene sesión activa
     * Navegar a HomeScreen
     */
    data object Authenticated : MainUiState()

    /**
     * Usuario no autenticado: Sin sesión
     * Mostrar AuthScreen (bienvenida)
     */
    data object Unauthenticated : MainUiState()
}

