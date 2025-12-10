package com.shopply.appEcommerce.ui.store

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopply.appEcommerce.data.local.entities.Product
import com.shopply.appEcommerce.data.repository.ProductRepository
import com.shopply.appEcommerce.data.repository.StoreRepository
import com.shopply.appEcommerce.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * StoreViewModel - ViewModel para la gestión de productos del vendedor
 *
 * Responsabilidades:
 * - Cargar productos de la tienda del vendedor actual
 * - Gestionar filtros y búsqueda
 * - Calcular estadísticas (total, activos, pausados, sin stock)
 * - Manejar acciones CRUD (pausar, eliminar, duplicar)
 */
@HiltViewModel
class StoreViewModel @Inject constructor(
    private val storeRepository: StoreRepository,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    // Estados
    private val _uiState = MutableStateFlow<StoreUiState>(StoreUiState.Loading)
    val uiState: StateFlow<StoreUiState> = _uiState.asStateFlow()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _stats = MutableStateFlow(ProductStats())
    val stats: StateFlow<ProductStats> = _stats.asStateFlow()

    // Filtros
    private val _selectedFilter = MutableStateFlow(ProductFilter.ALL)
    val selectedFilter: StateFlow<ProductFilter> = _selectedFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadProducts()
    }

    /**
     * Carga los productos de la tienda del vendedor actual
     */
    fun loadProducts() {
        viewModelScope.launch {
            try {
                _uiState.value = StoreUiState.Loading

                // Obtener usuario actual
                val currentUser = userRepository.getCurrentUser().first()

                if (currentUser == null) {
                    _uiState.value = StoreUiState.Error("Usuario no autenticado")
                    return@launch
                }

                // Obtener tienda del usuario
                val store = storeRepository.getStoreByOwnerId(currentUser.id).first()

                if (store == null) {
                    _uiState.value = StoreUiState.Error("No tienes una tienda asociada")
                    return@launch
                }

                // Cargar productos de la tienda
                productRepository.getProductsByStore(store.id).collect { productList ->
                    _products.value = productList
                    calculateStats(productList)
                    _uiState.value = StoreUiState.Success(
                        products = applyFilters(productList),
                        stats = _stats.value
                    )
                }

            } catch (e: Exception) {
                _uiState.value = StoreUiState.Error(e.message ?: "Error al cargar productos")
            }
        }
    }

    /**
     * Calcula estadísticas de los productos
     */
    private fun calculateStats(products: List<Product>) {
        _stats.value = ProductStats(
            total = products.size,
            active = products.count { it.isActive && it.stock > 0 },
            paused = products.count { !it.isActive },
            outOfStock = products.count { it.stock == 0 && it.isActive }
        )
    }

    /**
     * Aplica filtros y búsqueda a la lista de productos
     */
    private fun applyFilters(products: List<Product>): List<Product> {
        var filtered = products

        // Filtro por estado
        filtered = when (_selectedFilter.value) {
            ProductFilter.ALL -> filtered
            ProductFilter.ACTIVE -> filtered.filter { it.isActive && it.stock > 0 }
            ProductFilter.PAUSED -> filtered.filter { !it.isActive }
            ProductFilter.OUT_OF_STOCK -> filtered.filter { it.stock == 0 }
        }

        // Filtro por búsqueda
        if (_searchQuery.value.isNotBlank()) {
            filtered = filtered.filter {
                it.name.contains(_searchQuery.value, ignoreCase = true) ||
                it.description.contains(_searchQuery.value, ignoreCase = true)
            }
        }

        return filtered
    }

    /**
     * Cambia el filtro seleccionado
     */
    fun setFilter(filter: ProductFilter) {
        _selectedFilter.value = filter
        refreshFilteredProducts()
    }

    /**
     * Actualiza la búsqueda
     */
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        refreshFilteredProducts()
    }

    /**
     * Reaplica filtros y actualiza UI
     */
    private fun refreshFilteredProducts() {
        val currentState = _uiState.value
        if (currentState is StoreUiState.Success) {
            _uiState.value = currentState.copy(
                products = applyFilters(_products.value)
            )
        }
    }

    /**
     * Pausa o activa un producto
     */
    fun toggleProductStatus(productId: Long) {
        viewModelScope.launch {
            try {
                val product = _products.value.find { it.id == productId } ?: return@launch
                val updatedProduct = product.copy(
                    isActive = !product.isActive,
                    updatedAt = System.currentTimeMillis()
                )
                productRepository.updateProduct(updatedProduct)
                // La UI se actualizará automáticamente por el Flow
            } catch (e: Exception) {
                // TODO: Manejar error
            }
        }
    }

    /**
     * Elimina un producto
     * TODO: Implementar eliminación de imágenes en Firebase
     */
    fun deleteProduct(productId: Long) {
        viewModelScope.launch {
            try {
                val product = _products.value.find { it.id == productId } ?: return@launch
                productRepository.deleteProduct(product)
                // La UI se actualizará automáticamente por el Flow
            } catch (e: Exception) {
                // TODO: Manejar error
            }
        }
    }

    /**
     * Duplica un producto (crea una copia)
     * TODO: Implementar cuando tengamos AddEditProduct
     */
    fun duplicateProduct(productId: Long) {
        viewModelScope.launch {
            try {
                val product = _products.value.find { it.id == productId } ?: return@launch
                val duplicate = product.copy(
                    id = 0, // Room generará nuevo ID
                    name = "${product.name} (Copia)",
                    isActive = false, // Crear como inactivo
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                productRepository.createProduct(duplicate)
            } catch (e: Exception) {
                // TODO: Manejar error
            }
        }
    }
}

/**
 * Estados UI de la pantalla de tienda
 */
sealed class StoreUiState {
    object Loading : StoreUiState()
    data class Success(
        val products: List<Product>,
        val stats: ProductStats
    ) : StoreUiState()
    data class Error(val message: String) : StoreUiState()
}

/**
 * Estadísticas de productos
 */
data class ProductStats(
    val total: Int = 0,
    val active: Int = 0,
    val paused: Int = 0,
    val outOfStock: Int = 0
)

/**
 * Filtros de productos
 */
enum class ProductFilter {
    ALL,
    ACTIVE,
    PAUSED,
    OUT_OF_STOCK
}
