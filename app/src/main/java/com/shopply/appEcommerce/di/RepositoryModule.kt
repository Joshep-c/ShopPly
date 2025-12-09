package com.shopply.appEcommerce.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.shopply.appEcommerce.data.local.dao.CartDao
import com.shopply.appEcommerce.data.local.dao.CategoryDao
import com.shopply.appEcommerce.data.local.dao.ProductDao
import com.shopply.appEcommerce.data.local.dao.StoreDao
import com.shopply.appEcommerce.data.local.dao.UserDao
import com.shopply.appEcommerce.data.repository.CartRepository
import com.shopply.appEcommerce.data.repository.CategoryRepository
import com.shopply.appEcommerce.data.repository.DataSeeder
import com.shopply.appEcommerce.data.repository.ProductRepository
import com.shopply.appEcommerce.data.repository.StoreRepository
import com.shopply.appEcommerce.data.repository.UserRepository
import com.shopply.appEcommerce.data.security.PasswordHasher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * RepositoryModule - Módulo de inyección de dependencias para Repositories
 *
 * Provee:
 * - Todos los repositorios
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
        dataStore: DataStore<Preferences>,
        passwordHasher: PasswordHasher
    ): UserRepository {
        return UserRepository(userDao, dataStore, passwordHasher)
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

    /**
     * Provee DatabaseInitializer
     */
    @Provides
    @Singleton
    fun provideDatabaseInitializer(
        dataSeeder: DataSeeder,
        userRepository: UserRepository,
        storeRepository: StoreRepository,
        categoryRepository: CategoryRepository,
        productRepository: ProductRepository,
        cartRepository: CartRepository
    ): com.shopply.appEcommerce.data.local.database.DatabaseInitializer {
        return com.shopply.appEcommerce.data.local.database.DatabaseInitializer(
            dataSeeder,
            userRepository,
            storeRepository,
            categoryRepository,
            productRepository,
            cartRepository
        )
    }
}