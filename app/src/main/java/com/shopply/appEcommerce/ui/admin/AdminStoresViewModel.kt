package com.shopply.appEcommerce.ui.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopply.appEcommerce.data.local.entities.Store
import com.shopply.appEcommerce.data.local.entities.StoreStatus
import com.shopply.appEcommerce.data.repository.StoreRepository
import com.shopply.appEcommerce.domain.model.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * AdminStoresViewModel - ViewModel para la gestión de tiendas (Admin)
 *
 * Responsabilidades:
 * - Listar tiendas por estado (Pendientes, Aprobadas, Rechazadas)
 * - Aprobar tiendas
 * - Rechazar tiendas
 * - Ver estadísticas generales
 * - Filtrar tiendas
 */
@HiltViewModel
class AdminStoresViewModel @Inject constructor(
    private val storeRepository: StoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AdminStoresUiState>(AdminStoresUiState.Loading)
    val uiState: StateFlow<AdminStoresUiState> = _uiState.asStateFlow()

    // Filtro actual
    var currentFilter by mutableStateOf(StoreFilter.PENDING)
        private set

    // Diálogos
    var showApproveDialog by mutableStateOf(false)
        private set

    var showRejectDialog by mutableStateOf(false)
        private set

    var selectedStore: Store? by mutableStateOf(null)
        private set

    var rejectionReason by mutableStateOf("")
        private set

    init {
        loadStores()
    }

    /**
     * Cargar tiendas según el filtro actual
     */
    private fun loadStores() {
        viewModelScope.launch {
            try {
                _uiState.value = AdminStoresUiState.Loading

                // Cargar estadísticas
                val stats = storeRepository.getStoreStats()

                // Cargar tiendas según filtro
                val storesFlow = when (currentFilter) {
                    StoreFilter.ALL -> storeRepository.getApprovedStores() // Temporal: deberíamos tener getAllStores()
                    StoreFilter.PENDING -> storeRepository.getPendingStores()
                    StoreFilter.APPROVED -> storeRepository.getStoresByStatus(StoreStatus.APPROVED)
                    StoreFilter.REJECTED -> storeRepository.getStoresByStatus(StoreStatus.REJECTED)
                }

                storesFlow.collect { stores ->
                    _uiState.value = AdminStoresUiState.Success(
                        stores = stores,
                        stats = stats,
                        message = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = AdminStoresUiState.Error(
                    e.message ?: "Error al cargar tiendas"
                )
            }
        }
    }

    /**
     * Cambiar filtro de tiendas
     */
    fun changeFilter(filter: StoreFilter) {
        currentFilter = filter
        loadStores()
    }

    /**
     * Mostrar diálogo de aprobación
     */
    fun showApproveDialog(store: Store) {
        selectedStore = store
        showApproveDialog = true
    }

    /**
     * Ocultar diálogo de aprobación
     */
    fun hideApproveDialog() {
        showApproveDialog = false
        selectedStore = null
    }

    /**
     * Aprobar tienda
     */
    fun approveStore() {
        viewModelScope.launch {
            try {
                val store = selectedStore ?: return@launch

                when (val result = storeRepository.approveStore(store.id)) {
                    is Result.Success -> {
                        hideApproveDialog()
                        showMessage("Tienda ${store.name} aprobada exitosamente")
                        // Las tiendas se actualizarán automáticamente por el Flow
                    }
                    is Result.Error -> {
                        showMessage(result.exception.message ?: "Error al aprobar tienda")
                    }
                }
            } catch (e: Exception) {
                showMessage("Error: ${e.message}")
            }
        }
    }

    /**
     * Mostrar diálogo de rechazo
     */
    fun showRejectDialog(store: Store) {
        selectedStore = store
        rejectionReason = ""
        showRejectDialog = true
    }

    /**
     * Ocultar diálogo de rechazo
     */
    fun hideRejectDialog() {
        showRejectDialog = false
        selectedStore = null
        rejectionReason = ""
    }

    /**
     * Actualizar razón de rechazo
     */
    fun updateRejectionReason(reason: String) {
        rejectionReason = reason
    }

    /**
     * Rechazar tienda
     */
    fun rejectStore() {
        viewModelScope.launch {
            try {
                val store = selectedStore ?: return@launch

                if (rejectionReason.isBlank()) {
                    showMessage("Debes especificar la razón del rechazo")
                    return@launch
                }

                when (val result = storeRepository.rejectStore(store.id, rejectionReason)) {
                    is Result.Success -> {
                        hideRejectDialog()
                        showMessage("Tienda ${store.name} rechazada")
                        // Las tiendas se actualizarán automáticamente por el Flow
                    }
                    is Result.Error -> {
                        showMessage(result.exception.message ?: "Error al rechazar tienda")
                    }
                }
            } catch (e: Exception) {
                showMessage("Error: ${e.message}")
            }
        }
    }

    /**
     * Mostrar mensaje temporal
     */
    private fun showMessage(message: String) {
        val currentState = _uiState.value
        if (currentState is AdminStoresUiState.Success) {
            _uiState.value = currentState.copy(message = message)
        }
    }

    /**
     * Limpiar mensaje
     */
    fun clearMessage() {
        val currentState = _uiState.value
        if (currentState is AdminStoresUiState.Success) {
            _uiState.value = currentState.copy(message = null)
        }
    }

    /**
     * Refrescar tiendas
     */
    fun refresh() {
        loadStores()
    }
}

/**
 * Filtros de tiendas
 */
enum class StoreFilter {
    ALL,
    PENDING,
    APPROVED,
    REJECTED
}

/**
 * Estados UI de administración de tiendas
 */
sealed class AdminStoresUiState {
    data object Loading : AdminStoresUiState()
    data class Success(
        val stores: List<Store>,
        val stats: com.shopply.appEcommerce.data.repository.StoreStats,
        val message: String? = null
    ) : AdminStoresUiState()
    data class Error(val message: String) : AdminStoresUiState()
}

