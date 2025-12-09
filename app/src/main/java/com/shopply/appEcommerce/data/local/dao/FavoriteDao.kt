package com.shopply.appEcommerce.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shopply.appEcommerce.data.local.entities.Favorite
import kotlinx.coroutines.flow.Flow

/**
 * FavoriteDao - Acceso a datos de favoritos
 */
@Dao
interface FavoriteDao {

    /**
     * Obtener todos los favoritos de un usuario
     */
    @Query("SELECT * FROM favorites WHERE userId = :userId ORDER BY addedAt DESC")
    fun getFavorites(userId: Long): Flow<List<Favorite>>

    /**
     * Verificar si un producto es favorito
     */
    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE userId = :userId AND productId = :productId)")
    fun isFavorite(userId: Long, productId: Long): Flow<Boolean>

    /**
     * Verificar si un producto es favorito (sincrónico)
     */
    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE userId = :userId AND productId = :productId)")
    suspend fun isFavoriteSync(userId: Long, productId: Long): Boolean

    /**
     * Agregar producto a favoritos
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavorite(favorite: Favorite)

    /**
     * Eliminar producto de favoritos
     */
    @Query("DELETE FROM favorites WHERE userId = :userId AND productId = :productId")
    suspend fun removeFavorite(userId: Long, productId: Long)

    /**
     * Obtener cantidad de favoritos
     */
    @Query("SELECT COUNT(*) FROM favorites WHERE userId = :userId")
    fun getFavoriteCount(userId: Long): Flow<Int>

    /**
     * Limpiar todos los favoritos de un usuario
     */
    @Query("DELETE FROM favorites WHERE userId = :userId")
    suspend fun clearFavorites(userId: Long)

    /**
     * Obtener IDs de productos favoritos
     */
    @Query("SELECT productId FROM favorites WHERE userId = :userId")
    fun getFavoriteProductIds(userId: Long): Flow<List<Long>>
}

