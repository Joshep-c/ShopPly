package com.shopply.appEcommerce.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopply.appEcommerce.data.local.entities.Product
import com.shopply.appEcommerce.data.repository.FavoriteRepository
import com.shopply.appEcommerce.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * FavoritesViewModel - ViewModel para la pantalla de favoritos
 *
 * Responsabilidades:
 * - Cargar productos favoritos del usuario
 * - Eliminar productos de favoritos
 * - Gestionar estados de UI
 */
@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<FavoritesUiState>(FavoritesUiState.Loading)
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    private val _favoriteCount = MutableStateFlow(0)
    val favoriteCount: StateFlow<Int> = _favoriteCount.asStateFlow()

    private var currentUserId: Long? = null

    init {
        loadFavorites()
    }

    /**
     * Cargar productos favoritos del usuario actual
     */
    private fun loadFavorites() {
        viewModelScope.launch {
            try {
                _uiState.value = FavoritesUiState.Loading

                // Obtener usuario actual
                val currentUser = userRepository.getCurrentUser().first()

                if (currentUser == null) {
                    _uiState.value = FavoritesUiState.Error("Usuario no autenticado")
                    return@launch
                }

                currentUserId = currentUser.id

                // Cargar productos favoritos
                favoriteRepository.getFavoriteProducts(currentUser.id).collect { products ->
                    _uiState.value = if (products.isEmpty()) {
                        FavoritesUiState.Empty
                    } else {
                        FavoritesUiState.Success(products)
                    }
                }

            } catch (e: Exception) {
                _uiState.value = FavoritesUiState.Error(
                    e.message ?: "Error al cargar favoritos"
                )
            }
        }
    }

    /**
     * Cargar cantidad de favoritos (para mostrar en badge)
     */
    fun loadFavoriteCount() {
        viewModelScope.launch {
            try {
                val currentUser = userRepository.getCurrentUser().first()
                if (currentUser != null) {
                    favoriteRepository.getFavoriteCount(currentUser.id).collect { count ->
                        _favoriteCount.value = count
                    }
                }
            } catch (e: Exception) {
                // Silenciar error, no es crítico
            }
        }
    }

    /**
     * Eliminar producto de favoritos
     */
    fun removeFromFavorites(productId: Long) {
        viewModelScope.launch {
            try {
                val userId = currentUserId ?: return@launch
                favoriteRepository.removeFromFavorites(userId, productId)
                // La UI se actualizará automáticamente por el Flow
            } catch (e: Exception) {
                // TODO: Mostrar mensaje de error
            }
        }
    }

    /**
     * Limpiar todos los favoritos
     */
    fun clearAllFavorites() {
        viewModelScope.launch {
            try {
                val userId = currentUserId ?: return@launch
                favoriteRepository.clearFavorites(userId)
                // La UI se actualizará automáticamente
            } catch (e: Exception) {
                // TODO: Mostrar mensaje de error
            }
        }
    }

    /**
     * Refrescar favoritos
     */
    fun refresh() {
        loadFavorites()
    }
}

/**
 * Estados UI de la pantalla de favoritos
 */
sealed class FavoritesUiState {
    data object Loading : FavoritesUiState()
    data object Empty : FavoritesUiState()
    data class Success(val products: List<Product>) : FavoritesUiState()
    data class Error(val message: String) : FavoritesUiState()
}

