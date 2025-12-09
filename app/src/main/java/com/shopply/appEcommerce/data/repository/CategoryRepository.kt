package com.shopply.appEcommerce.data.repository

import com.shopply.appEcommerce.data.local.dao.CategoryDao
import com.shopply.appEcommerce.data.local.entities.Category
import com.shopply.appEcommerce.domain.model.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * CategoryRepository - Repositorio de categorías
 *
 * Gestiona:
 * - Consulta de categorías (todos los usuarios)
 * - CRUD de categorías (solo Admin)
 * - Categorías predefinidas
 */
@Singleton
class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao
) {

    // CONSULTAS (TODOS LOS USUARIOS)

    /**
     * Obtener todas las categorías activas
     */
    fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllActiveCategories()

    /**
     * Obtener categoría por ID
     */
    fun getCategoryById(categoryId: Long): Flow<Category?> = categoryDao.getCategoryById(categoryId)

    // ADMIN: GESTIÓN DE CATEGORÍAS

    /**
     * Crear nueva categoría (Admin)
     */
    suspend fun createCategory(category: Category): Result<Long> {
        return try {
            if (category.name.isBlank()) {
                return Result.Error(Exception("El nombre de la categoría es requerido"))
            }

            val categoryId = categoryDao.insertCategory(category)
            Result.Success(categoryId)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Actualizar categoría (Admin)
     */
    suspend fun updateCategory(category: Category): Result<Unit> {
        return try {
            categoryDao.updateCategory(category)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Eliminar categoría (Admin)
     */
    suspend fun deleteCategory(category: Category): Result<Unit> {
        return try {
            categoryDao.deleteCategory(category)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Activar/Desactivar categoría (Admin)
     */
    suspend fun toggleCategoryActive(categoryId: Long, isActive: Boolean): Result<Unit> {
        return try {
            categoryDao.setCategoryActive(categoryId, isActive)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Obtener todas las categorías (incluidas inactivas) - Admin
     */
    fun getAllCategoriesIncludingInactive(): Flow<List<Category>> = categoryDao.getAllCategories()

    // DATOS INICIALES

    /**
     * Insertar categorías predefinidas
     * Se ejecuta al iniciar la app por primera vez
     */
    suspend fun insertDefaultCategories() {
        val count = categoryDao.countActiveCategories()
        if (count > 0) return

        val defaultCategories = listOf(
            Category(
                name = "Electrónica",
                description = "Productos electrónicos y tecnología",
                displayOrder = 1
            ),
            Category(
                name = "Moda y Ropa",
                description = "Ropa, calzado y accesorios",
                displayOrder = 2
            ),
            Category(
                name = "Hogar y Cocina",
                description = "Artículos para el hogar",
                displayOrder = 3
            ),
            Category(
                name = "Alimentos",
                description = "Productos alimenticios y bebidas",
                displayOrder = 4
            ),
            Category(
                name = "Artesanía",
                description = "Productos artesanales peruanos",
                displayOrder = 5
            ),
            Category(
                name = "Salud y Belleza",
                description = "Cuidado personal y cosméticos",
                displayOrder = 6
            ),
            Category(
                name = "Deportes",
                description = "Artículos deportivos y outdoor",
                displayOrder = 7
            ),
            Category(
                name = "Libros",
                description = "Libros y material educativo",
                displayOrder = 8
            )
        )

        categoryDao.insertCategories(defaultCategories)
    }
}