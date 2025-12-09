package com.shopply.appEcommerce.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


// Entidad User - Usuarios del sistema

 // Soporta 3 tipos de usuarios:
 // - BUYER: Comprador
 // - SELLER: Vendedor (PYME)
 // - ADMIN: Administrador de la plataforma

@Entity(
    tableName = "users",
    indices = [
        Index(value = ["email"], unique = true),
        Index(value = ["userRole"])
    ]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val email: String,
    val passwordHash: String, // En producción usar hash real (bcrypt, etc)
    val name: String,
    val phone: String? = null,
    val userRole: UserRole = UserRole.BUYER,

    // Admin puede banear usuarios
    val isBanned: Boolean = false,
    val banReason: String? = null,

    // Timestamps
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis()
)

// Roles de usuario en el sistema

enum class UserRole {
    BUYER,      // Comprador: puede ver y comprar productos
    SELLER,     // Vendedor: puede crear tienda y vender productos
    ADMIN       // Administrador: gestiona la plataforma
}