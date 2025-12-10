package com.shopply.appEcommerce.data.repository

import com.shopply.appEcommerce.data.local.dao.ProductDao
import com.shopply.appEcommerce.data.local.entities.Product
import com.shopply.appEcommerce.domain.model.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ProductRepository - Repositorio de productos
 *
 * Gestiona:
 * - Catálogo de productos (compradores)
 * - CRUD de productos (vendedores)
 * - Búsqueda y filtrado
 * - Gestión de inventario
 */
@Singleton
class ProductRepository @Inject constructor(
    private val productDao: ProductDao
) {

    // COMPRADOR: VER CATÁLOGO

    /**
     * Obtener todos los productos activos
     */
    fun getAllProducts(): Flow<List<Product>> = productDao.getAllActiveProducts()

    /**
     * Obtener producto por ID
     */
    fun getProductById(productId: Long): Flow<Product?> = productDao.getProductById(productId)

    /**
     * Obtener productos por categoría
     */
    fun getProductsByCategory(categoryId: Long): Flow<List<Product>> =
        productDao.getProductsByCategory(categoryId)

    /**
     * Buscar productos por nombre o descripción
     */
    fun searchProducts(query: String): Flow<List<Product>> = productDao.searchProducts(query)

    /**
     * Obtener productos nuevos
     */
    fun getNewProducts(limit: Int = 10): Flow<List<Product>> = productDao.getNewProducts(limit)

    // VENDEDOR: GESTIÓN DE PRODUCTOS

    /**
     * Obtener productos de una tienda específica
     */
    fun getProductsByStore(storeId: Long): Flow<List<Product>> =
        productDao.getProductsByStore(storeId)

    /**
     * Obtener productos con stock bajo (alerta para vendedor)
     */
    fun getLowStockProducts(storeId: Long): Flow<List<Product>> =
        productDao.getLowStockProducts(storeId)

    /**
     * Crear nuevo producto (Vendedor)
     */
    suspend fun createProduct(product: Product): Result<Long> {
        return try {
            // Validaciones
            when {
                product.name.isBlank() -> {
                    return Result.Error(Exception("El nombre del producto es requerido"))
                }
                product.description.isBlank() -> {
                    return Result.Error(Exception("La descripción es requerida"))
                }
                product.price <= 0 -> {
                    return Result.Error(Exception("El precio debe ser mayor a 0"))
                }
                product.stock < 0 -> {
                    return Result.Error(Exception("El stock no puede ser negativo"))
                }
            }

            val productId = productDao.insertProduct(product)
            Result.Success(productId)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Actualizar producto (Vendedor)
     */
    suspend fun updateProduct(product: Product): Result<Unit> {
        return try {
            productDao.updateProduct(product.copy(updatedAt = System.currentTimeMillis()))
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Eliminar producto (Vendedor)
     */
    suspend fun deleteProduct(product: Product): Result<Unit> {
        return try {
            productDao.deleteProduct(product)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Activar/Desactivar producto (Vendedor)
     * Permite ocultar productos sin eliminarlos
     */
    suspend fun toggleProductActive(productId: Long, isActive: Boolean): Result<Unit> {
        return try {
            productDao.setProductActive(productId, isActive)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // GESTIÓN DE INVENTARIO

    /**
     * Reducir stock de un producto
     * Usado al agregar al carrito o al realizar una compra
     */
    suspend fun reduceStock(productId: Long, quantity: Int): Result<Unit> {
        return try {
            val rowsAffected = productDao.decreaseStock(productId, quantity)
            if (rowsAffected > 0) {
                Result.Success(Unit)
            } else {
                Result.Error(Exception("Stock insuficiente"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Aumentar stock de un producto
     * Usado al cancelar una compra o al reabastecer
     */
    suspend fun increaseStock(productId: Long, quantity: Int): Result<Unit> {
        return try {
            productDao.increaseStock(productId, quantity)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // ESTADÍSTICAS

    /**
     * Contar productos de una tienda
     */
    suspend fun countProductsByStore(storeId: Long): Int {
        return productDao.countProductsByStore(storeId)
    }

    /**
     * Contar productos activos en la plataforma
     */
    suspend fun countActiveProducts(): Int {
        return productDao.countActiveProducts()
    }
}