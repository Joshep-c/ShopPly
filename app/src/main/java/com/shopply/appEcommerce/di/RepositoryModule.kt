package com.shopply.appEcommerce.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.shopply.appEcommerce.data.local.dao.*
import com.shopply.appEcommerce.data.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * RepositoryModule - Módulo de inyección de dependencias para Repositories
 * Ubicación: app/src/main/java/com/shopply/appEcommerce/di/RepositoryModule.kt
 *
 * Provee:
 * - Todos los repositorios del MVP
 * - DataSeeder para poblar datos iniciales
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    /**
     * Provee UserRepository
     */
    @Provides
    @Singleton
    fun provideUserRepository(
        userDao: UserDao,
        dataStore: DataStore<Preferences>
    ): UserRepository {
        return UserRepository(userDao, dataStore)
    }

    /**
     * Provee StoreRepository
     */
    @Provides
    @Singleton
    fun provideStoreRepository(
        storeDao: StoreDao
    ): StoreRepository {
        return StoreRepository(storeDao)
    }

    /**
     * Provee CategoryRepository
     */
    @Provides
    @Singleton
    fun provideCategoryRepository(
        categoryDao: CategoryDao
    ): CategoryRepository {
        return CategoryRepository(categoryDao)
    }

    /**
     * Provee ProductRepository
     */
    @Provides
    @Singleton
    fun provideProductRepository(
        productDao: ProductDao
    ): ProductRepository {
        return ProductRepository(productDao)
    }

    /**
     * Provee CartRepository
     */
    @Provides
    @Singleton
    fun provideCartRepository(
        cartDao: CartDao,
        productDao: ProductDao
    ): CartRepository {
        return CartRepository(cartDao, productDao)
    }

    /**
     * Provee DataSeeder
     */
    @Provides
    @Singleton
    fun provideDataSeeder(
        userRepository: UserRepository,
        storeRepository: StoreRepository,
        categoryRepository: CategoryRepository,
        productRepository: ProductRepository
    ): DataSeeder {
        return DataSeeder(
            userRepository,
            storeRepository,
            categoryRepository,
            productRepository
        )
    }
}