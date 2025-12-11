package com.shopply.appEcommerce.ui.admin

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopply.appEcommerce.data.local.entities.Category
import com.shopply.appEcommerce.data.repository.CategoryRepository
import com.shopply.appEcommerce.data.storage.LocalStorageService
import com.shopply.appEcommerce.domain.model.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminCategoriesViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val localStorageService: LocalStorageService
) : ViewModel() {

    private val _uiState = MutableStateFlow<AdminCategoriesUiState>(AdminCategoriesUiState.Loading)
    val uiState: StateFlow<AdminCategoriesUiState> = _uiState.asStateFlow()

    // Estado del formulario
    var name by mutableStateOf("")
        private set
    var description by mutableStateOf("")
        private set
    var imageUrl by mutableStateOf("")
        private set
    var isActive by mutableStateOf(true)
        private set
    var displayOrder by mutableStateOf(0)
        private set

    // Modo de imagen: URL manual o Galería
    var useManualUrl by mutableStateOf(false)
        private set

    var isSavingImage by mutableStateOf(false)
        private set

    // Estado de edición
    var editingCategory by mutableStateOf<Category?>(null)
        private set
    var showCategoryDialog by mutableStateOf(false)
        private set
    var showDeleteDialog by mutableStateOf(false)
        private set
    var categoryToDelete by mutableStateOf<Category?>(null)
        private set

    // Búsqueda
    var searchQuery by mutableStateOf("")
        private set

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            try {
                categoryRepository.getAllCategoriesIncludingInactive().collect { categories ->
                    val filteredCategories = if (searchQuery.isBlank()) {
                        categories
                    } else {
                        categories.filter {
                            it.name.contains(searchQuery, ignoreCase = true) ||
                            it.description?.contains(searchQuery, ignoreCase = true) == true
                        }
                    }

                    _uiState.value = AdminCategoriesUiState.Success(
                        categories = filteredCategories,
                        message = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = AdminCategoriesUiState.Error(
                    e.message ?: "Error al cargar categorías"
                )
            }
        }
    }

    fun updateSearchQuery(query: String) {
        searchQuery = query
        loadCategories()
    }

    fun showCreateDialog() {
        clearForm()
        editingCategory = null
        showCategoryDialog = true
    }

    fun showEditDialog(category: Category) {
        editingCategory = category
        name = category.name
        description = category.description ?: ""
        imageUrl = category.imageUrl ?: ""
        isActive = category.isActive
        displayOrder = category.displayOrder
        showCategoryDialog = true
    }

    fun hideCategoryDialog() {
        showCategoryDialog = false
        clearForm()
        editingCategory = null
    }

    fun showDeleteConfirmation(category: Category) {
        categoryToDelete = category
        showDeleteDialog = true
    }

    fun hideDeleteDialog() {
        showDeleteDialog = false
        categoryToDelete = null
    }

    fun updateName(value: String) {
        name = value
    }

    fun updateDescription(value: String) {
        description = value
    }

    fun updateImageUrl(value: String) {
        imageUrl = value
    }

    fun updateIsActive(value: Boolean) {
        isActive = value
    }

    fun updateDisplayOrder(value: Int) {
        displayOrder = value
    }

    fun toggleImageInputMode() {
        useManualUrl = !useManualUrl
        if (!useManualUrl) {
            // Si cambia a galería, limpiar URL manual
            imageUrl = ""
        }
    }

    /**
     * Guardar imagen desde galería
     */
    fun saveImageFromGallery(uri: Uri) {
        viewModelScope.launch {
            try {
                isSavingImage = true

                // Guardar imagen en almacenamiento local
                val result = localStorageService.saveProductImage(uri)

                if (result.isSuccess) {
                    imageUrl = result.getOrNull() ?: ""
                    showMessage("Imagen guardada correctamente")
                } else {
                    showMessage("Error al guardar imagen: ${result.exceptionOrNull()?.message}")
                }

                isSavingImage = false
            } catch (e: Exception) {
                isSavingImage = false
                showMessage("Error: ${e.message}")
            }
        }
    }

    /**
     * Mostrar mensaje temporal
     */
    private fun showMessage(message: String) {
        val currentState = _uiState.value
        if (currentState is AdminCategoriesUiState.Success) {
            _uiState.value = currentState.copy(message = message)
        }
    }

    fun saveCategory() {
        viewModelScope.launch {
            if (!validateForm()) return@launch

            try {
                val category = if (editingCategory != null) {
                    // Editar categoría existente
                    editingCategory!!.copy(
                        name = name.trim(),
                        description = description.trim().ifBlank { null },
                        imageUrl = imageUrl.trim().ifBlank { null },
                        isActive = isActive,
                        displayOrder = displayOrder
                    )
                } else {
                    // Crear nueva categoría
                    Category(
                        name = name.trim(),
                        description = description.trim().ifBlank { null },
                        imageUrl = imageUrl.trim().ifBlank { null },
                        isActive = isActive,
                        displayOrder = displayOrder,
                        createdAt = System.currentTimeMillis()
                    )
                }

                val result = if (editingCategory != null) {
                    categoryRepository.updateCategory(category)
                } else {
                    categoryRepository.createCategory(category)
                }

                when (result) {
                    is Result.Success -> {
                        val currentState = _uiState.value as? AdminCategoriesUiState.Success
                        _uiState.value = currentState?.copy(
                            message = if (editingCategory != null)
                                "Categoría actualizada correctamente"
                            else
                                "Categoría creada correctamente"
                        ) ?: AdminCategoriesUiState.Success(emptyList(), "Categoría guardada")

                        hideCategoryDialog()
                        loadCategories()
                    }
                    is Result.Error -> {
                        val currentState = _uiState.value as? AdminCategoriesUiState.Success
                        _uiState.value = currentState?.copy(
                            message = result.exception.message ?: "Error al guardar categoría"
                        ) ?: AdminCategoriesUiState.Error(result.exception.message ?: "Error")
                    }
                }
            } catch (e: Exception) {
                val currentState = _uiState.value as? AdminCategoriesUiState.Success
                _uiState.value = currentState?.copy(
                    message = e.message ?: "Error al guardar categoría"
                ) ?: AdminCategoriesUiState.Error(e.message ?: "Error")
            }
        }
    }

    fun deleteCategory() {
        viewModelScope.launch {
            val category = categoryToDelete ?: return@launch

            try {
                when (val result = categoryRepository.deleteCategory(category)) {
                    is Result.Success -> {
                        val currentState = _uiState.value as? AdminCategoriesUiState.Success
                        _uiState.value = currentState?.copy(
                            message = "Categoría eliminada correctamente"
                        ) ?: AdminCategoriesUiState.Success(emptyList(), "Categoría eliminada")

                        hideDeleteDialog()
                        loadCategories()
                    }
                    is Result.Error -> {
                        val currentState = _uiState.value as? AdminCategoriesUiState.Success
                        _uiState.value = currentState?.copy(
                            message = result.exception.message ?: "Error al eliminar categoría"
                        ) ?: AdminCategoriesUiState.Error(result.exception.message ?: "Error")
                    }
                }
            } catch (e: Exception) {
                val currentState = _uiState.value as? AdminCategoriesUiState.Success
                _uiState.value = currentState?.copy(
                    message = e.message ?: "Error al eliminar categoría"
                ) ?: AdminCategoriesUiState.Error(e.message ?: "Error")
            }
        }
    }

    fun toggleCategoryActive(category: Category) {
        viewModelScope.launch {
            try {
                when (categoryRepository.toggleCategoryActive(category.id, !category.isActive)) {
                    is Result.Success -> {
                        val currentState = _uiState.value as? AdminCategoriesUiState.Success
                        _uiState.value = currentState?.copy(
                            message = if (category.isActive)
                                "Categoría desactivada"
                            else
                                "Categoría activada"
                        ) ?: AdminCategoriesUiState.Success(emptyList(), "Estado actualizado")

                        loadCategories()
                    }
                    is Result.Error -> {
                        val currentState = _uiState.value as? AdminCategoriesUiState.Success
                        _uiState.value = currentState?.copy(
                            message = "Error al cambiar estado de categoría"
                        ) ?: AdminCategoriesUiState.Error("Error al cambiar estado")
                    }
                }
            } catch (e: Exception) {
                val currentState = _uiState.value as? AdminCategoriesUiState.Success
                _uiState.value = currentState?.copy(
                    message = e.message ?: "Error al cambiar estado"
                ) ?: AdminCategoriesUiState.Error(e.message ?: "Error")
            }
        }
    }

    fun clearMessage() {
        val currentState = _uiState.value as? AdminCategoriesUiState.Success
        if (currentState != null) {
            _uiState.value = currentState.copy(message = null)
        }
    }

    private fun validateForm(): Boolean {
        if (name.isBlank()) {
            val currentState = _uiState.value as? AdminCategoriesUiState.Success
            _uiState.value = currentState?.copy(
                message = "El nombre es requerido"
            ) ?: AdminCategoriesUiState.Error("El nombre es requerido")
            return false
        }

        if (name.length < 3) {
            val currentState = _uiState.value as? AdminCategoriesUiState.Success
            _uiState.value = currentState?.copy(
                message = "El nombre debe tener al menos 3 caracteres"
            ) ?: AdminCategoriesUiState.Error("Nombre muy corto")
            return false
        }

        if (name.length > 50) {
            val currentState = _uiState.value as? AdminCategoriesUiState.Success
            _uiState.value = currentState?.copy(
                message = "El nombre no puede tener más de 50 caracteres"
            ) ?: AdminCategoriesUiState.Error("Nombre muy largo")
            return false
        }

        if (description.length > 200) {
            val currentState = _uiState.value as? AdminCategoriesUiState.Success
            _uiState.value = currentState?.copy(
                message = "La descripción no puede tener más de 200 caracteres"
            ) ?: AdminCategoriesUiState.Error("Descripción muy larga")
            return false
        }

        if (imageUrl.isNotBlank() && !imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
            val currentState = _uiState.value as? AdminCategoriesUiState.Success
            _uiState.value = currentState?.copy(
                message = "La URL de la imagen debe comenzar con http:// o https://"
            ) ?: AdminCategoriesUiState.Error("URL inválida")
            return false
        }

        return true
    }

    private fun clearForm() {
        name = ""
        description = ""
        imageUrl = ""
        isActive = true
        displayOrder = 0
    }
}

sealed class AdminCategoriesUiState {
    data object Loading : AdminCategoriesUiState()
    data class Success(
        val categories: List<Category>,
        val message: String?
    ) : AdminCategoriesUiState()
    data class Error(val message: String) : AdminCategoriesUiState()
}

