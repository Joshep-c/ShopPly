package com.shopply.appEcommerce.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.shopply.appEcommerce.data.local.database.DatabaseInitializer
import com.shopply.appEcommerce.ui.navigation.NavGraph
import com.shopply.appEcommerce.ui.navigation.Screen
import com.shopply.appEcommerce.ui.splash.SplashScreen
import com.shopply.appEcommerce.ui.theme.ShopPly2Theme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * MainActivity - Actividad principal
 *
 * Responsabilidades (siguiendo Single Responsibility Principle):
 * - Configurar la UI con Jetpack Compose
 * - Inicializar la base de datos
 * - Delegar la lógica de autenticación a MainViewModel
 *
 * Buenas prácticas implementadas:
 * ✅ Separación de responsabilidades (UI vs Lógica de negocio)
 * ✅ MVVM Pattern (ViewModel gestiona el estado)
 * ✅ Inyección de dependencias con Hilt
 * ✅ StateFlow para estados reactivos
 * ✅ SplashScreen profesional
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var databaseInitializer: DatabaseInitializer

    // ViewModel para gestionar el estado de autenticación
    private val mainViewModel: MainViewModel by viewModels()

    // MODO DEBUG: Cambia a false para producción
    private val isDebugMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializar base de datos en background
        initializeDatabase()

        setContent {
            ShopPly2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainContent(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = mainViewModel
                    )
                }
            }
        }
    }

    private fun initializeDatabase() {
        lifecycleScope.launch {
            // Siempre inicializar datos (solo se insertan si la BD está vacía)
            databaseInitializer.initialize()

            // Modo DEBUG: Mostrar logs detallados
            if (isDebugMode) {
                databaseInitializer.showDatabaseSummary()

                // OPCIONAL: Test completo (descomenta para verificar todas las operaciones)
                // databaseInitializer.runFullTest()
            }
        }
    }
}

/**
 * MainContent - Composable que gestiona los diferentes estados de la app
 *
 * Estados:
 * - Loading -> Mostrar SplashScreen
 * - Authenticated -> Navegar a HomeScreen
 * - Unauthenticated -> Mostrar AuthScreen (bienvenida)
 */
@Composable
private fun MainContent(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        is MainUiState.Loading -> {
            // Estado inicial: Verificando sesión
            SplashScreen(modifier = modifier)
        }

        is MainUiState.Authenticated -> {
            // Usuario autenticado: Ir a Home
            NavGraph(
                modifier = modifier,
                startDestination = Screen.Home.route
            )
        }

        is MainUiState.Unauthenticated -> {
            // Usuario no autenticado: Mostrar bienvenida
            NavGraph(
                modifier = modifier,
                startDestination = Screen.Auth.route
            )
        }
    }
}
