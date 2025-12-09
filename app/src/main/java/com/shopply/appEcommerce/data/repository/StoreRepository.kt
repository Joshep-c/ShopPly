package com.shopply.appEcommerce.data.repository

import com.shopply.appEcommerce.data.local.dao.StoreDao
import com.shopply.appEcommerce.data.local.entities.Store
import com.shopply.appEcommerce.data.local.entities.StoreStatus
import com.shopply.appEcommerce.domain.model.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * StoreRepository - Repositorio de tiendas
 *
 * Gestiona:
 * - CRUD de tiendas
 * - Proceso de aprobación (Admin)
 * - Búsqueda de tiendas
 * - Estadísticas
 */
@Singleton
class StoreRepository @Inject constructor(
    private val storeDao: StoreDao
) {

    // CONSULTAS BÁSICAS

    /**
     * Obtener tienda por ID
     */
    fun getStoreById(storeId: Long): Flow<Store?> = storeDao.getStoreById(storeId)

    /**
     * Obtener tienda del vendedor
     * Un vendedor solo puede tener una tienda
     */
    fun getStoreByOwnerId(ownerId: Long): Flow<Store?> = storeDao.getStoreByOwnerId(ownerId)

    /**
     * Obtener tiendas aprobadas (para compradores)
     */
    fun getApprovedStores(): Flow<List<Store>> = storeDao.getApprovedStores()

    /**
     * Buscar tiendas por nombre o descripción
     */
    fun searchStores(query: String): Flow<List<Store>> = storeDao.searchStores(query)

    // VENDEDOR: GESTIÓN DE TIENDA

    /**
     * Crear nueva tienda (Vendedor)
     * La tienda queda en estado PENDING hasta que un admin la apruebe
     */
    suspend fun createStore(store: Store): Result<Long> {
        return try {
            // Verificar si el vendedor ya tiene una tienda
            val existingStore = storeDao.getStoreByOwnerIdSync(store.ownerId)
            if (existingStore != null) {
                return Result.Error(Exception("Ya tienes una tienda registrada"))
            }

            // Validar RUC (11 dígitos)
            if (store.ruc.length != 11 || !store.ruc.all { it.isDigit() }) {
                return Result.Error(Exception("El RUC debe tener 11 dígitos"))
            }

            val storeId = storeDao.insertStore(store.copy(status = StoreStatus.PENDING))
            Result.Success(storeId)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Actualizar información de la tienda (Vendedor)
     */
    suspend fun updateStore(store: Store): Result<Unit> {
        return try {
            storeDao.updateStore(store)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Eliminar tienda (Vendedor)
     */
    suspend fun deleteStore(store: Store): Result<Unit> {
        return try {
            storeDao.deleteStore(store)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // ADMIN: APROBACIÓN DE TIENDAS

    /**
     * Obtener tiendas pendientes de aprobación (Admin)
     */
    fun getPendingStores(): Flow<List<Store>> = storeDao.getStoresByStatus(StoreStatus.PENDING)

    /**
     * Aprobar tienda (Admin)
     */
    suspend fun approveStore(storeId: Long): Result<Unit> {
        return try {
            storeDao.updateStoreStatus(
                storeId = storeId,
                status = StoreStatus.APPROVED,
                approvedAt = System.currentTimeMillis()
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Rechazar tienda (Admin)
     */
    suspend fun rejectStore(storeId: Long, reason: String): Result<Unit> {
        return try {
            if (reason.isBlank()) {
                return Result.Error(Exception("Debe especificar la razón del rechazo"))
            }

            storeDao.updateStoreStatus(
                storeId = storeId,
                status = StoreStatus.REJECTED,
                rejectionReason = reason
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Obtener tiendas por estado (Admin)
     */
    fun getStoresByStatus(status: StoreStatus): Flow<List<Store>> =
        storeDao.getStoresByStatus(status)

    // ESTADÍSTICAS

    /**
     * Obtener estadísticas de tiendas (Admin)
     */
    suspend fun getStoreStats(): StoreStats {
        return StoreStats(
            totalStores = storeDao.countStoresByStatus(StoreStatus.APPROVED) +
                    storeDao.countStoresByStatus(StoreStatus.PENDING) +
                    storeDao.countStoresByStatus(StoreStatus.REJECTED),
            pendingStores = storeDao.countStoresByStatus(StoreStatus.PENDING),
            approvedStores = storeDao.countStoresByStatus(StoreStatus.APPROVED),
            rejectedStores = storeDao.countStoresByStatus(StoreStatus.REJECTED)
        )
    }

    /**
     * Actualizar calificación de la tienda
     * (Para el sistema de reviews, en un futuro)
     */
    suspend fun updateStoreRating(storeId: Long, rating: Float) {
        storeDao.updateStoreRating(storeId, rating)
    }
}

/**
 * Estadísticas de tiendas
 */
data class StoreStats(
    val totalStores: Int,
    val pendingStores: Int,
    val approvedStores: Int,
    val rejectedStores: Int
)