package com.shopply.appEcommerce.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.shopply.appEcommerce.data.local.entities.Product
import kotlinx.coroutines.flow.Flow


// ProductDao - Acceso a datos de productos

@Dao
interface ProductDao {

    // CONSULTAS PARA COMPRADORES

    @Query("SELECT * FROM products WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getAllActiveProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :productId")
    fun getProductById(productId: Long): Flow<Product?>

    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductByIdSync(productId: Long): Product?

    @Query("SELECT * FROM products WHERE categoryId = :categoryId AND isActive = 1 ORDER BY createdAt DESC")
    fun getProductsByCategory(categoryId: Long): Flow<List<Product>>

    @Query("""
        SELECT * FROM products 
        WHERE (name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%')
        AND isActive = 1
        ORDER BY createdAt DESC
    """)
    fun searchProducts(query: String): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE isActive = 1 ORDER BY createdAt DESC LIMIT :limit")
    fun getNewProducts(limit: Int = 10): Flow<List<Product>>

    // CONSULTAS PARA VENDEDORES

    @Query("SELECT * FROM products WHERE storeId = :storeId ORDER BY createdAt DESC")
    fun getProductsByStore(storeId: Long): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE storeId = :storeId AND stock < 5")
    fun getLowStockProducts(storeId: Long): Flow<List<Product>>

    // OPERACIONES DE ESCRITURA

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<Product>)

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    // GESTIÓN DE INVENTARIO

    @Query("UPDATE products SET stock = stock - :quantity WHERE id = :productId AND stock >= :quantity")
    suspend fun decreaseStock(productId: Long, quantity: Int): Int

    @Query("UPDATE products SET stock = stock + :quantity WHERE id = :productId")
    suspend fun increaseStock(productId: Long, quantity: Int)

    @Query("UPDATE products SET isActive = :isActive, updatedAt = :timestamp WHERE id = :productId")
    suspend fun setProductActive(
        productId: Long,
        isActive: Boolean,
        timestamp: Long = System.currentTimeMillis()
    )

    // ESTADÍSTICAS

    @Query("SELECT COUNT(*) FROM products WHERE storeId = :storeId")
    suspend fun countProductsByStore(storeId: Long): Int

    @Query("SELECT COUNT(*) FROM products WHERE isActive = 1")
    suspend fun countActiveProducts(): Int
}