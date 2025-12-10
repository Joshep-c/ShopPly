package com.shopply.appEcommerce.data.local.database

import androidx.room.TypeConverter
import com.shopply.appEcommerce.data.local.entities.StoreStatus
import com.shopply.appEcommerce.data.local.entities.UserRole


// Converters - Conversores de tipos para Room

// Convierte tipos personalizados (enums) a tipos que Room puede guardar (String)

class Converters {

    // UserRole
    @TypeConverter
    fun fromUserRole(role: UserRole): String {
        return role.name
    }

    @TypeConverter
    fun toUserRole(role: String): UserRole {
        return UserRole.valueOf(role)
    }

    // StoreStatus

    @TypeConverter
    fun fromStoreStatus(status: StoreStatus): String {
        return status.name
    }

    @TypeConverter
    fun toStoreStatus(status: String): StoreStatus {
        return StoreStatus.valueOf(status)
    }
}