package com.shopply.appEcommerce.ui.productdetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.shopply.appEcommerce.R
import com.shopply.appEcommerce.ui.components.FavoriteButton

/**
 * ProductDetailScreen - Pantalla de detalles del producto
 *
 * Muestra:
 * - Información completa del producto
 * - Precio y stock
 * - Selector de cantidad
 * - Botón para agregar al carrito
 * - Botón de favoritos
 * - Información de la tienda
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit = {},
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val quantity by remember { derivedStateOf { viewModel.quantity } }
    val isFavorite by remember { derivedStateOf { viewModel.isFavorite } }

    // Snackbar
    val snackbarHostState = remember { SnackbarHostState() }

    // Mostrar mensaje si existe
    LaunchedEffect(uiState) {
        if (uiState is ProductDetailUiState.Success) {
            val successState = uiState as ProductDetailUiState.Success
            successState.message?.let { message ->
                snackbarHostState.showSnackbar(message)
                viewModel.clearMessage()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Producto") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = onCartClick) {
                        Icon(Icons.Default.ShoppingCart, "Carrito")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when (val state = uiState) {
            is ProductDetailUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is ProductDetailUiState.Success -> {
                ProductDetailContent(
                    modifier = Modifier.padding(paddingValues),
                    product = state.product,
                    storeName = state.storeName,
                    storeRating = state.storeRating,
                    quantity = quantity,
                    isFavorite = isFavorite,
                    isAddingToCart = state.isAddingToCart,
                    onIncrementQuantity = viewModel::incrementQuantity,
                    onDecrementQuantity = viewModel::decrementQuantity,
                    onToggleFavorite = viewModel::toggleFavorite,
                    onAddToCart = viewModel::addToCart
                )
            }

            is ProductDetailUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = onBackClick) {
                            Text("Volver")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductDetailContent(
    modifier: Modifier = Modifier,
    product: com.shopply.appEcommerce.data.local.entities.Product,
    storeName: String,
    storeRating: Float,
    quantity: Int,
    isFavorite: Boolean,
    isAddingToCart: Boolean,
    onIncrementQuantity: () -> Unit,
    onDecrementQuantity: () -> Unit,
    onToggleFavorite: () -> Unit,
    onAddToCart: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Imagen del producto
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.icon),
                error = painterResource(id = R.drawable.icon)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Título y botón de favorito
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                // Botón de favoritos usando el componente reutilizable
                FavoriteButton(
                    isFavorite = isFavorite,
                    onClick = onToggleFavorite
                )
            }

            // Información de la tienda
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Store,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = storeName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                if (storeRating > 0) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        text = String.format("%.1f", storeRating),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Precio
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "S/ ${String.format("%.2f", product.price)}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Stock disponible
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Inventory,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = if (product.stock > 0) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
                )
                Text(
                    text = if (product.stock > 0) {
                        "Stock disponible: ${product.stock} unidades"
                    } else {
                        "Sin stock"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (product.stock > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Descripción
            Text(
                text = "Descripción",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = product.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Selector de cantidad
            if (product.stock > 0) {
                Text(
                    text = "Cantidad",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Botón decrementar
                    OutlinedIconButton(
                        onClick = onDecrementQuantity,
                        enabled = quantity > 1
                    ) {
                        Icon(Icons.Default.Remove, "Decrementar")
                    }

                    // Cantidad
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = quantity.toString(),
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
                        )
                    }

                    // Botón incrementar
                    OutlinedIconButton(
                        onClick = onIncrementQuantity,
                        enabled = quantity < product.stock
                    ) {
                        Icon(Icons.Default.Add, "Incrementar")
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Subtotal
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Subtotal",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "S/ ${String.format("%.2f", product.price * quantity)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón agregar al carrito
                Button(
                    onClick = onAddToCart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !isAddingToCart && product.stock > 0
                ) {
                    if (isAddingToCart) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(Icons.Default.ShoppingCart, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Agregar al Carrito")
                    }
                }
            } else {
                // Producto sin stock
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReportProblem,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Producto agotado",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Espaciado final
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

