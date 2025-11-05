package com.shopply.appEcommerce.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopply.appEcommerce.data.local.entities.UserRole
import com.shopply.appEcommerce.data.repository.UserRepository
import com.shopply.appEcommerce.domain.model.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * AuthViewModel - ViewModel de autenticación
 *
 * Gestiona:
 * - Login de usuarios
 * - Registro de usuarios (Comprador, Vendedor)
 * - Estados de autenticación
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    // Estado de autenticación
    var isLoggedIn by mutableStateOf(false)
        private set

    // Estados de UI
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // Campos del formulario de Login
    var loginEmail by mutableStateOf("")
    var loginPassword by mutableStateOf("")

    // Campos del formulario de Registro
    var registerEmail by mutableStateOf("")
    var registerPassword by mutableStateOf("")
    var registerName by mutableStateOf("")
    var registerPhone by mutableStateOf("")
    var registerConfirmPassword by mutableStateOf("")

    // Tipo de cuenta a registrar
    var isBusinessAccount by mutableStateOf(false)

    // NOTA: NO verificamos sesión automáticamente aquí
    // La verificación se hace en MainActivity para el auto-login
    // Aquí solo gestionamos login/registro manual

    // ===== LOGIN =====

    /**
     * Iniciar sesión
     */
    fun login() {
        if (!validateLoginInput()) return

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            when (val result = userRepository.login(loginEmail, loginPassword)) {
                is Result.Success -> {
                    isLoggedIn = true
                    _uiState.value = AuthUiState.Success("¡Bienvenido ${result.data.name}!")
                }
                is Result.Error -> {
                    _uiState.value = AuthUiState.Error(
                        result.exception.message ?: "Error al iniciar sesión"
                    )
                }
            }
        }
    }

    /**
     * Validar campos de login
     */
    private fun validateLoginInput(): Boolean {
        when {
            loginEmail.isBlank() -> {
                _uiState.value = AuthUiState.Error("Ingresa tu email")
                return false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(loginEmail).matches() -> {
                _uiState.value = AuthUiState.Error("Email inválido")
                return false
            }
            loginPassword.isBlank() -> {
                _uiState.value = AuthUiState.Error("Ingresa tu contraseña")
                return false
            }
        }
        return true
    }

    // ===== REGISTRO =====

    /**
     * Registrar nuevo usuario
     */
    fun register() {
        if (!validateRegisterInput()) return

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            // Determinar el rol según el tipo de cuenta
            val userRole = if (isBusinessAccount) UserRole.SELLER else UserRole.BUYER

            when (val result = userRepository.register(
                email = registerEmail,
                name = registerName,
                password = registerPassword,
                phone = registerPhone.takeIf { it.isNotBlank() },
                userRole = userRole
            )) {
                is Result.Success -> {
                    isLoggedIn = true
                    val accountType = if (isBusinessAccount) "vendedor" else "comprador"
                    _uiState.value = AuthUiState.Success(
                        "¡Cuenta de $accountType creada exitosamente!"
                    )
                }
                is Result.Error -> {
                    _uiState.value = AuthUiState.Error(
                        result.exception.message ?: "Error al crear cuenta"
                    )
                }
            }
        }
    }

    /**
     * Validar campos de registro
     */
    private fun validateRegisterInput(): Boolean {
        when {
            registerName.isBlank() -> {
                _uiState.value = AuthUiState.Error("Ingresa tu nombre completo")
                return false
            }
            registerEmail.isBlank() -> {
                _uiState.value = AuthUiState.Error("Ingresa tu email")
                return false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(registerEmail).matches() -> {
                _uiState.value = AuthUiState.Error("Email inválido")
                return false
            }
            registerPassword.isBlank() -> {
                _uiState.value = AuthUiState.Error("Ingresa una contraseña")
                return false
            }
            registerPassword.length < 6 -> {
                _uiState.value = AuthUiState.Error("La contraseña debe tener al menos 6 caracteres")
                return false
            }
            registerPassword != registerConfirmPassword -> {
                _uiState.value = AuthUiState.Error("Las contraseñas no coinciden")
                return false
            }
        }
        return true
    }

    // ===== LOGOUT =====

    /**
     * Cerrar sesión
     */
    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            isLoggedIn = false
            clearFields()
        }
    }

    // ===== UTILIDADES =====

    /**
     * Limpiar mensaje de error
     */
    fun clearError() {
        _uiState.value = AuthUiState.Idle
    }

    /**
     * Limpiar todos los campos
     */
    private fun clearFields() {
        loginEmail = ""
        loginPassword = ""
        registerEmail = ""
        registerPassword = ""
        registerName = ""
        registerPhone = ""
        registerConfirmPassword = ""
        isBusinessAccount = false
    }

    /**
     * Actualizar campo de login
     */
    fun updateLoginEmail(value: String) {
        loginEmail = value
    }

    fun updateLoginPassword(value: String) {
        loginPassword = value
    }

    /**
     * Actualizar campos de registro
     */
    fun updateRegisterEmail(value: String) {
        registerEmail = value
    }

    fun updateRegisterPassword(value: String) {
        registerPassword = value
    }

    fun updateRegisterName(value: String) {
        registerName = value
    }

    fun updateRegisterPhone(value: String) {
        registerPhone = value
    }

    fun updateRegisterConfirmPassword(value: String) {
        registerConfirmPassword = value
    }

    fun toggleBusinessAccount() {
        isBusinessAccount = !isBusinessAccount
    }
}

/**
 * Estados de UI para autenticación
 */
sealed class AuthUiState {
    data object Idle : AuthUiState()
    data object Loading : AuthUiState()
    data class Success(val message: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}