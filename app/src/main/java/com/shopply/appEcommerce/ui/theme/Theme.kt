package com.shopply.appEcommerce.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * ShopPly Dark Color Scheme
 * Esquema oscuro optimizado para compras nocturnas
 */
private val DarkColorScheme = darkColorScheme(
    primary = ShopPlyBlueLight,
    onPrimary = Color.Black,
    primaryContainer = ShopPlyBlueDark,
    onPrimaryContainer = ShopPlyBlueLight,

    secondary = ShopPlyOrangeLight,
    onSecondary = Color.Black,
    secondaryContainer = ShopPlyOrangeDark,
    onSecondaryContainer = ShopPlyOrangeLight,

    tertiary = ShopPlyGreen,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF1B5E20),
    onTertiaryContainer = Color(0xFF81C784),

    error = ErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFF8B1A1A),
    onErrorContainer = Color(0xFFFFDAD6),

    background = BackgroundDark,
    onBackground = Color(0xFFE6E1E5),

    surface = SurfaceDark,
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF2B2B2B),
    onSurfaceVariant = ShopPlyGray400,

    outline = ShopPlyGray600,
    outlineVariant = ShopPlyGray800
)

/**
 * ShopPly Light Color Scheme
 * Esquema claro optimizado para visualización de productos
 */
private val LightColorScheme = lightColorScheme(
    primary = ShopPlyBlue,
    onPrimary = Color.White,
    primaryContainer = ShopPlyBlueLight,
    onPrimaryContainer = ShopPlyBlueDark,

    secondary = ShopPlyOrange,
    onSecondary = Color.White,
    secondaryContainer = ShopPlyOrangeLight,
    onSecondaryContainer = ShopPlyOrangeDark,

    tertiary = ShopPlyGreen,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFC8E6C9),
    onTertiaryContainer = Color(0xFF1B5E20),

    error = ErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    background = BackgroundLight,
    onBackground = ShopPlyGray900,

    surface = SurfaceLight,
    onSurface = ShopPlyGray900,
    surfaceVariant = ShopPlyGray100,
    onSurfaceVariant = ShopPlyGray700,

    outline = ShopPlyGray400,
    outlineVariant = ShopPlyGray300
)

/**
 * ShopPly Theme
 *
 * Tema principal de la aplicación con soporte para:
 * - Modo claro/oscuro
 * - Dynamic Color (Android 12+)
 * - Status bar personalizado
 */
@Composable
fun ShopPly2Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color disponible en Android 12+
    dynamicColor: Boolean = false, // Desactivado por defecto para mantener branding
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}