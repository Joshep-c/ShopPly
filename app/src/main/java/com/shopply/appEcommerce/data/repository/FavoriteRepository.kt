package com.shopply.appEcommerce.data.repository

import com.shopply.appEcommerce.data.local.dao.FavoriteDao
import com.shopply.appEcommerce.data.local.dao.ProductDao
import com.shopply.appEcommerce.data.local.entities.Favorite
import com.shopply.appEcommerce.data.local.entities.Product
import com.shopply.appEcommerce.domain.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

/**
 * FavoriteRepository - Repositorio de favoritos
 *
 * Gestiona:
 * - Lista de favoritos del usuario
 * - Agregar/quitar favoritos
 * - Verificar si un producto es favorito
 */
@Singleton
class FavoriteRepository @Inject constructor(
    private val favoriteDao: FavoriteDao,
    private val productDao: ProductDao
) {

    /**
     * Obtener productos favoritos del usuario con sus detalles
     */
    fun getFavoriteProducts(userId: Long): Flow<List<Product>> {
        return combine(
            favoriteDao.getFavorites(userId),
            productDao.getAllProductsFlow()
        ) { favorites, allProducts ->
            val favoriteProductIds = favorites.map { it.productId }.toSet()
            allProducts.filter { it.id in favoriteProductIds }
        }
    }

    /**
     * Verificar si un producto es favorito
     */
    fun isFavorite(userId: Long, productId: Long): Flow<Boolean> {
        return favoriteDao.isFavorite(userId, productId)
    }

    /**
     * Agregar producto a favoritos
     */
    suspend fun addToFavorites(userId: Long, productId: Long): Result<Unit> {
        return try {
            // Verificar que el producto existe
            val product = productDao.getProductByIdSync(productId)
            if (product == null) {
                return Result.Error(Exception("Producto no encontrado"))
            }

            favoriteDao.addFavorite(
                Favorite(
                    userId = userId,
                    productId = productId
                )
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Eliminar producto de favoritos
     */
    suspend fun removeFromFavorites(userId: Long, productId: Long): Result<Unit> {
        return try {
            favoriteDao.removeFavorite(userId, productId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Toggle favorito (agregar si no existe, quitar si existe)
     */
    suspend fun toggleFavorite(userId: Long, productId: Long): Result<Boolean> {
        return try {
            val isFavorite = favoriteDao.isFavoriteSync(userId, productId)
            if (isFavorite) {
                favoriteDao.removeFavorite(userId, productId)
                Result.Success(false) // Ya no es favorito
            } else {
                favoriteDao.addFavorite(
                    Favorite(
                        userId = userId,
                        productId = productId
                    )
                )
                Result.Success(true) // Ahora es favorito
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Obtener cantidad de favoritos
     */
    fun getFavoriteCount(userId: Long): Flow<Int> {
        return favoriteDao.getFavoriteCount(userId)
    }

    /**
     * Limpiar todos los favoritos
     */
    suspend fun clearFavorites(userId: Long): Result<Unit> {
        return try {
            favoriteDao.clearFavorites(userId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

