package com.shopply.appEcommerce.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.shopply.appEcommerce.data.local.entities.UserRole
import com.shopply.appEcommerce.data.repository.UserRepository
import com.shopply.appEcommerce.ui.admin.AdminStoresScreen
import com.shopply.appEcommerce.ui.addproduct.AddEditProductScreen
import com.shopply.appEcommerce.ui.cart.CartScreen
import com.shopply.appEcommerce.ui.favorites.FavoritesScreen
import com.shopply.appEcommerce.ui.home.HomeScreen
import com.shopply.appEcommerce.ui.home.HomeViewModel
import com.shopply.appEcommerce.ui.navigation.Screen
import com.shopply.appEcommerce.ui.navigation.getBottomNavItems
import com.shopply.appEcommerce.ui.productdetail.ProductDetailScreen
import com.shopply.appEcommerce.ui.profile.ProfileScreen
import com.shopply.appEcommerce.ui.store.StoreScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    userRepository: UserRepository,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    var userRole by remember { mutableStateOf(UserRole.BUYER) }

    LaunchedEffect(Unit) {
        scope.launch {
            val user = userRepository.getCurrentUser().first()
            userRole = user?.userRole ?: UserRole.BUYER
        }
    }

    val bottomNavItems = getBottomNavItems(userRole)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentDestination?.hierarchy?.any {
                            it.route == item.route
                        } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        MainNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            userRole = userRole,
            onLogout = onLogout
        )
    }
}

@Composable
fun MainNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    userRole: UserRole,
    onLogout: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            val viewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                viewModel = viewModel,
                onLogout = onLogout,
                onProductClick = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId.toString()))
                }
            )
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen(
                onProductClick = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId.toString()))
                }
            )
        }

        composable(Screen.Cart.route) {
            CartScreen(
                onProductClick = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId.toString()))
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(onLogout = onLogout)
        }

        // Pantalla de Tienda (solo para vendedores)
        if (userRole == UserRole.SELLER) {
            composable(Screen.Store.route) {
                StoreScreen(
                    onProductClick = { productId ->
                        navController.navigate(Screen.ProductDetail.createRoute(productId.toString()))
                    },
                    onAddProductClick = {
                        navController.navigate("add_product")
                    },
                    onEditProductClick = { productId ->
                        navController.navigate("edit_product/$productId")
                    }
                )
            }
        }

        // AGREGAR PRODUCTO (solo vendedores)
        composable("add_product") {
            AddEditProductScreen(
                productId = null,
                onBackClick = { navController.popBackStack() },
                onProductSaved = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId.toString())) {
                        popUpTo(Screen.Store.route)
                    }
                }
            )
        }

        // EDITAR PRODUCTO (solo vendedores)
        composable(
            route = "edit_product/{productId}",
            arguments = listOf(
                navArgument("productId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            AddEditProductScreen(
                productId = productId,
                onBackClick = { navController.popBackStack() },
                onProductSaved = {
                    navController.popBackStack()
                }
            )
        }

        // Pantalla de Admin (solo para administradores)
        if (userRole == UserRole.ADMIN) {
            composable(Screen.AdminStores.route) {
                AdminStoresScreen()
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
            ProductDetailScreen(
                productId = productId,
                onBackClick = { navController.popBackStack() },
                onCartClick = { navController.navigate(Screen.Cart.route) }
            )
        }
    }
}