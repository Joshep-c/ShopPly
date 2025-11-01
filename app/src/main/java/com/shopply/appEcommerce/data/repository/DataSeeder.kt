package com.shopply.appEcommerce.data.repository

import com.shopply.appEcommerce.data.local.entities.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DataSeeder - Poblador de datos iniciales
 * Ubicación: app/src/main/java/com/shopply/appEcommerce/data/repository/DataSeeder.kt
 *
 * Inserta datos de prueba para el prototipo:
 * - 1 Admin
 * - 2 Vendedores con tiendas
 * - 1 Comprador
 * - 8 Categorías
 * - 8 Productos de ejemplo
 */
@Singleton
class DataSeeder @Inject constructor(
    private val userRepository: UserRepository,
    private val storeRepository: StoreRepository,
    private val categoryRepository: CategoryRepository,
    private val productRepository: ProductRepository
) {

    /**
     * Poblar base de datos con datos iniciales
     * Solo se ejecuta si la BD está vacía
     */
    suspend fun seedInitialData() {
        // Verificar si ya hay datos
        val stats = userRepository.getUserStats()
        if (stats.totalUsers > 0) {
            return // Ya existen datos, no hacer nada
        }

        // 1. Crear usuarios de prueba
        createTestUsers()

        // 2. Crear categorías
        categoryRepository.insertDefaultCategories()

        // 3. Crear tiendas
        createTestStores()

        // 4. Crear productos
        createTestProducts()
    }

    /**
     * Crear usuarios de prueba
     */
    private suspend fun createTestUsers() {
        // ADMIN
        userRepository.register(
            email = "admin@shopply.pe",
            name = "Admin ShopPly",
            password = "admin123",
            phone = "987654321",
            userRole = UserRole.ADMIN
        )

        // VENDEDOR 1 - Tech Arequipa
        userRepository.register(
            email = "tech@arequipa.pe",
            name = "Juan Pérez",
            password = "seller123",
            phone = "987111222",
            userRole = UserRole.SELLER
        )

        // VENDEDOR 2 - Artesanía Cusco
        userRepository.register(
            email = "artesania@cusco.pe",
            name = "María Rodriguez",
            password = "seller123",
            phone = "987333444",
            userRole = UserRole.SELLER
        )

        // COMPRADOR
        userRepository.register(
            email = "comprador@gmail.com",
            name = "Carlos Mendoza",
            password = "buyer123",
            phone = "987555666",
            userRole = UserRole.BUYER
        )
    }

    /**
     * Crear tiendas de prueba
     */
    private suspend fun createTestStores() {
        // Tienda 1 - Tech Arequipa (APROBADA)
        val techStore = Store(
            ownerId = 2, // Juan Pérez
            name = "Tech Arequipa",
            description = "Tecnología y electrónica de última generación para todo el Perú",
            ruc = "20123456789",
            phone = "987111222",
            status = StoreStatus.APPROVED,
            rating = 4.5f,
            createdAt = System.currentTimeMillis(),
            approvedAt = System.currentTimeMillis()
        )
        storeRepository.createStore(techStore)

        // Tienda 2 - Artesanía Cusco (APROBADA)
        val artStore = Store(
            ownerId = 3, // María Rodriguez
            name = "Artesanía Cusqueña",
            description = "Productos artesanales 100% peruanos, hechos a mano en Cusco",
            ruc = "20987654321",
            phone = "987333444",
            status = StoreStatus.APPROVED,
            rating = 4.8f,
            createdAt = System.currentTimeMillis(),
            approvedAt = System.currentTimeMillis()
        )
        storeRepository.createStore(artStore)
    }

    /**
     * Crear productos de prueba
     */
    private suspend fun createTestProducts() {
        val products = listOf(
            // PRODUCTOS DE TECH AREQUIPA (storeId = 1)
            Product(
                storeId = 1,
                categoryId = 1, // Electrónica
                name = "Laptop HP Pavilion 15",
                description = "Laptop con procesador Intel Core i5 11va Gen, 8GB RAM DDR4, 512GB SSD, " +
                        "Pantalla 15.6\" Full HD, Windows 11. Ideal para trabajo y estudios.",
                price = 2499.00,
                stock = 15,
                imageUrl = null,
                isActive = true
            ),
            Product(
                storeId = 1,
                categoryId = 1, // Electrónica
                name = "Mouse Logitech MX Master 3",
                description = "Mouse ergonómico inalámbrico con precisión avanzada, " +
                        "sensor de 4000 DPI, batería recargable USB-C, compatible con Windows y Mac.",
                price = 299.00,
                stock = 30,
                imageUrl = null,
                isActive = true
            ),
            Product(
                storeId = 1,
                categoryId = 1, // Electrónica
                name = "Teclado Mecánico RGB Redragon",
                description = "Teclado gaming con switches mecánicos rojos, iluminación RGB " +
                        "personalizable, 104 teclas, anti-ghosting, cable USB trenzado.",
                price = 189.00,
                stock = 20,
                imageUrl = null,
                isActive = true
            ),
            Product(
                storeId = 1,
                categoryId = 1, // Electrónica
                name = "Audífonos Bluetooth Sony WH-1000XM4",
                description = "Audífonos over-ear con cancelación activa de ruido, " +
                        "batería de 30 horas, carga rápida, audio Hi-Res, asistente de voz.",
                price = 899.00,
                stock = 12,
                imageUrl = null,
                isActive = true
            ),

            // PRODUCTOS DE ARTESANÍA CUSQUEÑA (storeId = 2)
            Product(
                storeId = 2,
                categoryId = 5, // Artesanía
                name = "Chompa de Alpaca Natural",
                description = "Chompa tejida a mano con lana de alpaca 100% natural. " +
                        "Diseños tradicionales andinos, suave y abrigadora. Tallas disponibles: S, M, L, XL.",
                price = 250.00,
                stock = 12,
                imageUrl = null,
                isActive = true
            ),
            Product(
                storeId = 2,
                categoryId = 5, // Artesanía
                name = "Chullo Cusqueño Tradicional",
                description = "Gorro tradicional peruano tejido a mano con diseños geométricos andinos. " +
                        "Lana de oveja y alpaca, colores vivos, orejeras incluidas.",
                price = 45.00,
                stock = 50,
                imageUrl = null,
                isActive = true
            ),
            Product(
                storeId = 2,
                categoryId = 4, // Alimentos
                name = "Café Orgánico de Altura 500g",
                description = "Café premium del Valle de La Convención - Cusco. " +
                        "Grano selecto, tueste medio, cultivo orgánico, notas achocolatadas y frutales.",
                price = 38.00,
                stock = 100,
                imageUrl = null,
                isActive = true
            ),
            Product(
                storeId = 2,
                categoryId = 5, // Artesanía
                name = "Manta Andina Multicolor",
                description = "Manta tejida con diseños tradicionales incas, 100% lana de alpaca. " +
                        "Medidas: 1.5m x 2m. Perfecta para decoración o abrigo. Hecha en telar tradicional.",
                price = 180.00,
                stock = 8,
                imageUrl = null,
                isActive = true
            )
        )

        products.forEach { product ->
            productRepository.createProduct(product)
        }
    }

    /**
     * Limpiar todos los datos (útil para testing)
     */
    suspend fun clearAllData() {
        // Nota: En el MVP no implementamos esta funcionalidad
        // Se puede agregar después si es necesario
    }
}