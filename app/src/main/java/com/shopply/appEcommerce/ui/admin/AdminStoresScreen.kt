package com.shopply.appEcommerce.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shopply.appEcommerce.data.local.entities.Store
import com.shopply.appEcommerce.data.local.entities.StoreStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * AdminStoresScreen - Pantalla de administración de tiendas (Solo Admin)
 *
 * Características:
 * - Ver todas las tiendas
 * - Filtrar por estado (Pendientes, Aprobadas, Rechazadas)
 * - Aprobar tiendas pendientes
 * - Rechazar tiendas con razón
 * - Ver estadísticas generales
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminStoresScreen(
    modifier: Modifier = Modifier,
    viewModel: AdminStoresViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Mostrar mensajes
    LaunchedEffect(uiState) {
        if (uiState is AdminStoresUiState.Success) {
            val successState = uiState as AdminStoresUiState.Success
            successState.message?.let { message ->
                snackbarHostState.showSnackbar(message)
                viewModel.clearMessage()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Gestión de Tiendas",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        if (uiState is AdminStoresUiState.Success) {
                            val stats = (uiState as AdminStoresUiState.Success).stats
                            Text(
                                "${stats.totalStores} tiendas registradas",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::refresh) {
                        Icon(Icons.Default.Refresh, "Refrescar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when (val state = uiState) {
            is AdminStoresUiState.Loading -> {
                LoadingState(modifier.padding(paddingValues))
            }
            is AdminStoresUiState.Success -> {
                AdminStoresContent(
                    modifier = modifier.padding(paddingValues),
                    stores = state.stores,
                    stats = state.stats,
                    currentFilter = viewModel.currentFilter,
                    onFilterChange = viewModel::changeFilter,
                    onApproveClick = viewModel::showApproveDialog,
                    onRejectClick = viewModel::showRejectDialog
                )
            }
            is AdminStoresUiState.Error -> {
                ErrorState(
                    modifier = modifier.padding(paddingValues),
                    message = state.message,
                    onRetry = viewModel::refresh
                )
            }
        }
    }

    // Diálogo de aprobación
    if (viewModel.showApproveDialog && viewModel.selectedStore != null) {
        ApproveStoreDialog(
            store = viewModel.selectedStore!!,
            onDismiss = viewModel::hideApproveDialog,
            onConfirm = viewModel::approveStore
        )
    }

    // Diálogo de rechazo
    if (viewModel.showRejectDialog && viewModel.selectedStore != null) {
        RejectStoreDialog(
            store = viewModel.selectedStore!!,
            reason = viewModel.rejectionReason,
            onReasonChange = viewModel::updateRejectionReason,
            onDismiss = viewModel::hideRejectDialog,
            onConfirm = viewModel::rejectStore
        )
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(
    modifier: Modifier = Modifier,
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Default.Error,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
            Button(onClick = onRetry) {
                Icon(Icons.Default.Refresh, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reintentar")
            }
        }
    }
}

@Composable
private fun AdminStoresContent(
    modifier: Modifier = Modifier,
    stores: List<Store>,
    stats: com.shopply.appEcommerce.data.repository.StoreStats,
    currentFilter: StoreFilter,
    onFilterChange: (StoreFilter) -> Unit,
    onApproveClick: (Store) -> Unit,
    onRejectClick: (Store) -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Estadísticas
        StatsSection(stats = stats)

        // Filtros
        FilterChips(
            currentFilter = currentFilter,
            onFilterChange = onFilterChange,
            stats = stats
        )

        // Lista de tiendas
        if (stores.isEmpty()) {
            EmptyState(currentFilter = currentFilter)
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = stores,
                    key = { it.id }
                ) { store ->
                    StoreCard(
                        store = store,
                        onApproveClick = { onApproveClick(store) },
                        onRejectClick = { onRejectClick(store) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StatsSection(
    stats: com.shopply.appEcommerce.data.repository.StoreStats
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                icon = Icons.Default.Store,
                label = "Total",
                value = stats.totalStores.toString(),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            StatItem(
                icon = Icons.Default.HourglassEmpty,
                label = "Pendientes",
                value = stats.pendingStores.toString(),
                color = MaterialTheme.colorScheme.tertiary
            )
            StatItem(
                icon = Icons.Default.CheckCircle,
                label = "Aprobadas",
                value = stats.approvedStores.toString(),
                color = Color(0xFF4CAF50)
            )
            StatItem(
                icon = Icons.Default.Cancel,
                label = "Rechazadas",
                value = stats.rejectedStores.toString(),
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterChips(
    currentFilter: StoreFilter,
    onFilterChange: (StoreFilter) -> Unit,
    stats: com.shopply.appEcommerce.data.repository.StoreStats
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = currentFilter == StoreFilter.PENDING,
            onClick = { onFilterChange(StoreFilter.PENDING) },
            label = { Text("Pendientes (${stats.pendingStores})") },
            leadingIcon = if (currentFilter == StoreFilter.PENDING) {
                { Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp)) }
            } else null
        )

        FilterChip(
            selected = currentFilter == StoreFilter.APPROVED,
            onClick = { onFilterChange(StoreFilter.APPROVED) },
            label = { Text("Aprobadas (${stats.approvedStores})") },
            leadingIcon = if (currentFilter == StoreFilter.APPROVED) {
                { Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp)) }
            } else null
        )

        FilterChip(
            selected = currentFilter == StoreFilter.REJECTED,
            onClick = { onFilterChange(StoreFilter.REJECTED) },
            label = { Text("Rechazadas (${stats.rejectedStores})") },
            leadingIcon = if (currentFilter == StoreFilter.REJECTED) {
                { Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp)) }
            } else null
        )
    }
}

@Composable
private fun EmptyState(currentFilter: StoreFilter) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Store,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Text(
                text = when (currentFilter) {
                    StoreFilter.ALL -> "No hay tiendas registradas"
                    StoreFilter.PENDING -> "No hay tiendas pendientes"
                    StoreFilter.APPROVED -> "No hay tiendas aprobadas"
                    StoreFilter.REJECTED -> "No hay tiendas rechazadas"
                },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StoreCard(
    store: Store,
    onApproveClick: () -> Unit,
    onRejectClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header con nombre y estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = store.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "RUC: ${store.ruc}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Badge de estado
                StatusBadge(status = store.status)
            }

            // Descripción
            Text(
                text = store.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Información adicional
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InfoChip(
                    icon = Icons.Default.Phone,
                    text = store.phone
                )
                InfoChip(
                    icon = Icons.Default.CalendarToday,
                    text = formatDate(store.createdAt)
                )
            }

            // Razón de rechazo si aplica
            if (store.status == StoreStatus.REJECTED && store.rejectionReason != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Razón: ${store.rejectionReason}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            // Botones de acción (solo para tiendas pendientes)
            if (store.status == StoreStatus.PENDING) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onRejectClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.Default.Cancel, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Rechazar")
                    }

                    Button(
                        onClick = onApproveClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Aprobar")
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: StoreStatus) {
    val (color, icon, text) = when (status) {
        StoreStatus.PENDING -> Triple(
            MaterialTheme.colorScheme.tertiaryContainer,
            Icons.Default.HourglassEmpty,
            "Pendiente"
        )
        StoreStatus.APPROVED -> Triple(
            Color(0xFF4CAF50).copy(alpha = 0.2f),
            Icons.Default.CheckCircle,
            "Aprobada"
        )
        StoreStatus.REJECTED -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            Icons.Default.Cancel,
            "Rechazada"
        )
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = color
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = when (status) {
                    StoreStatus.PENDING -> MaterialTheme.colorScheme.onTertiaryContainer
                    StoreStatus.APPROVED -> Color(0xFF4CAF50)
                    StoreStatus.REJECTED -> MaterialTheme.colorScheme.onErrorContainer
                }
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = when (status) {
                    StoreStatus.PENDING -> MaterialTheme.colorScheme.onTertiaryContainer
                    StoreStatus.APPROVED -> Color(0xFF4CAF50)
                    StoreStatus.REJECTED -> MaterialTheme.colorScheme.onErrorContainer
                }
            )
        }
    }
}

@Composable
private fun InfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ApproveStoreDialog(
    store: Store,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF4CAF50)
            )
        },
        title = { Text("Aprobar Tienda") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "¿Estás seguro de aprobar esta tienda?",
                    style = MaterialTheme.typography.bodyLarge
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = store.name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "RUC: ${store.ruc}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = store.phone,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Text(
                    text = "La tienda será visible para todos los compradores.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Aprobar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun RejectStoreDialog(
    store: Store,
    reason: String,
    onReasonChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Cancel,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = { Text("Rechazar Tienda") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Vas a rechazar la tienda:",
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = store.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = reason,
                    onValueChange = onReasonChange,
                    label = { Text("Razón del rechazo") },
                    placeholder = { Text("Ej: Documentación incompleta...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )

                Text(
                    text = "El vendedor podrá ver esta razón.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Rechazar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

