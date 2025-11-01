package com.shopply.appEcommerce.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.shopply.appEcommerce.ui.auth.AuthScreen
import com.shopply.appEcommerce.ui.auth.AuthViewModel
import com.shopply.appEcommerce.ui.auth.LoginScreen
import com.shopply.appEcommerce.ui.auth.SignUpScreen

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Auth.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Auth.route) {
            AuthScreen(
                modifier = modifier,
                onLoginClick = {
                    navController.navigate(Screen.Login.route)
                },
                onSignUpClick = {
                    navController.navigate(Screen.SignUp.route)
                }
            )
        }

        composable(Screen.Login.route) {
            val viewModel: AuthViewModel = hiltViewModel()

            LaunchedEffect(viewModel.isLoggedIn) {
                if (viewModel.isLoggedIn) {
                    navController.navigate(Screen.Home.route) {
                        // Eliminar toda la pila de autenticación
                        popUpTo(Screen.Auth.route) {
                            inclusive = true
                        }
                        // Evitar múltiples copias de Home
                        launchSingleTop = true
                    }
                }
            }

            LoginScreen(
                modifier = modifier,
                viewModel = viewModel,
                onSignUpClick = {
                    navController.navigate(Screen.SignUp.route)
                }
            )
        }

        composable(Screen.SignUp.route) {
            val viewModel: AuthViewModel = hiltViewModel()

            LaunchedEffect(viewModel.isLoggedIn) {
                if (viewModel.isLoggedIn) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Auth.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            }

            SignUpScreen(
                modifier = modifier,
                viewModel = viewModel,
                onLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        // ========== PANTALLA HOME (Para el futuro) ==========
        composable(Screen.Home.route) {
            // HomeScreen(
            //     modifier = modifier,
            //     onProductClick = { productId ->
            //         navController.navigate(
            //             Screen.ProductDetail.createRoute(productId)
            //         )
            //     }
            // )
        }

        // ========== DETALLE DE PRODUCTO (Ejemplo con argumentos) ==========
        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(
                navArgument("productId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            // ProductDetailScreen(
            //     productId = productId,
            //     onBackClick = { navController.popBackStack() }
            // )
        }
    }
}