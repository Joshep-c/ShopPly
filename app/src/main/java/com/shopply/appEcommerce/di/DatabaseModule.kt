package com.shopply.appEcommerce.di

import android.content.Context
import androidx.room.Room
import com.shopply.appEcommerce.data.local.dao.CartDao
import com.shopply.appEcommerce.data.local.dao.CategoryDao
import com.shopply.appEcommerce.data.local.dao.FavoriteDao
import com.shopply.appEcommerce.data.local.dao.ProductDao
import com.shopply.appEcommerce.data.local.dao.StoreDao
import com.shopply.appEcommerce.data.local.dao.UserDao
import com.shopply.appEcommerce.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * DatabaseModule - Módulo de inyección de dependencias para Room
 *
 * Provee:
 * - Instancia de AppDatabase
 * - Todos los DAOs
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provee la instancia única de la base de datos
     */
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration() // Solo para desarrollo/prototipo
            .build()
    }

    /**
     * Provee UserDao
     */
    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    /**
     * Provee StoreDao
     */
    @Provides
    @Singleton
    fun provideStoreDao(database: AppDatabase): StoreDao {
        return database.storeDao()
    }

    /**
     * Provee CategoryDao
     */
    @Provides
    @Singleton
    fun provideCategoryDao(database: AppDatabase): CategoryDao {
        return database.categoryDao()
    }

    /**
     * Provee ProductDao
     */
    @Provides
    @Singleton
    fun provideProductDao(database: AppDatabase): ProductDao {
        return database.productDao()
    }

    /**
     * Provee CartDao
     */
    @Provides
    @Singleton
    fun provideCartDao(database: AppDatabase): CartDao {
        return database.cartDao()
    }

    /**
     * Provee FavoriteDao
     */
    @Provides
    @Singleton
    fun provideFavoriteDao(database: AppDatabase): FavoriteDao {
        return database.favoriteDao()
    }
}