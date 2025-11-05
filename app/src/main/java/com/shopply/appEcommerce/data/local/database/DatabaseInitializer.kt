package com.shopply.appEcommerce.data.local.database

import android.util.Log
import com.shopply.appEcommerce.data.repository.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DatabaseInitializer - Inicializador y verificador de base de datos
 * Ubicación: app/src/main/java/com/shopply/appEcommerce/data/local/database/DatabaseInitializer.kt
 *
 * Funciones:
 * - Poblar datos iniciales
 * - Verificar integridad de la BD
 * - Logging para debugging
 */
@Singleton
class DatabaseInitializer @Inject constructor(
    private val dataSeeder: DataSeeder,
    private val userRepository: UserRepository,
    private val storeRepository: StoreRepository,
    private val categoryRepository: CategoryRepository,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) {
    private val TAG = "DatabaseInitializer"

    /**
     * Inicializar base de datos con datos de prueba
     * Se ejecuta automáticamente al iniciar la app
     */
    suspend fun initialize() {
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "🔵 Iniciando inicialización de base de datos...")

                // Poblar datos iniciales
                dataSeeder.seedInitialData()

                // Verificar datos
                verifyData()

                Log.d(TAG, "✅ Base de datos inicializada correctamente")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al inicializar base de datos: ${e.message}", e)
            }
        }
    }

    /**
     * Verificar que los datos se insertaron correctamente
     */
    private suspend fun verifyData() {
        Log.d(TAG, "📊 Verificando datos insertados...")

        // Verificar usuarios
        val userStats = userRepository.getUserStats()
        Log.d(TAG, """
            👥 USUARIOS:
            - Total: ${userStats.totalUsers}
            - Compradores: ${userStats.totalBuyers}
            - Vendedores: ${userStats.totalSellers}
            - Admins: ${userStats.totalAdmins}
        """.trimIndent())

        // Verificar tiendas
        val storeStats = storeRepository.getStoreStats()
        Log.d(TAG, """
            🏪 TIENDAS:
            - Total: ${storeStats.totalStores}
            - Aprobadas: ${storeStats.approvedStores}
            - Pendientes: ${storeStats.pendingStores}
            - Rechazadas: ${storeStats.rejectedStores}
        """.trimIndent())

        // Verificar categorías
        val categories = categoryRepository.getAllCategories().first()
        Log.d(TAG, "📂 CATEGORÍAS: ${categories.size} categorías activas")
        categories.forEach {
            Log.d(TAG, "   - ${it.name}")
        }

        // Verificar productos
        val products = productRepository.getAllProducts().first()
        Log.d(TAG, "📦 PRODUCTOS: ${products.size} productos activos")
        products.take(3).forEach {
            Log.d(TAG, "   - ${it.name} (S/ ${it.price})")
        }

        // Verificar tiendas aprobadas
        val approvedStores = storeRepository.getApprovedStores().first()
        Log.d(TAG, "✅ TIENDAS APROBADAS:")
        approvedStores.forEach { store ->
            val productCount = productRepository.countProductsByStore(store.id)
            Log.d(TAG, "   - ${store.name} (${productCount} productos, Rating: ${store.rating}⭐)")
        }
    }

    /**
     * Test completo de la base de datos
     * Prueba operaciones CRUD en todas las entidades
     */
    suspend fun runFullTest() {
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "🧪 ========== INICIANDO TEST COMPLETO ==========")

                testUsers()
                testStores()
                testCategories()
                testProducts()
                testCart()

                Log.d(TAG, "✅ ========== TEST COMPLETO EXITOSO ==========")
            } catch (e: Exception) {
                Log.e(TAG, "❌ ========== TEST FALLÓ: ${e.message} ==========", e)
            }
        }
    }

    private suspend fun testUsers() {
        Log.d(TAG, "🧪 Test: Usuarios")

        // Obtener todos los usuarios
        val users = userRepository.getAllUsers().first()
        Log.d(TAG, "✓ Total usuarios: ${users.size}")

        // Buscar usuario específico
        val admin = users.find { it.email == "admin@shopply.pe" }
        if (admin != null) {
            Log.d(TAG, "✓ Admin encontrado: ${admin.name} (${admin.userRole})")
        }
    }

    private suspend fun testStores() {
        Log.d(TAG, "🧪 Test: Tiendas")

        val stores = storeRepository.getApprovedStores().first()
        Log.d(TAG, "✓ Tiendas aprobadas: ${stores.size}")

        stores.forEach { store ->
            Log.d(TAG, "  - ${store.name} (RUC: ${store.ruc})")
        }
    }

    private suspend fun testCategories() {
        Log.d(TAG, "🧪 Test: Categorías")

        val categories = categoryRepository.getAllCategories().first()
        Log.d(TAG, "✓ Categorías activas: ${categories.size}")
    }

    private suspend fun testProducts() {
        Log.d(TAG, "🧪 Test: Productos")

        // Productos activos
        val products = productRepository.getAllProducts().first()
        Log.d(TAG, "✓ Productos activos: ${products.size}")

        // Buscar por categoría (Electrónica = 1)
        val electronics = productRepository.getProductsByCategory(1).first()
        Log.d(TAG, "✓ Productos en Electrónica: ${electronics.size}")

        // Búsqueda
        val searchResults = productRepository.searchProducts("laptop").first()
        Log.d(TAG, "✓ Búsqueda 'laptop': ${searchResults.size} resultados")
    }

    private suspend fun testCart() {
        Log.d(TAG, "🧪 Test: Carrito")

        // Agregar producto al carrito del comprador (userId = 4)
        val result = cartRepository.addToCart(userId = 4, productId = 1, quantity = 2)

        if (result is com.shopply.appEcommerce.domain.model.Result.Success) {
            Log.d(TAG, "✓ Producto agregado al carrito")

            // Verificar carrito
            val cartItems = cartRepository.getCartItems(4).first()
            Log.d(TAG, "✓ Items en carrito: ${cartItems.size}")

            // Limpiar carrito
            cartRepository.clearCart(4)
            Log.d(TAG, "✓ Carrito limpiado")
        } else {
            Log.e(TAG, "✗ Error al agregar al carrito")
        }
    }

    /**
     * Mostrar resumen de la base de datos
     */
    suspend fun showDatabaseSummary() {
        withContext(Dispatchers.IO) {
            Log.d(TAG, """
                
                ╔════════════════════════════════════════╗
                ║     📊 RESUMEN BASE DE DATOS          ║
                ╚════════════════════════════════════════╝
            """.trimIndent())

            verifyData()

            Log.d(TAG, "╚════════════════════════════════════════╝")
        }
    }
}