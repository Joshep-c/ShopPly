package com.shopply.appEcommerce.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index


 // Entidad CartItem - Items del carrito de compras

 // Carrito persistente que guarda los productos seleccionados por el usuario
 // Relación muchos a muchos entre User y Product

@Entity(
    tableName = "cart_items",
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
data class CartItem(
    val userId: Long,
    val productId: Long,
    val quantity: Int = 1,
    val addedAt: Long = System.currentTimeMillis()
)