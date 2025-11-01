package com.shopply.appEcommerce.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.shopply.appEcommerce.data.local.dao.*
import com.shopply.appEcommerce.data.local.entities.*


// AppDatabase - Base de datos principal de la aplicación

// 5 tablas esenciales:
// - User: Usuarios (Comprador, Vendedor, Admin)
//  - Store: Tiendas PYME
//  - Category: Categorías de productos
// - Product: Catálogo de productos
// - CartItem: Carrito de compras
@Database(
    entities = [
        User::class,
        Store::class,
        Category::class,
        Product::class,
        CartItem::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    // DAOs
    abstract fun userDao(): UserDao
    abstract fun storeDao(): StoreDao
    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao

    companion object {
        const val DATABASE_NAME = "shopply_database"
    }
}