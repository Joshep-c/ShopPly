package com.shopply.appEcommerce.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.shopply.appEcommerce.data.local.entities.Store
import com.shopply.appEcommerce.data.local.entities.StoreStatus
import kotlinx.coroutines.flow.Flow


// StoreDao - Acceso a datos de tiendas

@Dao
interface StoreDao {

    // CONSULTAS BÁSICAS

    @Query("SELECT * FROM stores WHERE id = :storeId")
    fun getStoreById(storeId: Long): Flow<Store?>

    @Query("SELECT * FROM stores WHERE ownerId = :ownerId LIMIT 1")
    fun getStoreByOwnerId(ownerId: Long): Flow<Store?>

    @Query("SELECT * FROM stores WHERE ownerId = :ownerId LIMIT 1")
    suspend fun getStoreByOwnerIdSync(ownerId: Long): Store?

    @Query("SELECT * FROM stores WHERE status = :status ORDER BY createdAt DESC")
    fun getStoresByStatus(status: StoreStatus): Flow<List<Store>>

    @Query("SELECT * FROM stores WHERE status = 'APPROVED' ORDER BY rating DESC")
    fun getApprovedStores(): Flow<List<Store>>

    @Query("""
        SELECT * FROM stores 
        WHERE status = 'APPROVED' 
        AND (name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%')
        ORDER BY rating DESC
    """)
    fun searchStores(query: String): Flow<List<Store>>

    // OPERACIONES DE ESCRITURA

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStore(store: Store): Long

    @Update
    suspend fun updateStore(store: Store)

    @Delete
    suspend fun deleteStore(store: Store)

    // ADMIN: APROBACIÓN DE TIENDAS

    @Query("""
        UPDATE stores 
        SET status = :status, 
            approvedAt = :approvedAt,
            rejectionReason = :rejectionReason
        WHERE id = :storeId
    """)
    suspend fun updateStoreStatus(
        storeId: Long,
        status: StoreStatus,
        approvedAt: Long? = null,
        rejectionReason: String? = null
    )

    @Query("UPDATE stores SET rating = :rating WHERE id = :storeId")
    suspend fun updateStoreRating(storeId: Long, rating: Float)

    @Query("SELECT COUNT(*) FROM stores WHERE status = :status")
    suspend fun countStoresByStatus(status: StoreStatus): Int
}