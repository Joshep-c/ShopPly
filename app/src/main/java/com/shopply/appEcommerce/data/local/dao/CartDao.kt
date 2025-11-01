package com.shopply.appEcommerce.data.local.dao

import androidx.room.*
import com.shopply.appEcommerce.data.local.entities.CartItem
import kotlinx.coroutines.flow.Flow


 // CartDao - Acceso a datos del carrito de compras

@Dao
interface CartDao {

    // CONSULTAS

    @Query("SELECT * FROM cart_items WHERE userId = :userId ORDER BY addedAt DESC")
    fun getCartItems(userId: Long): Flow<List<CartItem>>

    @Query("SELECT * FROM cart_items WHERE userId = :userId AND productId = :productId")
    suspend fun getCartItem(userId: Long, productId: Long): CartItem?

    @Query("SELECT COUNT(*) FROM cart_items WHERE userId = :userId")
    fun getCartItemCount(userId: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM cart_items WHERE userId = :userId")
    suspend fun getCartItemCountSync(userId: Long): Int

    // OPERACIONES DE ESCRITURA

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItem)

    @Update
    suspend fun updateCartItem(item: CartItem)

    @Delete
    suspend fun deleteCartItem(item: CartItem)

    @Query("DELETE FROM cart_items WHERE userId = :userId")
    suspend fun clearCart(userId: Long)

    @Query("DELETE FROM cart_items WHERE userId = :userId AND productId = :productId")
    suspend fun removeFromCart(userId: Long, productId: Long)

    @Query("UPDATE cart_items SET quantity = :quantity WHERE userId = :userId AND productId = :productId")
    suspend fun updateQuantity(userId: Long, productId: Long, quantity: Int)

    // OPERACIONES COMPUESTAS

    
    // Agrega un producto al carrito o incrementa su cantidad si ya existe
    
    @Transaction
    suspend fun addOrUpdateCartItem(userId: Long, productId: Long, quantityToAdd: Int = 1) {
        val existingItem = getCartItem(userId, productId)
        if (existingItem != null) {
            updateQuantity(userId, productId, existingItem.quantity + quantityToAdd)
        } else {
            insertCartItem(CartItem(userId, productId, quantityToAdd))
        }
    }
}