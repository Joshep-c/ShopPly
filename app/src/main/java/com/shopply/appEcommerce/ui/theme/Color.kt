package com.shopply.appEcommerce.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * ShopPly Color System
 * Basado en tokens de diseño web para consistencia cross-platform
 */


// LIGHT THEME COLORS


// Primary - Blue (#0066ff)
val LightPrimary = Color(0xFF0066FF)
val LightOnPrimary = Color(0xFFFFFFFF)

// Secondary - Green (#10b981)
val LightSecondary = Color(0xFF10B981)
val LightOnSecondary = Color(0xFFFFFFFF)

// Accent - Orange (#f59e0b)
val LightAccent = Color(0xFFF59E0B)
val LightOnAccent = Color(0xFF1A202C)

// Background & Surface
val LightBackground = Color(0xFFFAFBFC)
val LightForeground = Color(0xFF1A202C)
val LightCard = Color(0xFFFFFFFF)
val LightCardForeground = Color(0xFF1A202C)

// Muted (fondos sutiles)
val LightMuted = Color(0xFFF7FAFC)
val LightMutedForeground = Color(0xFF64748B)

// Destructive/Error
val LightDestructive = Color(0xFFEF4444)
val LightOnDestructive = Color(0xFFFFFFFF)

// Borders & Inputs
val LightBorder = Color(0x14000000)  // rgba(0, 0, 0, 0.08)
val LightInputBackground = Color(0xFFF8FAFC)
val LightSwitchBackground = Color(0xFFCBD5E1)

// Charts
val LightChart1 = Color(0xFF0066FF)  // Primary blue
val LightChart2 = Color(0xFF10B981)  // Secondary green
val LightChart3 = Color(0xFFF59E0B)  // Accent orange
val LightChart4 = Color(0xFF8B5CF6)  // Purple
val LightChart5 = Color(0xFFEC4899)  // Pink

// DARK THEME COLORS

// Background & Surface - oklch(0.145 0 0) ≈ #212529
val DarkBackground = Color(0xFF212529)
val DarkForeground = Color(0xFFFBFBFC)  // oklch(0.985 0 0)
val DarkCard = Color(0xFF212529)
val DarkCardForeground = Color(0xFFFBFBFC)

// Primary - oklch(0.985 0 0) (claro en dark mode)
val DarkPrimary = Color(0xFFFBFBFC)
val DarkOnPrimary = Color(0xFF343A40)  // oklch(0.205 0 0)

// Secondary - oklch(0.269 0 0) ≈ #444
val DarkSecondary = Color(0xFF444444)
val DarkOnSecondary = Color(0xFFFBFBFC)

// Muted
val DarkMuted = Color(0xFF444444)  // oklch(0.269 0 0)
val DarkMutedForeground = Color(0xFFB5B5B5)  // oklch(0.708 0 0)

// Accent
val DarkAccent = Color(0xFF444444)
val DarkOnAccent = Color(0xFFFBFBFC)

// Destructive - oklch(0.396 0.141 25.723)
val DarkDestructive = Color(0xFF8B3A3A)
val DarkOnDestructive = Color(0xFFA85252)  // oklch(0.637 0.237 25.331)

// Borders & Inputs
val DarkBorder = Color(0xFF444444)
val DarkInputBackground = Color(0xFF2B2B2B)
val DarkRing = Color(0xFF707070)  // oklch(0.439 0 0)

// Charts - oklch values
val DarkChart1 = Color(0xFF5E7CE2)  // oklch(0.488 0.243 264.376) - blue
val DarkChart2 = Color(0xFF52C97E)  // oklch(0.696 0.17 162.48) - green
val DarkChart3 = Color(0xFFE8B339)  // oklch(0.769 0.188 70.08) - yellow
val DarkChart4 = Color(0xFF9D7CD8)  // oklch(0.627 0.265 303.9) - purple
val DarkChart5 = Color(0xFFD85577)  // oklch(0.645 0.246 16.439) - pink

// SPECIAL COLORS
val FavoriteRed = Color(0xFFE91E63)       // Corazón de favoritos
val DiscountRed = Color(0xFFFF3B30)       // Badge de descuento
val StarGold = Color(0xFFFFB800)          // Estrellas de rating
val OnlineGreen = Color(0xFF4CAF50)       // Estado online

