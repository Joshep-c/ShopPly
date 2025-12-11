package com.shopply.appEcommerce.ui.addproduct

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopply.appEcommerce.data.local.entities.Category
import com.shopply.appEcommerce.data.local.entities.Product
import com.shopply.appEcommerce.data.repository.CategoryRepository
import com.shopply.appEcommerce.data.repository.ProductRepository
import com.shopply.appEcommerce.data.repository.StoreRepository
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
 * AddEditProductViewModel - ViewModel para agregar/editar productos
 *
 * Responsabilidades:
 * - Cargar categorías disponibles
 * - Cargar producto para edición
 * - Validar campos del formulario
 * - Guardar/actualizar producto
 * - Gestión de estados UI
 */
@HiltViewModel
class AddEditProductViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val storeRepository: StoreRepository,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productId: Long? = savedStateHandle.get<String>("productId")?.toLongOrNull()

    private val _uiState = MutableStateFlow<AddEditProductUiState>(AddEditProductUiState.Loading)
    val uiState: StateFlow<AddEditProductUiState> = _uiState.asStateFlow()

    // Campos del formulario
    var name by mutableStateOf("")
        private set

    var description by mutableStateOf("")
        private set

    var price by mutableStateOf("")
        private set

    var stock by mutableStateOf("")
        private set

    var selectedCategory: Category? by mutableStateOf(null)
        private set

    var imageUrl by mutableStateOf("")
        private set

    var isActive by mutableStateOf(true)
        private set

    var isSaving by mutableStateOf(false)
        private set

    private var currentStoreId: Long? = null
    private var categories: List<Category> = emptyList()

    val isEditing: Boolean
        get() = productId != null

    init {
        loadFormData()
    }

    /**
     * Cargar datos del formulario (categorías y producto si es edición)
     */
    private fun loadFormData() {
        viewModelScope.launch {
            try {
                _uiState.value = AddEditProductUiState.Loading

                // Obtener usuario actual
                val currentUser = userRepository.getCurrentUser().first()
                if (currentUser == null) {
                    _uiState.value = AddEditProductUiState.Error("Usuario no autenticado")
                    return@launch
                }

                // Obtener tienda del vendedor
                val store = storeRepository.getStoreByOwnerId(currentUser.id).first()
                if (store == null) {
                    _uiState.value = AddEditProductUiState.Error("No tienes una tienda registrada")
                    return@launch
                }
                currentStoreId = store.id

                // Cargar categorías
                categories = categoryRepository.getAllCategories().first()

                // Si es edición, cargar producto
                if (productId != null) {
                    val product = productRepository.getProductById(productId).first()
                    if (product == null) {
                        _uiState.value = AddEditProductUiState.Error("Producto no encontrado")
                        return@launch
                    }

                    // Llenar formulario con datos del producto
                    name = product.name
                    description = product.description
                    price = product.price.toString()
                    stock = product.stock.toString()
                    imageUrl = product.imageUrl
                    isActive = product.isActive
                    selectedCategory = categories.find { it.id == product.categoryId }
                }

                _uiState.value = AddEditProductUiState.Success(
                    categories = categories,
                    message = null
                )
            } catch (e: Exception) {
                _uiState.value = AddEditProductUiState.Error(
                    e.message ?: "Error al cargar datos"
                )
            }
        }
    }

    /**
     * Actualizar campos del formulario
     */
    fun updateName(value: String) {
        if (value.length <= 80) {
            name = value
        }
    }

    fun updateDescription(value: String) {
        if (value.length <= 500) {
            description = value
        }
    }

    fun updatePrice(value: String) {
        if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
            price = value
        }
    }

    fun updateStock(value: String) {
        if (value.isEmpty() || value.matches(Regex("^\\d+$"))) {
            stock = value
        }
    }

    fun updateCategory(category: Category) {
        selectedCategory = category
    }

    fun updateImageUrl(url: String) {
        imageUrl = url
    }

    fun updateIsActive(active: Boolean) {
        isActive = active
    }

    /**
     * Validar formulario
     */
    private fun validateForm(): String? {
        if (name.isBlank()) {
            return "El nombre es obligatorio"
        }

        if (name.length < 3) {
            return "El nombre debe tener al menos 3 caracteres"
        }

        if (description.isBlank()) {
            return "La descripción es obligatoria"
        }

        if (description.length < 10) {
            return "La descripción debe tener al menos 10 caracteres"
        }

        if (price.isBlank()) {
            return "El precio es obligatorio"
        }

        val priceValue = price.toDoubleOrNull()
        if (priceValue == null || priceValue <= 0) {
            return "El precio debe ser mayor a 0"
        }

        if (stock.isBlank()) {
            return "El stock es obligatorio"
        }

        val stockValue = stock.toIntOrNull()
        if (stockValue == null || stockValue < 0) {
            return "El stock debe ser 0 o mayor"
        }

        if (selectedCategory == null) {
            return "Debes seleccionar una categoría"
        }

        if (imageUrl.isBlank()) {
            return "La URL de la imagen es obligatoria"
        }

        return null
    }

    /**
     * Guardar producto
     */
    fun saveProduct(onSuccess: (Long) -> Unit) {
        viewModelScope.launch {
            try {
                // Validar formulario
                val validationError = validateForm()
                if (validationError != null) {
                    showMessage(validationError)
                    return@launch
                }

                val storeId = currentStoreId ?: run {
                    showMessage("No se encontró la tienda")
                    return@launch
                }

                isSaving = true

                val product = Product(
                    id = productId ?: 0,
                    storeId = storeId,
                    categoryId = selectedCategory!!.id,
                    name = name.trim(),
                    description = description.trim(),
                    price = price.toDouble(),
                    stock = stock.toInt(),
                    imageUrl = imageUrl.trim(),
                    isActive = isActive,
                    createdAt = if (productId == null) System.currentTimeMillis() else 0,
                    updatedAt = System.currentTimeMillis()
                )

                if (productId != null) {
                    // Actualizar producto existente
                    when (val result = productRepository.updateProduct(product)) {
                        is Result.Success -> {
                            isSaving = false
                            onSuccess(productId)
                        }
                        is Result.Error -> {
                            isSaving = false
                            showMessage(result.exception.message ?: "Error al actualizar producto")
                        }
                    }
                } else {
                    // Crear nuevo producto
                    when (val result = productRepository.createProduct(product)) {
                        is Result.Success -> {
                            isSaving = false
                            onSuccess(result.data)
                        }
                        is Result.Error -> {
                            isSaving = false
                            showMessage(result.exception.message ?: "Error al crear producto")
                        }
                    }
                }
            } catch (e: Exception) {
                isSaving = false
                showMessage("Error: ${e.message}")
            }
        }
    }

    /**
     * Mostrar mensaje temporal
     */
    private fun showMessage(message: String) {
        val currentState = _uiState.value
        if (currentState is AddEditProductUiState.Success) {
            _uiState.value = currentState.copy(message = message)
        }
    }

    /**
     * Limpiar mensaje
     */
    fun clearMessage() {
        val currentState = _uiState.value
        if (currentState is AddEditProductUiState.Success) {
            _uiState.value = currentState.copy(message = null)
        }
    }
}

/**
 * Estados UI del formulario
 */
sealed class AddEditProductUiState {
    data object Loading : AddEditProductUiState()
    data class Success(
        val categories: List<Category>,
        val message: String? = null
    ) : AddEditProductUiState()
    data class Error(val message: String) : AddEditProductUiState()
}

