package com.shopply.appEcommerce.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Extensiones de ColorScheme para acceder a colores especiales de ShopPly
 * que no están directamente mapeados en Material 3
 */

// Colores especiales para e-commerce
val ColorScheme.starRating: Color
    get() = tertiary  // Naranja/Amarillo para ratings

val ColorScheme.discountBadge: Color
    get() = error  // Rojo para descuentos y ofertas

val ColorScheme.favoriteIcon: Color
    get() = error  // Rojo para corazón de favoritos

val ColorScheme.successColor: Color
    get() = secondary  // Verde para éxito/disponible

val ColorScheme.onlineStatus: Color
    get() = secondary  // Verde para estado online

// Colores de charts (usar los del theme)
val ColorScheme.chart1: Color
    get() = primary  // Azul

val ColorScheme.chart2: Color
    get() = secondary  // Verde

val ColorScheme.chart3: Color
    get() = tertiary  // Naranja

val ColorScheme.chart4: Color
    get() = LightChart4  // Purple - usar el mismo en ambos modos por simplicidad

val ColorScheme.chart5: Color
    get() = LightChart5  // Pink - usar el mismo en ambos modos por simplicidad

// Backgrounds especiales
val ColorScheme.inputBackground: Color
    get() = surfaceVariant

val ColorScheme.cardElevated: Color
    get() = surface



