package com.shopply.appEcommerce.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Entidad Product - Productos del catálogo

 // Los vendedores publican productos en sus tiendas
 // Los productos pertenecen a una categoría y una tienda

@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            entity = Store::class,
            parentColumns = ["id"],
            childColumns = ["storeId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["storeId"]),
        Index(value = ["categoryId"]),
        Index(value = ["isActive"]),
        Index(value = ["name"])
    ]
)
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val storeId: Long,
    val categoryId: Long,

    // Información del producto
    val name: String,
    val description: String,
    val price: Double,

    /**
     * URL de la imagen del producto
     *
     * Soporta DOS tipos de imágenes:
     *
     * 1. URLs externas (para productos de prueba):
     *    "https://images.unsplash.com/photo-123/image.jpg"
     *    - ✅ Gratis, sin Firebase Storage
     *    - ✅ APK más ligero
     *    - ⚠️ Requiere internet
     *
     * 2. Rutas locales (para productos del vendedor):
     *    "file:///data/data/com.shopply.appEcommerce/files/product_images/abc.jpg"
     *    - ✅ Guardadas con LocalStorageService
     *    - ✅ Funciona offline
     *    - ✅ Sin costos de Firebase
     *
     * Coil (AsyncImage) maneja automáticamente ambos tipos.
     */
    val imageUrl: String = "",

    // Inventario
    val stock: Int = 0,
    val isActive: Boolean = true,

    // Timestamps
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
