package com.shopply.appEcommerce.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.shopply.appEcommerce.data.local.dao.CartDao
import com.shopply.appEcommerce.data.local.dao.CategoryDao
import com.shopply.appEcommerce.data.local.dao.FavoriteDao
import com.shopply.appEcommerce.data.local.dao.ProductDao
import com.shopply.appEcommerce.data.local.dao.StoreDao
import com.shopply.appEcommerce.data.local.dao.UserDao
import com.shopply.appEcommerce.data.local.entities.CartItem
import com.shopply.appEcommerce.data.local.entities.Category
import com.shopply.appEcommerce.data.local.entities.Favorite
import com.shopply.appEcommerce.data.local.entities.Product
import com.shopply.appEcommerce.data.local.entities.Store
import com.shopply.appEcommerce.data.local.entities.User


// AppDatabase - Base de datos

// 5 tablas esenciales:
// - User: Usuarios (Comprador, Vendedor, Admin)
// - Store: Tiendas PYME
// - Category: Categorías de productos
// - Product: Catálogo de productos
// - CartItem: Carrito de compras
@Database(
    entities = [
        User::class,
        Store::class,
        Category::class,
        Product::class,
        CartItem::class,
        Favorite::class
    ],
    version = 4, // Incrementado por agregar imageUrl a Category
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
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        const val DATABASE_NAME = "shopply_database"

        /**
         * Migración de versión 3 a 4
         * Agrega el campo imageUrl a la tabla categories
         */
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE categories ADD COLUMN imageUrl TEXT"
                )
            }
        }
    }
}