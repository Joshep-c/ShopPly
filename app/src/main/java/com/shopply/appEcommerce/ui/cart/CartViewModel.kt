package com.shopply.appEcommerce.ui.cart

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopply.appEcommerce.data.local.dao.ProductDao
import com.shopply.appEcommerce.data.local.entities.CartItem
import com.shopply.appEcommerce.data.local.entities.Product
import com.shopply.appEcommerce.data.repository.CartRepository
import com.shopply.appEcommerce.data.repository.UserRepository
import com.shopply.appEcommerce.domain.model.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * CartViewModel - ViewModel para el carrito de compras
 *
 * Responsabilidades:
 * - Cargar items del carrito con sus productos
 * - Actualizar cantidades
 * - Eliminar items
 * - Calcular totales
 * - Procesar pago (checkout)
 * - Validar stock antes de pagar
 */
@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val userRepository: UserRepository,
    private val productDao: ProductDao
) : ViewModel() {

    private val _uiState = MutableStateFlow<CartUiState>(CartUiState.Loading)
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    // Estados del checkout
    var showCheckoutDialog by mutableStateOf(false)
        private set

    var showPaymentSuccessDialog by mutableStateOf(false)
        private set

    var isProcessingPayment by mutableStateOf(false)
        private set

    private var currentUserId: Long? = null
    private var cartItems: List<CartItemWithProduct> = emptyList()

    init {
        loadCart()
    }

    /**
     * Cargar carrito del usuario actual
     */
    private fun loadCart() {
        viewModelScope.launch {
            try {
                _uiState.value = CartUiState.Loading

                // Obtener usuario actual
                val currentUser = userRepository.getCurrentUser().first()
                if (currentUser == null) {
                    _uiState.value = CartUiState.Error("Usuario no autenticado")
                    return@launch
                }

                currentUserId = currentUser.id

                // Cargar items del carrito con sus productos
                cartRepository.getCartItems(currentUser.id).collect { items ->
                    if (items.isEmpty()) {
                        _uiState.value = CartUiState.Empty
                    } else {
                        // Obtener información de productos
                        val itemsWithProducts = items.mapNotNull { cartItem ->
                            val product = productDao.getProductByIdSync(cartItem.productId)
                            product?.let {
                                CartItemWithProduct(
                                    cartItem = cartItem,
                                    product = it
                                )
                            }
                        }

                        cartItems = itemsWithProducts

                        // Calcular totales
                        val subtotal = itemsWithProducts.sumOf {
                            it.product.price * it.cartItem.quantity
                        }
                        val shipping = if (subtotal > 0) 10.0 else 0.0 // S/ 10 envío fijo
                        val total = subtotal + shipping

                        _uiState.value = CartUiState.Success(
                            items = itemsWithProducts,
                            subtotal = subtotal,
                            shipping = shipping,
                            total = total,
                            message = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = CartUiState.Error(
                    e.message ?: "Error al cargar el carrito"
                )
            }
        }
    }

    /**
     * Incrementar cantidad de un item
     */
    fun incrementQuantity(productId: Long) {
        viewModelScope.launch {
            try {
                val userId = currentUserId ?: return@launch
                val item = cartItems.find { it.cartItem.productId == productId } ?: return@launch

                // Verificar stock
                if (item.cartItem.quantity >= item.product.stock) {
                    showMessage("Stock máximo alcanzado")
                    return@launch
                }

                when (val result = cartRepository.updateQuantity(
                    userId,
                    productId,
                    item.cartItem.quantity + 1
                )) {
                    is Result.Success -> {
                        // La UI se actualizará automáticamente por el Flow
                    }
                    is Result.Error -> {
                        showMessage(result.exception.message ?: "Error al actualizar cantidad")
                    }
                }
            } catch (e: Exception) {
                showMessage("Error: ${e.message}")
            }
        }
    }

    /**
     * Decrementar cantidad de un item
     */
    fun decrementQuantity(productId: Long) {
        viewModelScope.launch {
            try {
                val userId = currentUserId ?: return@launch
                val item = cartItems.find { it.cartItem.productId == productId } ?: return@launch

                val newQuantity = item.cartItem.quantity - 1

                if (newQuantity <= 0) {
                    // Si llega a 0, eliminar del carrito
                    removeFromCart(productId)
                } else {
                    when (val result = cartRepository.updateQuantity(userId, productId, newQuantity)) {
                        is Result.Success -> {
                            // La UI se actualizará automáticamente
                        }
                        is Result.Error -> {
                            showMessage(result.exception.message ?: "Error al actualizar cantidad")
                        }
                    }
                }
            } catch (e: Exception) {
                showMessage("Error: ${e.message}")
            }
        }
    }

    /**
     * Eliminar item del carrito
     */
    fun removeFromCart(productId: Long) {
        viewModelScope.launch {
            try {
                val userId = currentUserId ?: return@launch
                when (val result = cartRepository.removeFromCart(userId, productId)) {
                    is Result.Success -> {
                        showMessage("Producto eliminado del carrito")
                    }
                    is Result.Error -> {
                        showMessage(result.exception.message ?: "Error al eliminar producto")
                    }
                }
            } catch (e: Exception) {
                showMessage("Error: ${e.message}")
            }
        }
    }

    /**
     * Limpiar todo el carrito
     */
    fun clearCart() {
        viewModelScope.launch {
            try {
                val userId = currentUserId ?: return@launch
                when (val result = cartRepository.clearCart(userId)) {
                    is Result.Success -> {
                        showMessage("Carrito vaciado")
                    }
                    is Result.Error -> {
                        showMessage(result.exception.message ?: "Error al vaciar carrito")
                    }
                }
            } catch (e: Exception) {
                showMessage("Error: ${e.message}")
            }
        }
    }

    // ===== CHECKOUT =====

    /**
     * Mostrar diálogo de confirmación de pago
     */
    fun showCheckoutDialog() {
        showCheckoutDialog = true
    }

    /**
     * Ocultar diálogo de confirmación de pago
     */
    fun hideCheckoutDialog() {
        showCheckoutDialog = false
    }

    /**
     * Procesar pago (checkout)
     */
    fun processPayment() {
        viewModelScope.launch {
            try {
                isProcessingPayment = true
                val userId = currentUserId ?: run {
                    isProcessingPayment = false
                    hideCheckoutDialog()
                    showMessage("Usuario no autenticado")
                    return@launch
                }

                // 1. Validar stock de todos los items
                when (val result = cartRepository.validateCartStock(userId)) {
                    is Result.Success -> {
                        if (result.data.isNotEmpty()) {
                            // Hay problemas de stock
                            isProcessingPayment = false
                            hideCheckoutDialog()
                            showMessage("Algunos productos no están disponibles: ${result.data.joinToString()}")
                            return@launch
                        }
                    }
                    is Result.Error -> {
                        isProcessingPayment = false
                        hideCheckoutDialog()
                        showMessage("Error al validar stock")
                        return@launch
                    }
                }

                // 2. Simular procesamiento de pago (3 segundos)
                kotlinx.coroutines.delay(3000)

                // 3. Reducir stock de productos (operación de BD)
                cartItems.forEach { item ->
                    val product = item.product
                    val newStock = product.stock - item.cartItem.quantity
                    productDao.updateProduct(
                        product.copy(
                            stock = newStock.coerceAtLeast(0),
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                }

                // 4. Vaciar carrito
                when (cartRepository.clearCart(userId)) {
                    is Result.Success -> {
                        // Carrito vaciado exitosamente
                    }
                    is Result.Error -> {
                        // Error al vaciar pero continuamos
                    }
                }

                // 5. Finalizar procesamiento y mostrar confirmación
                isProcessingPayment = false
                hideCheckoutDialog()

                // Pequeño delay para asegurar que el diálogo de confirmación se cierre
                kotlinx.coroutines.delay(200)

                // Mostrar diálogo de éxito
                showPaymentSuccessDialog = true

            } catch (e: Exception) {
                isProcessingPayment = false
                hideCheckoutDialog()
                showMessage("Error al procesar pago: ${e.message}")
            }
        }
    }

    /**
     * Ocultar diálogo de pago exitoso
     */
    fun hidePaymentSuccessDialog() {
        showPaymentSuccessDialog = false
    }

    /**
     * Mostrar mensaje temporal
     */
    private fun showMessage(message: String) {
        val currentState = _uiState.value
        if (currentState is CartUiState.Success) {
            _uiState.value = currentState.copy(message = message)
        }
    }

    /**
     * Limpiar mensaje
     */
    fun clearMessage() {
        val currentState = _uiState.value
        if (currentState is CartUiState.Success) {
            _uiState.value = currentState.copy(message = null)
        }
    }

    /**
     * Refrescar carrito
     */
    fun refresh() {
        loadCart()
    }
}

/**
 * Item del carrito con información del producto
 */
data class CartItemWithProduct(
    val cartItem: CartItem,
    val product: Product
)

/**
 * Estados UI del carrito
 */
sealed class CartUiState {
    data object Loading : CartUiState()
    data object Empty : CartUiState()
    data class Success(
        val items: List<CartItemWithProduct>,
        val subtotal: Double,
        val shipping: Double,
        val total: Double,
        val message: String? = null
    ) : CartUiState()
    data class Error(val message: String) : CartUiState()
}
