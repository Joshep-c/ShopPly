package com.shopply.appEcommerce.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(  // ← AGREGAR @Inject constructor
    // Aquí puedes inyectar repositorios más adelante
    // private val authRepository: AuthRepository
) : ViewModel() {

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoggedIn by mutableStateOf(false)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun login() {
        isLoading = true
        errorMessage = null

        // Simulación básica de autenticación
        if (email == "admin@test.com" && password == "123456") {
            isLoggedIn = true
        } else {
            errorMessage = "Credenciales inválidas"
        }

        isLoading = false
    }

    fun signUp() {
        isLoading = true
        errorMessage = null

        if (email.isNotBlank() && password.length >= 6) {
            isLoggedIn = true
        } else {
            errorMessage = "Datos inválidos"
        }

        isLoading = false
    }

    fun logout() {
        isLoggedIn = false
        email = ""
        password = ""
    }
}