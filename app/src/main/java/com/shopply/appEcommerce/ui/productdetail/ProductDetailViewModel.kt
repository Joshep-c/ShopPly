package com.shopply.appEcommerce.ui.productdetail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopply.appEcommerce.data.local.entities.Product
import com.shopply.appEcommerce.data.repository.CartRepository
import com.shopply.appEcommerce.data.repository.FavoriteRepository
import com.shopply.appEcommerce.data.repository.ProductRepository
import com.shopply.appEcommerce.data.repository.StoreRepository
import com.shopply.appEcommerce.data.repository.UserRepository
import com.shopply.appEcommerce.domain.model.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * ProductDetailViewModel - ViewModel para la pantalla de detalles del producto
 *
 * Gestiona:
 * - Información del producto
 * - Agregar a favoritos
 * - Agregar al carrito
 * - Cantidad seleccionada
 */
@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val favoriteRepository: FavoriteRepository,
    private val storeRepository: StoreRepository,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productId: Long = savedStateHandle.get<String>("productId")?.toLongOrNull() ?: 0L

    private val _uiState = MutableStateFlow<ProductDetailUiState>(ProductDetailUiState.Loading)
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    var quantity by mutableStateOf(1)
        private set

    var isFavorite by mutableStateOf(false)
        private set

    private var currentUserId: Long? = null

    init {
        loadProductDetail()
    }

    private fun loadProductDetail() {
        viewModelScope.launch {
            try {
                _uiState.value = ProductDetailUiState.Loading

                // Ejecutar todas las consultas en paralelo usando async en IO dispatcher
                withContext(Dispatchers.IO) {
                    // Obtener usuario actual
                    val userIdDeferred = async { userRepository.getCurrentUserId() }
                    currentUserId = userIdDeferred.await()

                    if (currentUserId == null) {
                        withContext(Dispatchers.Main) {
                            _uiState.value = ProductDetailUiState.Error("Usuario no autenticado")
                        }
                        return@withContext
                    }

                    // Validar productId
                    if (productId <= 0) {
                        withContext(Dispatchers.Main) {
                            _uiState.value = ProductDetailUiState.Error("ID de producto inválido")
                        }
                        return@withContext
                    }

                    // Ejecutar consultas paralelas para optimizar
                    val productDeferred = async { productRepository.getProductById(productId).first() }
                    val isFavoriteDeferred = async {
                        favoriteRepository.isFavorite(currentUserId!!, productId).first()
                    }

                    val product = productDeferred.await()
                    isFavorite = isFavoriteDeferred.await()

                    if (product == null) {
                        withContext(Dispatchers.Main) {
                            _uiState.value = ProductDetailUiState.Error("Producto no encontrado")
                        }
                        return@withContext
                    }

                    // Obtener tienda solo si el producto existe
                    val store = storeRepository.getStoreById(product.storeId).first()

                    // Actualizar UI en el hilo principal
                    withContext(Dispatchers.Main) {
                        _uiState.value = ProductDetailUiState.Success(
                            product = product,
                            storeName = store?.name ?: "Tienda desconocida",
                            storeRating = store?.rating ?: 0f
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = ProductDetailUiState.Error(
                    e.message ?: "Error al cargar producto. Intenta nuevamente."
                )
            }
        }
    }

    /**
     * Incrementar cantidad
     */
    fun incrementQuantity() {
        val currentState = _uiState.value
        if (currentState is ProductDetailUiState.Success) {
            if (quantity < currentState.product.stock) {
                quantity++
            }
        }
    }

    /**
     * Decrementar cantidad
     */
    fun decrementQuantity() {
        if (quantity > 1) {
            quantity--
        }
    }

    /**
     * Toggle favorito
     */
    fun toggleFavorite() {
        viewModelScope.launch {
            val userId = currentUserId ?: return@launch

            withContext(Dispatchers.IO) {
                when (val result = favoriteRepository.toggleFavorite(userId, productId)) {
                    is Result.Success -> {
                        withContext(Dispatchers.Main) {
                            isFavorite = result.data
                            val message = if (result.data) {
                                "Agregado a favoritos"
                            } else {
                                "Eliminado de favoritos"
                            }
                            _uiState.value = (_uiState.value as? ProductDetailUiState.Success)?.copy(
                                message = message
                            ) ?: _uiState.value
                        }
                    }
                    is Result.Error -> {
                        withContext(Dispatchers.Main) {
                            _uiState.value = (_uiState.value as? ProductDetailUiState.Success)?.copy(
                                message = "Error al actualizar favoritos"
                            ) ?: _uiState.value
                        }
                    }
                }
            }
        }
    }

    /**
     * Agregar al carrito
     */
    fun addToCart() {
        viewModelScope.launch {
            val userId = currentUserId ?: return@launch

            // Mostrar loading
            _uiState.value = (_uiState.value as? ProductDetailUiState.Success)?.copy(
                isAddingToCart = true
            ) ?: _uiState.value

            // Ejecutar operación en IO dispatcher
            withContext(Dispatchers.IO) {
                when (val result = cartRepository.addToCart(userId, productId, quantity)) {
                    is Result.Success -> {
                        withContext(Dispatchers.Main) {
                            _uiState.value = (_uiState.value as? ProductDetailUiState.Success)?.copy(
                                isAddingToCart = false,
                                message = "Agregado al carrito ($quantity ${if (quantity == 1) "unidad" else "unidades"})"
                            ) ?: _uiState.value
                            // Resetear cantidad
                            quantity = 1
                        }
                    }
                    is Result.Error -> {
                        withContext(Dispatchers.Main) {
                            _uiState.value = (_uiState.value as? ProductDetailUiState.Success)?.copy(
                                isAddingToCart = false,
                                message = result.exception.message ?: "Error al agregar al carrito"
                            ) ?: _uiState.value
                        }
                    }
                }
            }
        }
    }

    /**
     * Limpiar mensaje
     */
    fun clearMessage() {
        _uiState.value = (_uiState.value as? ProductDetailUiState.Success)?.copy(
            message = null
        ) ?: _uiState.value
    }
}

/**
 * Estados de UI para ProductDetail
 */
sealed class ProductDetailUiState {
    data object Loading : ProductDetailUiState()

    data class Success(
        val product: Product,
        val storeName: String,
        val storeRating: Float,
        val isAddingToCart: Boolean = false,
        val message: String? = null
    ) : ProductDetailUiState()

    data class Error(val message: String) : ProductDetailUiState()
}

