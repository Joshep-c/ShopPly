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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    // Primary (#0066ff)
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimary.copy(alpha = 0.1f),
    onPrimaryContainer = LightPrimary,

    // Secondary (#10b981)
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondary.copy(alpha = 0.1f),
    onSecondaryContainer = LightSecondary,

    // Tertiary/Accent (#f59e0b)
    tertiary = LightAccent,
    onTertiary = LightOnAccent,
    tertiaryContainer = LightAccent.copy(alpha = 0.1f),
    onTertiaryContainer = LightAccent,

    // Error/Destructive (#ef4444)
    error = LightDestructive,
    onError = LightOnDestructive,
    errorContainer = LightDestructive.copy(alpha = 0.1f),
    onErrorContainer = LightDestructive,

    // Background & Surface
    background = LightBackground,
    onBackground = LightForeground,
    surface = LightCard,
    onSurface = LightCardForeground,
    surfaceVariant = LightMuted,
    onSurfaceVariant = LightMutedForeground,

    // Outline & Borders
    outline = LightBorder,
    outlineVariant = LightBorder.copy(alpha = 0.5f)
)

/**
 * ShopPly Dark Color Scheme
 * Basado en tokens CSS .dark
 */
private val DarkColorScheme = darkColorScheme(
    // Primary (invertido en dark mode)
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkOnPrimary,
    onPrimaryContainer = DarkPrimary,

    // Secondary
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondary,
    onSecondaryContainer = DarkOnSecondary,

    // Tertiary/Accent
    tertiary = DarkAccent,
    onTertiary = DarkOnAccent,
    tertiaryContainer = DarkAccent,
    onTertiaryContainer = DarkOnAccent,

    // Error/Destructive
    error = DarkDestructive,
    onError = DarkOnDestructive,
    errorContainer = DarkDestructive,
    onErrorContainer = DarkOnDestructive,

    // Background & Surface
    background = DarkBackground,
    onBackground = DarkForeground,
    surface = DarkCard,
    onSurface = DarkCardForeground,
    surfaceVariant = DarkMuted,
    onSurfaceVariant = DarkMutedForeground,

    // Outline & Borders
    outline = DarkBorder,
    outlineVariant = DarkBorder.copy(alpha = 0.5f)
)

/**
 * ShopPly Theme
 *
 * Tema principal con colores alineados a diseño web CSS
 * Soporta:
 * - Modo claro/oscuro
 * - Dynamic Color (Android 12+)
 */
@Composable
fun ShopPly2Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}