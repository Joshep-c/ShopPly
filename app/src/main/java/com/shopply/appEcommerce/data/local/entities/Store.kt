package com.shopply.appEcommerce.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


// Entidad Store - Tiendas de las PYMEs

 // Cada vendedor puede tener UNA tienda
 // Las tiendas deben ser aprobadas por un admin antes de estar activas

@Entity(
    tableName = "stores",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["ownerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["ownerId"], unique = true), // Un vendedor = una tienda
        Index(value = ["status"])
    ]
)
data class Store(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val ownerId: Long, // ID del usuario vendedor

    // Información de la tienda
    val name: String,
    val description: String,
    val ruc: String, // RUC de 11 dígitos (Perú)
    val phone: String,

    // Estado y métricas
    val status: StoreStatus = StoreStatus.PENDING,
    val rating: Float = 0f, // Promedio de calificaciones (para futuro)
    val rejectionReason: String? = null, // Razón si fue rechazada

    // Timestamps
    val createdAt: Long = System.currentTimeMillis(),
    val approvedAt: Long? = null
)

// Estados de una tienda

enum class StoreStatus {
    PENDING,    // Esperando aprobación del admin
    APPROVED,   // Aprobada y activa
    REJECTED    // Rechazada por admin
}