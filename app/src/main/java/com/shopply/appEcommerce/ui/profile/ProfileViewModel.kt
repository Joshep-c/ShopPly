package com.shopply.appEcommerce.ui.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopply.appEcommerce.data.local.entities.User
import com.shopply.appEcommerce.data.repository.UserRepository
import com.shopply.appEcommerce.domain.model.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ProfileViewModel - ViewModel para la pantalla de perfil
 *
 * Responsabilidades:
 * - Cargar información del usuario actual
 * - Editar perfil (nombre, email, teléfono)
 * - Cambiar contraseña
 * - Cerrar sesión
 * - Eliminar cuenta
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    // Estados de edición
    var isEditMode by mutableStateOf(false)
        private set

    var editName by mutableStateOf("")
        private set

    var editEmail by mutableStateOf("")
        private set

    var editPhone by mutableStateOf("")
        private set

    // Cambio de contraseña
    var showChangePasswordDialog by mutableStateOf(false)
        private set

    var currentPassword by mutableStateOf("")
        private set

    var newPassword by mutableStateOf("")
        private set

    var confirmNewPassword by mutableStateOf("")
        private set

    // Eliminar cuenta
    var showDeleteAccountDialog by mutableStateOf(false)
        private set

    var deleteAccountPassword by mutableStateOf("")
        private set

    private var currentUser: User? = null

    init {
        loadProfile()
    }

    /**
     * Cargar perfil del usuario actual
     */
    private fun loadProfile() {
        viewModelScope.launch {
            try {
                _uiState.value = ProfileUiState.Loading

                userRepository.getCurrentUser().collect { user ->
                    if (user != null) {
                        currentUser = user
                        editName = user.name
                        editEmail = user.email
                        editPhone = user.phone ?: ""

                        _uiState.value = ProfileUiState.Success(
                            user = user,
                            message = null
                        )
                    } else {
                        _uiState.value = ProfileUiState.Error("No se pudo cargar el perfil")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(
                    e.message ?: "Error al cargar perfil"
                )
            }
        }
    }

    /**
     * Activar modo edición
     */
    fun enableEditMode() {
        isEditMode = true
    }

    /**
     * Cancelar edición
     */
    fun cancelEdit() {
        isEditMode = false
        currentUser?.let { user ->
            editName = user.name
            editEmail = user.email
            editPhone = user.phone ?: ""
        }
    }

    /**
     * Actualizar campos de edición
     */
    fun updateEditName(value: String) {
        editName = value
    }

    fun updateEditEmail(value: String) {
        editEmail = value
    }

    fun updateEditPhone(value: String) {
        editPhone = value
    }

    /**
     * Guardar cambios del perfil
     */
    fun saveProfile() {
        viewModelScope.launch {
            try {
                val user = currentUser ?: return@launch

                // Validaciones
                if (editName.isBlank()) {
                    showMessage("El nombre no puede estar vacío")
                    return@launch
                }

                if (editEmail.isBlank()) {
                    showMessage("El email no puede estar vacío")
                    return@launch
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(editEmail).matches()) {
                    showMessage("Email inválido")
                    return@launch
                }

                // Actualizar usuario
                val updatedUser = user.copy(
                    name = editName.trim(),
                    email = editEmail.trim().lowercase(),
                    phone = editPhone.trim().ifBlank { null }
                )

                when (val result = userRepository.updateUser(updatedUser)) {
                    is Result.Success -> {
                        isEditMode = false
                        showMessage("Perfil actualizado correctamente")
                    }
                    is Result.Error -> {
                        showMessage(result.exception.message ?: "Error al actualizar perfil")
                    }
                }
            } catch (e: Exception) {
                showMessage("Error: ${e.message}")
            }
        }
    }

    // ===== CAMBIO DE CONTRASEÑA =====

    fun showChangePasswordDialog() {
        showChangePasswordDialog = true
        currentPassword = ""
        newPassword = ""
        confirmNewPassword = ""
    }

    fun hideChangePasswordDialog() {
        showChangePasswordDialog = false
        currentPassword = ""
        newPassword = ""
        confirmNewPassword = ""
    }

    fun updateCurrentPassword(value: String) {
        currentPassword = value
    }

    fun updateNewPassword(value: String) {
        newPassword = value
    }

    fun updateConfirmNewPassword(value: String) {
        confirmNewPassword = value
    }

    fun changePassword() {
        viewModelScope.launch {
            try {
                // Validaciones
                if (currentPassword.isBlank()) {
                    showMessage("Ingresa tu contraseña actual")
                    return@launch
                }

                if (newPassword.isBlank()) {
                    showMessage("Ingresa la nueva contraseña")
                    return@launch
                }

                if (newPassword.length < 6) {
                    showMessage("La contraseña debe tener al menos 6 caracteres")
                    return@launch
                }

                if (newPassword != confirmNewPassword) {
                    showMessage("Las contraseñas no coinciden")
                    return@launch
                }

                val user = currentUser ?: return@launch

                // Verificar contraseña actual
                val loginResult = userRepository.login(user.email, currentPassword)
                if (loginResult is Result.Error) {
                    showMessage("Contraseña actual incorrecta")
                    return@launch
                }

                // Cambiar contraseña
                when (val result = userRepository.changePassword(user.id, newPassword)) {
                    is Result.Success -> {
                        hideChangePasswordDialog()
                        showMessage("Contraseña cambiada correctamente")
                    }
                    is Result.Error -> {
                        showMessage(result.exception.message ?: "Error al cambiar contraseña")
                    }
                }
            } catch (e: Exception) {
                showMessage("Error: ${e.message}")
            }
        }
    }

    // ===== ELIMINAR CUENTA =====

    fun showDeleteAccountDialog() {
        showDeleteAccountDialog = true
        deleteAccountPassword = ""
    }

    fun hideDeleteAccountDialog() {
        showDeleteAccountDialog = false
        deleteAccountPassword = ""
    }

    fun updateDeleteAccountPassword(value: String) {
        deleteAccountPassword = value
    }

    fun deleteAccount(onAccountDeleted: () -> Unit) {
        viewModelScope.launch {
            try {
                val user = currentUser ?: return@launch

                // Verificar contraseña
                val loginResult = userRepository.login(user.email, deleteAccountPassword)
                if (loginResult is Result.Error) {
                    showMessage("Contraseña incorrecta")
                    return@launch
                }

                // Eliminar cuenta
                when (val result = userRepository.deleteUser(user.id)) {
                    is Result.Success -> {
                        userRepository.logout()
                        onAccountDeleted()
                    }
                    is Result.Error -> {
                        showMessage(result.exception.message ?: "Error al eliminar cuenta")
                    }
                }
            } catch (e: Exception) {
                showMessage("Error: ${e.message}")
            }
        }
    }

    /**
     * Cerrar sesión
     */
    fun logout(onLogout: () -> Unit) {
        viewModelScope.launch {
            userRepository.logout()
            onLogout()
        }
    }

    /**
     * Mostrar mensaje temporal
     */
    private fun showMessage(message: String) {
        val currentState = _uiState.value
        if (currentState is ProfileUiState.Success) {
            _uiState.value = currentState.copy(message = message)
        }
    }

    /**
     * Limpiar mensaje
     */
    fun clearMessage() {
        val currentState = _uiState.value
        if (currentState is ProfileUiState.Success) {
            _uiState.value = currentState.copy(message = null)
        }
    }
}

/**
 * Estados UI de la pantalla de perfil
 */
sealed class ProfileUiState {
    data object Loading : ProfileUiState()
    data class Success(
        val user: User,
        val message: String? = null
    ) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}
