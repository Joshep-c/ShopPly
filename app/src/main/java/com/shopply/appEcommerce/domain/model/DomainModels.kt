package com.shopply.appEcommerce.domain.model

/**
 * DomainModels - Modelos del dominio y utilitarios
 *
 * Contiene:
 * - Result wrapper para manejo de errores
 * - Estados de UI comunes
 */

// ===== RESULT WRAPPER =====

/**
 * Sealed class para envolver resultados de operaciones
 * Facilita el manejo de éxito y errores
 */
sealed class Result<out T> {
    /**
     * Operación exitosa con datos
     */
    data class Success<T>(val data: T) : Result<T>()

    /**
     * Operación fallida con excepción
     */
    data class Error(val exception: Exception) : Result<Nothing>()
}

// ===== UI STATES =====

/**
 * Estados genéricos de UI
 */
sealed class UiState<out T> {
    /**
     * Estado inicial o inactivo
     */
    data object Idle : UiState<Nothing>()

    /**
     * Cargando datos
     */
    data object Loading : UiState<Nothing>()

    /**
     * Operación exitosa con datos
     */
    data class Success<T>(val data: T) : UiState<T>()

    /**
     * Error con mensaje
     */
    data class Error(val message: String) : UiState<Nothing>()
}

// ===== EXTENSION FUNCTIONS =====

/**
 * Verifica si el Result es exitoso
 */
fun <T> Result<T>.isSuccess(): Boolean = this is Result.Success

/**
 * Verifica si el Result es error
 */
fun <T> Result<T>.isError(): Boolean = this is Result.Error

/**
 * Obtiene los datos si es Success, null si es Error
 */
fun <T> Result<T>.getOrNull(): T? {
    return when (this) {
        is Result.Success -> data
        is Result.Error -> null
    }
}

/**
 * Obtiene el mensaje de error si existe
 */
fun <T> Result<T>.getErrorMessage(): String? {
    return when (this) {
        is Result.Success -> null
        is Result.Error -> exception.message
    }
}