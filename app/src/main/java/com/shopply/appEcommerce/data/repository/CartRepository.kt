package com.shopply.appEcommerce.data.repository

import com.shopply.appEcommerce.data.local.dao.CartDao
import com.shopply.appEcommerce.data.local.dao.ProductDao
import com.shopply.appEcommerce.data.local.entities.CartItem
import com.shopply.appEcommerce.domain.model.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


// CartRepository - Repositorio del carrito de compras

// Gestiona:
// - Items del carrito persistente
// - Agregar/quitar productos
// - Actualizar cantidades
// - Validaciones de stock

@Singleton
class CartRepository @Inject constructor(
    private val cartDao: CartDao,
    private val productDao: ProductDao
) {

    //CONSULTAS
    // Obtener items del carrito del usuario

    fun getCartItems(userId: Long): Flow<List<CartItem>> = cartDao.getCartItems(userId)

    // Obtener cantidad de items en el carrito

    fun getCartItemCount(userId: Long): Flow<Int> = cartDao.getCartItemCount(userId)

    // Obtener cantidad de items en el carrito (sincrónico)

    suspend fun getCartItemCountSync(userId: Long): Int = cartDao.getCartItemCountSync(userId)

    // AGREGAR AL CARRITO

    // Agregar producto al carrito
    // Si ya existe, incrementa la cantidad

    suspend fun addToCart(userId: Long, productId: Long, quantity: Int = 1): Result<Unit> {
        return try {
            // Validar cantidad
            if (quantity <= 0) {
                return Result.Error(Exception("La cantidad debe ser mayor a 0"))
            }

            // Verificar que el producto existe y está disponible
            val product = productDao.getProductByIdSync(productId)
            when {
                product == null -> {
                    return Result.Error(Exception("Producto no encontrado"))
                }
                !product.isActive -> {
                    return Result.Error(Exception("Este producto no está disponible"))
                }
                product.stock <= 0 -> {
                    return Result.Error(Exception("Producto sin stock"))
                }
            }

            // Verificar stock disponible considerando lo que ya está en el carrito
            val existingItem = cartDao.getCartItem(userId, productId)
            val totalQuantity = (existingItem?.quantity ?: 0) + quantity

            if (totalQuantity > product.stock) {
                return Result.Error(Exception("Stock insuficiente. Disponible: ${product.stock}"))
            }

            // Agregar o actualizar
            cartDao.addOrUpdateCartItem(userId, productId, quantity)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // ACTUALIZAR CANTIDAD
    // Actualizar cantidad de un item en el carrito
    // Si quantity = 0, elimina el item

    suspend fun updateQuantity(userId: Long, productId: Long, quantity: Int): Result<Unit> {
        return try {
            if (quantity < 0) {
                return Result.Error(Exception("La cantidad no puede ser negativa"))
            }

            // Si es 0, eliminar del carrito
            if (quantity == 0) {
                return removeFromCart(userId, productId)
            }

            // Verificar stock disponible
            val product = productDao.getProductByIdSync(productId)
            if (product != null && quantity > product.stock) {
                return Result.Error(Exception("Stock insuficiente. Disponible: ${product.stock}"))
            }

            cartDao.updateQuantity(userId, productId, quantity)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // ELIMINAR DEL CARRITO
    // Eliminar un producto del carrito

    suspend fun removeFromCart(userId: Long, productId: Long): Result<Unit> {
        return try {
            cartDao.removeFromCart(userId, productId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // Vaciar todo el carrito

    suspend fun clearCart(userId: Long): Result<Unit> {
        return try {
            cartDao.clearCart(userId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // VALIDACIONES
    // Verificar si todos los items del carrito tienen stock disponible

    suspend fun validateCartStock(userId: Long): Result<List<String>> {
        return try {
            val items = mutableListOf<CartItem>()
            cartDao.getCartItems(userId).collect { items.addAll(it) }

            val unavailableItems = mutableListOf<String>()

            for (item in items) {
                val product = productDao.getProductByIdSync(item.productId)
                when {
                    product == null -> {
                        unavailableItems.add("Producto no encontrado")
                    }
                    !product.isActive -> {
                        unavailableItems.add("${product.name} ya no está disponible")
                    }
                    product.stock < item.quantity -> {
                        unavailableItems.add("${product.name}: stock insuficiente")
                    }
                }
            }

            if (unavailableItems.isEmpty()) {
                Result.Success(emptyList())
            } else {
                Result.Success(unavailableItems)
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}