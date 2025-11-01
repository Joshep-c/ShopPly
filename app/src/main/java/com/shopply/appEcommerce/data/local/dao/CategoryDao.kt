package com.shopply.appEcommerce.data.local.dao

import androidx.room.*
import com.shopply.appEcommerce.data.local.entities.Category
import kotlinx.coroutines.flow.Flow


 // CategoryDao - Acceso a datos de categorías

@Dao
interface CategoryDao {

    // CONSULTAS BÁSICAS

    @Query("SELECT * FROM categories WHERE isActive = 1 ORDER BY displayOrder ASC")
    fun getAllActiveCategories(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    fun getCategoryById(categoryId: Long): Flow<Category?>

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    suspend fun getCategoryByIdSync(categoryId: Long): Category?

    @Query("SELECT * FROM categories ORDER BY displayOrder ASC")
    fun getAllCategories(): Flow<List<Category>>

    // OPERACIONES DE ESCRITURA

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<Category>)

    @Update
    suspend fun updateCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    // ADMIN: GESTIÓN

    @Query("UPDATE categories SET isActive = :isActive WHERE id = :categoryId")
    suspend fun setCategoryActive(categoryId: Long, isActive: Boolean)

    @Query("SELECT COUNT(*) FROM categories WHERE isActive = 1")
    suspend fun countActiveCategories(): Int
}