package com.shopply.appEcommerce.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


// Entidad Category - Categorías de productos

 // Organiza los productos en categorías para facilitar la búsqueda
 // Solo el ADMIN puede gestionar categorías

@Entity(
    tableName = "categories",
    indices = [Index(value = ["name"], unique = true)]
)
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,
    val description: String? = null,

    // Control de visualización
    val isActive: Boolean = true,
    val displayOrder: Int = 0, // Orden de aparición en la UI

    val createdAt: Long = System.currentTimeMillis()
)