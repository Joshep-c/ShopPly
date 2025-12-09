package com.shopply.appEcommerce.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.ui.graphics.vector.ImageVector
import com.shopply.appEcommerce.data.local.entities.UserRole

data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String,
    val roles: List<UserRole> = UserRole.entries
)

fun getBottomNavItems(userRole: UserRole): List<BottomNavItem> {
    return when (userRole) {
        UserRole.BUYER -> {
            listOf(
                BottomNavItem(
                    title = "Inicio",
                    icon = Icons.Default.Home,
                    route = Screen.Home.route
                ),
                BottomNavItem(
                    title = "Favoritos",
                    icon = Icons.Default.Favorite,
                    route = Screen.Favorites.route
                ),
                BottomNavItem(
                    title = "Carrito",
                    icon = Icons.Default.ShoppingCart,
                    route = Screen.Cart.route
                ),
                BottomNavItem(
                    title = "Perfil",
                    icon = Icons.Default.Person,
                    route = Screen.Profile.route
                )
            )
        }
        UserRole.SELLER -> {
            listOf(
                BottomNavItem(
                    title = "Inicio",
                    icon = Icons.Default.Home,
                    route = Screen.Home.route
                ),
                BottomNavItem(
                    title = "Favoritos",
                    icon = Icons.Default.Favorite,
                    route = Screen.Favorites.route
                ),
                BottomNavItem(
                    title = "Carrito",
                    icon = Icons.Default.ShoppingCart,
                    route = Screen.Cart.route
                ),
                BottomNavItem(
                    title = "Tienda",
                    icon = Icons.Default.Store,
                    route = Screen.Store.route
                ),
                BottomNavItem(
                    title = "Perfil",
                    icon = Icons.Default.Person,
                    route = Screen.Profile.route
                )
            )
        }
        UserRole.ADMIN -> {
            listOf(
                BottomNavItem(
                    title = "Inicio",
                    icon = Icons.Default.Home,
                    route = Screen.Home.route
                ),
                BottomNavItem(
                    title = "Favoritos",
                    icon = Icons.Default.Favorite,
                    route = Screen.Favorites.route
                ),
                BottomNavItem(
                    title = "Carrito",
                    icon = Icons.Default.ShoppingCart,
                    route = Screen.Cart.route
                ),
                BottomNavItem(
                    title = "Tiendas",
                    icon = Icons.Default.Store,
                    route = Screen.AdminStores.route
                ),
                BottomNavItem(
                    title = "Perfil",
                    icon = Icons.Default.Person,
                    route = Screen.Profile.route
                )
            )
        }
    }
}