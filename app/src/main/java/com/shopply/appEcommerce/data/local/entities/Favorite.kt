package com.shopply.appEcommerce.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Entidad Favorite - Productos favoritos del usuario
 *
 * Permite a los usuarios marcar productos como favoritos
 * Relación muchos a muchos entre User y Product
 */
@Entity(
    tableName = "favorites",
    primaryKeys = ["userId", "productId"],
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["productId"])
    ]
)
data class Favorite(
    val userId: Long,
    val productId: Long,
    val addedAt: Long = System.currentTimeMillis()
)

