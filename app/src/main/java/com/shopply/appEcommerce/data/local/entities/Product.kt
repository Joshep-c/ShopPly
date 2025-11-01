package com.shopply.appEcommerce.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index


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
    val imageUrl: String? = null,

    // Inventario
    val stock: Int = 0,
    val isActive: Boolean = true, // Vendedor puede desactivar productos

    // Timestamps
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)