package com.shopply.appEcommerce.ui.store

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.shopply.appEcommerce.R
import com.shopply.appEcommerce.data.local.entities.Product

/**
 * StoreScreen - Pantalla de gestión de productos del vendedor
 *
 * Muestra:
 * - Estadísticas de productos (total, activos, pausados, sin stock)
 * - Búsqueda y filtros
 * - Lista de productos con acciones CRUD
 * - FAB para agregar nuevos productos
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreScreen(
    modifier: Modifier = Modifier,
    onProductClick: (Long) -> Unit = {},
    onAddProductClick: () -> Unit = {},
    onEditProductClick: (Long) -> Unit = {},
    viewModel: StoreViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showSearchBar by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Tienda") },
                actions = {
                    // Botón de búsqueda (solo si la tienda está aprobada)
                    if (uiState is StoreUiState.Success) {
                        IconButton(onClick = { showSearchBar = !showSearchBar }) {
                            Icon(
                                imageVector = if (showSearchBar) Icons.Default.Close else Icons.Default.Search,
                                contentDescription = if (showSearchBar) "Cerrar búsqueda" else "Buscar"
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            // Solo mostrar FAB si la tienda está aprobada
            if (uiState is StoreUiState.Success) {
                FloatingActionButton(
                    onClick = onAddProductClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Agregar producto",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is StoreUiState.Loading -> {
                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = "Cargando productos...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            is StoreUiState.Error -> {
                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Button(onClick = { viewModel.loadProducts() }) {
                            Text("Reintentar")
                        }
                    }
                }
            }

            is StoreUiState.Success -> {
                StoreContent(
                    modifier = modifier.padding(paddingValues),
                    products = state.products,
                    stats = stats,
                    selectedFilter = selectedFilter,
                    searchQuery = searchQuery,
                    showSearchBar = showSearchBar,
                    onSearchQueryChange = { viewModel.setSearchQuery(it) },
                    onFilterSelected = { viewModel.setFilter(it) },
                    onProductClick = onProductClick,
                    onEditProduct = onEditProductClick,
                    onPauseProduct = { viewModel.toggleProductStatus(it) },
                    onDuplicateProduct = { viewModel.duplicateProduct(it) },
                    onDeleteProduct = { showDeleteDialog = it }
                )
            }

            is StoreUiState.StorePending -> {
                StorePendingContent(
                    modifier = modifier.padding(paddingValues),
                    storeName = state.store.name
                )
            }

            is StoreUiState.StoreRejected -> {
                StoreRejectedContent(
                    modifier = modifier.padding(paddingValues),
                    storeName = state.store.name,
                    reason = state.reason
                )
            }

            is StoreUiState.NoStore -> {
                NoStoreContent(
                    modifier = modifier.padding(paddingValues)
                )
            }
        }
    }

    // Diálogo de confirmación para eliminar
    showDeleteDialog?.let { productId ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            icon = {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Eliminar Producto") },
            text = {
                Text("¿Estás seguro de eliminar este producto? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteProduct(productId)
                        showDeleteDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun StoreContent(
    modifier: Modifier = Modifier,
    products: List<Product>,
    stats: ProductStats,
    selectedFilter: ProductFilter,
    searchQuery: String,
    showSearchBar: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onFilterSelected: (ProductFilter) -> Unit,
    onProductClick: (Long) -> Unit,
    onEditProduct: (Long) -> Unit,
    onPauseProduct: (Long) -> Unit,
    onDuplicateProduct: (Long) -> Unit,
    onDeleteProduct: (Long) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 88.dp) // Espacio para FAB
    ) {
        // Barra de búsqueda (si está activa)
        if (showSearchBar) {
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Buscar productos...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, "Buscar")
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Default.Clear, "Limpiar")
                            }
                        }
                    },
                    singleLine = true,
                    shape = MaterialTheme.shapes.large
                )
            }
        }

        // Tarjetas de estadísticas
        item {
            StatsCards(stats = stats)
        }

        // Chips de filtros
        item {
            FilterChips(
                selectedFilter = selectedFilter,
                onFilterSelected = onFilterSelected
            )
        }

        // Lista de productos o estado vacío
        if (products.isEmpty()) {
            item {
                EmptyProductsState(
                    modifier = Modifier.padding(32.dp),
                    hasFilter = selectedFilter != ProductFilter.ALL || searchQuery.isNotEmpty()
                )
            }
        } else {
            items(
                items = products,
                key = { it.id }
            ) { product ->
                ProductCard(
                    product = product,
                    onClick = { onProductClick(product.id) },
                    onEdit = { onEditProduct(product.id) },
                    onPause = { onPauseProduct(product.id) },
                    onDuplicate = { onDuplicateProduct(product.id) },
                    onDelete = { onDeleteProduct(product.id) }
                )
            }
        }
    }
}

@Composable
private fun StatsCards(stats: ProductStats) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Resumen",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                title = "Total",
                value = stats.total.toString(),
                icon = Icons.Default.Inventory,
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )

            StatCard(
                title = "Activos",
                value = stats.active.toString(),
                icon = Icons.Default.CheckCircle,
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )

            StatCard(
                title = "Pausados",
                value = stats.paused.toString(),
                icon = Icons.Default.Pause,
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor.copy(alpha = 0.8f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterChips(
    selectedFilter: ProductFilter,
    onFilterSelected: (ProductFilter) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedFilter == ProductFilter.ALL,
                onClick = { onFilterSelected(ProductFilter.ALL) },
                label = { Text("Todos") },
                leadingIcon = if (selectedFilter == ProductFilter.ALL) {
                    { Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp)) }
                } else null
            )
        }

        item {
            FilterChip(
                selected = selectedFilter == ProductFilter.ACTIVE,
                onClick = { onFilterSelected(ProductFilter.ACTIVE) },
                label = { Text("Activos") },
                leadingIcon = if (selectedFilter == ProductFilter.ACTIVE) {
                    { Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp)) }
                } else null
            )
        }

        item {
            FilterChip(
                selected = selectedFilter == ProductFilter.PAUSED,
                onClick = { onFilterSelected(ProductFilter.PAUSED) },
                label = { Text("Pausados") },
                leadingIcon = if (selectedFilter == ProductFilter.PAUSED) {
                    { Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp)) }
                } else null
            )
        }

        item {
            FilterChip(
                selected = selectedFilter == ProductFilter.OUT_OF_STOCK,
                onClick = { onFilterSelected(ProductFilter.OUT_OF_STOCK) },
                label = { Text("Sin Stock") },
                leadingIcon = if (selectedFilter == ProductFilter.OUT_OF_STOCK) {
                    { Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp)) }
                } else null
            )
        }
    }
}

@Composable
private fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onPause: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp,
            pressedElevation = 3.dp,
            hoveredElevation = 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = MaterialTheme.shapes.large // 12dp del theme
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Imagen del producto con diseño mejorado
            Card(
                modifier = Modifier.size(90.dp),
                shape = MaterialTheme.shapes.medium, // 10dp del theme
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.icon),
                        error = painterResource(id = R.drawable.icon)
                    )

                    // Overlay sutil si está pausado
                    if (!product.isActive) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Pause,
                                contentDescription = "Pausado",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }

            // Información del producto
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Nombre del producto
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Precio destacado con el color primario del theme
                Text(
                    text = "S/ ${String.format("%.2f", product.price)}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )

                // Chips de estado con colores del theme
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Stock chip
                    StockChipThemed(stock = product.stock)

                    // Status chip
                    StatusChipThemed(isActive = product.isActive, stock = product.stock)
                }
            }

            // Menú contextual con mejor integración
            Box(
                modifier = Modifier.offset(x = 4.dp, y = (-4).dp)
            ) {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Opciones",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                ProductActionsMenu(
                    expanded = showMenu,
                    onDismiss = { showMenu = false },
                    product = product,
                    onEdit = {
                        onEdit()
                        showMenu = false
                    },
                    onPause = {
                        onPause()
                        showMenu = false
                    },
                    onDuplicate = {
                        onDuplicate()
                        showMenu = false
                    },
                    onDelete = {
                        onDelete()
                        showMenu = false
                    }
                )
            }
        }
    }
}

// Chip de stock usando colores del theme de ShopPly
@Composable
private fun StockChipThemed(stock: Int) {
    val (backgroundColor, contentColor, icon) = when {
        stock == 0 -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            Icons.Default.Close
        )
        stock < 10 -> Triple(
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer,
            Icons.Default.Warning
        )
        else -> Triple(
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer,
            Icons.Default.CheckCircle
        )
    }

    Surface(
        shape = MaterialTheme.shapes.small, // 8dp del theme
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = "$stock",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = contentColor
            )
        }
    }
}

// Chip de estado usando colores del theme de ShopPly
@Composable
private fun StatusChipThemed(isActive: Boolean, stock: Int) {
    val (backgroundColor, contentColor, text) = when {
        stock == 0 -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.error,
            "Agotado"
        )
        !isActive -> Triple(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
            "Pausado"
        )
        else -> Triple(
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.secondary,
            "Activo"
        )
    }

    Surface(
        shape = MaterialTheme.shapes.small, // 8dp del theme
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Indicador circular de estado
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(
                        color = contentColor,
                        shape = CircleShape
                    )
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = contentColor
            )
        }
    }
}


@Composable
private fun ProductActionsMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    product: Product,
    onEdit: () -> Unit,
    onPause: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        DropdownMenuItem(
            text = { Text("Editar") },
            onClick = onEdit,
            leadingIcon = {
                Icon(Icons.Default.Edit, null)
            }
        )

        DropdownMenuItem(
            text = {
                Text(if (product.isActive) "Pausar" else "Activar")
            },
            onClick = onPause,
            leadingIcon = {
                Icon(
                    if (product.isActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                    null
                )
            }
        )

        DropdownMenuItem(
            text = { Text("Duplicar") },
            onClick = onDuplicate,
            leadingIcon = {
                Icon(Icons.Default.ContentCopy, null)
            }
        )

        HorizontalDivider()

        DropdownMenuItem(
            text = { Text("Eliminar") },
            onClick = onDelete,
            leadingIcon = {
                Icon(
                    Icons.Default.Delete,
                    null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            colors = MenuDefaults.itemColors(
                textColor = MaterialTheme.colorScheme.error
            )
        )
    }
}

@Composable
private fun EmptyProductsState(
    modifier: Modifier = Modifier,
    hasFilter: Boolean
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = if (hasFilter) Icons.Default.SearchOff else Icons.Default.Store,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )

        Text(
            text = if (hasFilter) "No se encontraron productos" else "No tienes productos aún",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Text(
            text = if (hasFilter) {
                "Intenta con otros filtros o términos de búsqueda"
            } else {
                "Comienza agregando tu primer producto usando el botón +"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Contenido cuando la tienda está pendiente de aprobación
 */
@Composable
private fun StorePendingContent(
    modifier: Modifier = Modifier,
    storeName: String
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Icono de reloj/espera
                Icon(
                    imageVector = Icons.Default.HourglassEmpty,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.tertiary
                )

                Text(
                    text = "Tienda Pendiente",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )

                Text(
                    text = "\"$storeName\"",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                )

                Text(
                    text = "Tu tienda está siendo revisada por nuestro equipo de administración. " +
                            "Este proceso puede tomar entre 24 a 48 horas.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Mientras tanto puedes:",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text("• Preparar las fotos de tus productos", style = MaterialTheme.typography.bodySmall)
                        Text("• Escribir las descripciones", style = MaterialTheme.typography.bodySmall)
                        Text("• Definir tus precios y stock", style = MaterialTheme.typography.bodySmall)
                    }
                }

                Text(
                    text = "Te notificaremos cuando tu tienda sea aprobada",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Contenido cuando la tienda fue rechazada
 */
@Composable
private fun StoreRejectedContent(
    modifier: Modifier = Modifier,
    storeName: String,
    reason: String
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Icono de bloqueo
                Icon(
                    imageVector = Icons.Default.Block,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.error
                )

                Text(
                    text = "Tienda Rechazada",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )

                Text(
                    text = "\"$storeName\"",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                )

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Razón del rechazo:",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = reason,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Text(
                    text = "Si crees que fue un error, contacta a soporte para más información.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Contenido cuando el usuario no tiene tienda
 */
@Composable
private fun NoStoreContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Storefront,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "No tienes una tienda",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Parece que tu cuenta no tiene una tienda asociada. " +
                            "Si eres vendedor, por favor contacta a soporte.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
