package com.shopply.appEcommerce.data.repository

import com.shopply.appEcommerce.data.local.entities.Product
import com.shopply.appEcommerce.data.local.entities.Store
import com.shopply.appEcommerce.data.local.entities.StoreStatus
import com.shopply.appEcommerce.data.local.entities.UserRole
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DataSeeder - Poblador de datos iniciales
 *
 * Inserta datos de prueba para el prototipo:
 * - 1 Admin
 * - 2 Vendedores con tiendas
 * - 1 Comprador
 * - 8 Categorías
 * - 20 Productos de ejemplo
 * - Favoritos de prueba
 */
@Singleton
class DataSeeder @Inject constructor(
    private val userRepository: UserRepository,
    private val storeRepository: StoreRepository,
    private val categoryRepository: CategoryRepository,
    private val productRepository: ProductRepository,
    private val favoriteRepository: FavoriteRepository
) {

    /**
     * Poblar base de datos con datos iniciales
     * Solo si la BD está vacía
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

        // 5. Crear favoritos de prueba
        createTestFavorites()
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
        val techStoreResult = storeRepository.createStore(
            Store(
                ownerId = 2, // Juan Pérez
                name = "Tech Arequipa",
                description = "Tecnología y electrónica de última generación para todo el Perú",
                ruc = "20123456789",
                phone = "987111222",
                status = StoreStatus.PENDING, // Se crea como pending
                rating = 4.5f,
                createdAt = System.currentTimeMillis()
            )
        )

        // Aprobar la tienda inmediatamente (simular aprobación del admin)
        if (techStoreResult is com.shopply.appEcommerce.domain.model.Result.Success) {
            storeRepository.approveStore(techStoreResult.data)
        }

        // Tienda 2 - Artesanía Arequipa (APROBADA)
        val artStoreResult = storeRepository.createStore(
            Store(
                ownerId = 3, // María Rodriguez
                name = "Artesanía Arequipeña",
                description = "Productos artesanales 100% peruanos, hechos a mano en Arequipa",
                ruc = "20987654321",
                phone = "987333444",
                status = StoreStatus.PENDING,
                rating = 4.8f,
                createdAt = System.currentTimeMillis()
            )
        )

        // Aprobar la tienda inmediatamente
        if (artStoreResult is com.shopply.appEcommerce.domain.model.Result.Success) {
            storeRepository.approveStore(artStoreResult.data)
        }
    }

    /**
     * Crear productos de prueba
     *
     * NOTA: Todos usan URLs de Unsplash (externas)
     * Esto permite:
     * - Descarga más rápida del APK
     * - Menor tamaño de la app
     */
    private suspend fun createTestProducts() {
        val products = listOf(
            // TECH AREQUIPA (storeId = 1) - ELECTRÓNICA

            Product(
                storeId = 1,
                categoryId = 1, // Electrónica
                name = "Laptop HP Pavilion 15",
                description = "Laptop con procesador Intel Core i5 11va Gen, 8GB RAM DDR4, 512GB SSD, " +
                        "Pantalla 15.6\" Full HD, Windows 11. Ideal para trabajo y estudios.",
                price = 2499.00,
                stock = 15,
                imageUrl = "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=500&h=500&fit=crop",
                isActive = true
            ),
            Product(
                storeId = 1,
                categoryId = 1, // Electrónica
                name = "MacBook Air M2 2023",
                description = "MacBook Air con chip M2, 8GB RAM unificada, 256GB SSD, " +
                        "pantalla Retina 13.6\", batería 18 horas. Ultraligero 1.24kg.",
                price = 4299.00,
                stock = 8,
                imageUrl = "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=500&h=500&fit=crop",
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
                imageUrl = "https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=500&h=500&fit=crop",
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
                imageUrl = "https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=500&h=500&fit=crop",
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
                imageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500&h=500&fit=crop",
                isActive = true
            ),
            Product(
                storeId = 1,
                categoryId = 1, // Electrónica
                name = "Webcam Logitech C920 Full HD",
                description = "Cámara web 1080p a 30fps, enfoque automático, " +
                        "corrección de luz, micrófono estéreo. Ideal para videollamadas y streaming.",
                price = 249.00,
                stock = 25,
                imageUrl = "https://promart.vteximg.com.br/arquivos/ids/6526436-1000-1000/image-006b18b462b742ea8e7742b6247dff7c.jpg?v=637989425151500000",
                isActive = true
            ),
            Product(
                storeId = 1,
                categoryId = 1, // Electrónica
                name = "Monitor LG UltraWide 29\"",
                description = "Monitor IPS 29\" 2560x1080, FreeSync 75Hz, " +
                        "HDR10, entrada HDMI/DisplayPort. Perfecto para productividad.",
                price = 899.00,
                stock = 10,
                imageUrl = "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=500&h=500&fit=crop",
                isActive = true
            ),
            Product(
                storeId = 1,
                categoryId = 1, // Electrónica
                name = "Tablet Samsung Galaxy Tab S8",
                description = "Tablet Android 11\", pantalla AMOLED 120Hz, " +
                        "S Pen incluido, 8GB RAM, 128GB. Ideal para diseño y entretenimiento.",
                price = 1899.00,
                stock = 15,
                imageUrl = "https://images.unsplash.com/photo-1561154464-82e9adf32764?w=500&h=500&fit=crop",
                isActive = true
            ),
            Product(
                storeId = 1,
                categoryId = 1, // Electrónica
                name = "Smartwatch Apple Watch Series 8",
                description = "Reloj inteligente con GPS, monitor de salud, " +
                        "pantalla always-on, resistente al agua, batería 18 horas.",
                price = 1699.00,
                stock = 20,
                imageUrl = "https://images.unsplash.com/photo-1579586337278-3befd40fd17a?w=500&h=500&fit=crop",
                isActive = true
            ),
            Product(
                storeId = 1,
                categoryId = 1, // Electrónica
                name = "Disco SSD Kingston 1TB NVMe",
                description = "SSD M.2 NVMe Gen 3, velocidad lectura 3500MB/s, " +
                        "escritura 2900MB/s. Acelera tu PC hasta 10x más rápido.",
                price = 329.00,
                stock = 40,
                imageUrl = "https://images.unsplash.com/photo-1597872200969-2b65d56bd16b?w=500&h=500&fit=crop",
                isActive = true
            ),

            // ========== ARTESANÍA AREQUIPEÑA (storeId = 2) ==========

            Product(
                storeId = 2,
                categoryId = 5, // Artesanía
                name = "Chompa de Alpaca Natural",
                description = "Chompa tejida a mano con lana de alpaca 100% natural. " +
                        "Diseños tradicionales andinos, suave y abrigadora. Tallas: S, M, L, XL.",
                price = 250.00,
                stock = 12,
                imageUrl = "https://images.unsplash.com/photo-1434389677669-e08b4cac3105?w=500&h=500&fit=crop",
                isActive = true
            ),
            Product(
                storeId = 2,
                categoryId = 5, // Artesanía
                name = "Sombrero Arequipeño Tradicional",
                description = "Sombrero tradicional peruano tejido con fibras vegetales. " +
                        "Lana de oveja y alpaca, colores vivos, orejeras incluidas.",
                price = 45.00,
                stock = 50,
                imageUrl = "https://images.unsplash.com/photo-1529958030586-3aae4ca485ff?w=500&h=500&fit=crop",
                isActive = true
            ),
            Product(
                storeId = 2,
                categoryId = 5, // Artesanía
                name = "Poncho Andino Multicolor",
                description = "Poncho tejido con diseños tradicionales, 100% lana de alpaca. " +
                        "Medidas: 1.5m x 2m. Perfecta para decoración o abrigo.",
                price = 180.00,
                stock = 8,
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQd-1icQanG9W-paUq1O94mq1vDFiRNldLFvg&s",
                isActive = true
            ),
            Product(
                storeId = 2,
                categoryId = 5, // Artesanía
                name = "Bolso Artesanal de Aguayo",
                description = "Bolso tejido a mano con tela aguayo tradicional peruana. " +
                        "Diseños geométricos coloridos, cierre con cremallera, 35x30cm.",
                price = 75.00,
                stock = 30,
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcScNeIW6ExHxO68bwRccQ2pIhAQnIKg6LmfFA&s",
                isActive = true
            ),
            Product(
                storeId = 2,
                categoryId = 5, // Artesanía
                name = "Tapiz Decorativo Andino",
                description = "Tapiz tejido en telar tradicional, 100% lana de oveja. " +
                        "Diseños incas, chakanas y símbolos andinos. 80x120cm.",
                price = 350.00,
                stock = 5,
                imageUrl = "https://images1.novica.net/pictures/4/p444989_1.jpg",
                isActive = true
            ),
            Product(
                storeId = 2,
                categoryId = 4, // Alimentos
                name = "Café Orgánico Arequipeño 500g",
                description = "Café premium 100% arábica de selección nacional. " +
                        "Grano selecto, tueste medio, cultivo orgánico, notas achocolatadas.",
                price = 38.00,
                stock = 100,
                imageUrl = "https://cafevalenzuela.com.pe/wp-content/uploads/2021/06/CAFE-EXPRESS.jpg",
                isActive = true
            ),
            Product(
                storeId = 2,
                categoryId = 4, // Alimentos
                name = "Miel de Abeja Pura 1kg",
                description = "Miel 100% natural de abejas de la sierra peruana. " +
                        "Sin aditivos, propiedades antibacterianas, envase de vidrio.",
                price = 42.00,
                stock = 60,
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT_50eUHoUc1eRRWHgYr9JTossfGs8wBqH0GA&s",
                isActive = true
            ),
            Product(
                storeId = 2,
                categoryId = 4, // Alimentos
                name = "Quinua Orgánica Premium 1kg",
                description = "Quinua blanca certificada orgánica del altiplano peruano. " +
                        "Alta en proteínas, sin gluten, superalimento ancestral.",
                price = 28.00,
                stock = 80,
                imageUrl = "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=500&h=500&fit=crop",
                isActive = true
            ),
            Product(
                storeId = 2,
                categoryId = 4, // Alimentos
                name = "Chocolate Orgánico 70% Cacao",
                description = "Chocolate artesanal peruano con 70% cacao orgánico. " +
                        "Sabor intenso, sin azúcar refinada, barra de 100g.",
                price = 18.00,
                stock = 120,
                imageUrl = "https://images.unsplash.com/photo-1511381939415-e44015466834?w=500&h=500&fit=crop",
                isActive = true
            ),
            Product(
                storeId = 2,
                categoryId = 5, // Artesanía
                name = "Cerámica Decorativa Shipibo",
                description = "Jarrón de cerámica hecho a mano con diseños Shipibo-Konibo. " +
                        "Arte amazónico peruano, pintado a mano, 25cm altura.",
                price = 95.00,
                stock = 15,
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQyrkWIO3gvwmLN2Qha7JNC6gmdvynLGt8cJw&s",
                isActive = true
            )
        )

        products.forEach { product ->
            productRepository.createProduct(product)
        }
    }

    /**
     * Crear favoritos de prueba para el comprador
     */
    private suspend fun createTestFavorites() {
        val buyerUserId = 4L // Carlos Mendoza (comprador)

        // Agregar algunos productos como favoritos del comprador
        // Productos: 1, 2, 5, 11, 13, 16
        val favoriteProductIds = listOf(1L, 2L, 5L, 11L, 13L, 16L)

        favoriteProductIds.forEach { productId ->
            favoriteRepository.addToFavorites(
                userId = buyerUserId,
                productId = productId
            )
        }
    }
}