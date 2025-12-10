package com.shopply.appEcommerce.data.local.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shopply.appEcommerce.data.local.entities.StoreStatus
import com.shopply.appEcommerce.data.local.entities.UserRole


// Converters - Conversores de tipos para Room
// Convierte tipos personalizados (enums, listas) a tipos que Room puede guardar (String)

class Converters {

    private val gson = Gson()

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

    // List<String> para URLs de imágenes
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }
}