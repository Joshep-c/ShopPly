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
import com.shopply.appEcommerce.data.repository.UserRepository
import com.shopply.appEcommerce.ui.auth.AuthScreen
import com.shopply.appEcommerce.ui.auth.AuthViewModel
import com.shopply.appEcommerce.ui.auth.LoginScreen
import com.shopply.appEcommerce.ui.auth.SignUpScreen
import com.shopply.appEcommerce.ui.main.MainScreen

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Auth.route,
    userRepository: UserRepository? = null
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // PANTALLAS DE AUTENTICACIÓN
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

        // PANTALLA PRINCIPAL CON NAVEGACIÓN INFERIOR
        composable(Screen.Home.route) {
            if (userRepository != null) {
                MainScreen(
                    modifier = modifier,
                    userRepository = userRepository,
                    onLogout = {
                        navController.navigate(Screen.Auth.route) {
                            popUpTo(0) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }

        // DETALLE DE PRODUCTO
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