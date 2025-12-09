package com.shopply.appEcommerce.ui.navigation

sealed class Screen(val route: String) {
    // Rutas de autenticación
    data object Auth : Screen("auth")
    data object Login : Screen("login")
    data object SignUp : Screen("signup")

    // Rutas principales
    data object Home : Screen("home")
    data object Categories : Screen("categories")
    data object Cart : Screen("cart")
    data object Profile : Screen("profile")
    data object Favorites : Screen("favorites")

    // Rutas de vendedor
    data object Store : Screen("store")

    // Rutas de administrador
    data object AdminStores : Screen("admin_stores")

    // Rutas con argumentos
    data object ProductDetail : Screen("product/{productId}") {
        fun createRoute(productId: String) = "product/$productId"
    }

    data object CategoryProducts : Screen("category/{categoryId}") {
        fun createRoute(categoryId: String) = "category/$categoryId"
    }

    data object OrderDetail : Screen("order/{orderId}") {
        fun createRoute(orderId: String) = "order/$orderId"
    }

    // Rutas de búsqueda
    data object Search : Screen("search?query={query}") {
        fun createRoute(query: String = "") = "search?query=$query"
    }
}