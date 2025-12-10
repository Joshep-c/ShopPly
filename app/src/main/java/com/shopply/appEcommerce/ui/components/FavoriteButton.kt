package com.shopply.appEcommerce.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


// FavoriteButton - Botón de favoritos reutilizable

@Composable
fun FavoriteButton(
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
) {
    // Animación de escala
    val scale by animateFloatAsState(
        targetValue = if (isFavorite) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = 0.5f,
            stiffness = 300f
        ),
        label = "scale"
    )

    // Animación de color
    val iconColor by animateColorAsState(
        targetValue = if (isFavorite) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        },
        label = "color"
    )

    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = backgroundColor,
        shadowElevation = 4.dp
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = if (isFavorite) {
                    Icons.Default.Favorite
                } else {
                    Icons.Default.FavoriteBorder
                },
                contentDescription = if (isFavorite) {
                    "Eliminar de favoritos"
                } else {
                    "Agregar a favoritos"
                },
                tint = iconColor,
                modifier = Modifier
                    .size(20.dp)
                    .scale(scale)
            )
        }
    }
}

