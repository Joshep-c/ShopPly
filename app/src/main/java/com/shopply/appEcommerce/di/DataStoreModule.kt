package com.shopply.appEcommerce.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Extension property para crear DataStore
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "shopply_preferences"
)

/**
 * DataStoreModule - Módulo de inyección de dependencias para DataStore
 * Ubicación: app/src/main/java/com/shopply/appEcommerce/di/DataStoreModule.kt
 *
 * Provee:
 * - DataStore para preferencias (sesión de usuario)
 */
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    /**
     * Provee DataStore de preferencias
     * Usado para guardar la sesión del usuario actual
     */
    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return context.dataStore
    }
}