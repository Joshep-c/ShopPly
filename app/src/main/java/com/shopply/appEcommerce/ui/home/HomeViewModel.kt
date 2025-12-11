package com.shopply.appEcommerce.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopply.appEcommerce.data.local.entities.Category
import com.shopply.appEcommerce.data.local.entities.Product
import com.shopply.appEcommerce.data.local.entities.User
import com.shopply.appEcommerce.data.repository.CategoryRepository
import com.shopply.appEcommerce.data.repository.ProductRepository
import com.shopply.appEcommerce.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            try {
                // Combinar flujos de datos
                combine(
                    userRepository.getCurrentUser(),
                    productRepository.getAllProducts(),
                    categoryRepository.getAllCategories()
                ) { user, products, categories ->
                    Triple(user, products, categories)
                }.collect { (user, products, categories) ->
                    if (user != null) {
                        // Filtrar solo categorías activas
                        val activeCategories = categories.filter { it.isActive }

                        // Separar productos recomendados y ofertas
                        val recommendedProducts = products.take(10) // Primeros 10
                        val specialOffers = products.reversed().take(8) // Últimos 8

                        _uiState.value = HomeUiState.Success(
                            user = user,
                            categories = activeCategories,
                            allProducts = products,
                            recommendedProducts = recommendedProducts,
                            specialOffers = specialOffers
                        )
                    } else {
                        _uiState.value = HomeUiState.Error("No se pudo cargar el usuario")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }
}

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(
        val user: User,
        val categories: List<Category>,
        val allProducts: List<Product>,
        val recommendedProducts: List<Product>,
        val specialOffers: List<Product>
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
