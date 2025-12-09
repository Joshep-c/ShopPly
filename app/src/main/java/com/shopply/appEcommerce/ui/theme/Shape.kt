package com.shopply.appEcommerce.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    // --radius-sm: calc(var(--radius) - 4px) = 8dp
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),

    // --radius-md: calc(var(--radius) - 2px) = 10dp
    medium = RoundedCornerShape(10.dp),

    // --radius-lg: var(--radius) = 12dp
    large = RoundedCornerShape(12.dp),

    // --radius-xl: calc(var(--radius) + 4px) = 16dp
    extraLarge = RoundedCornerShape(16.dp)
)

