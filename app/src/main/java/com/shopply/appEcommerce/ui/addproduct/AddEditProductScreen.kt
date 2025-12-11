package com.shopply.appEcommerce.ui.addproduct

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shopply.appEcommerce.data.local.entities.Category

/**
 * AddEditProductScreen - Formulario para agregar o editar productos
 *
 * Características:
 * - Crear nuevo producto
 * - Editar producto existente
 * - Validaciones en tiempo real
 * - Guardado en base de datos
 * - Integrado con ViewModel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductScreen(
    productId: String? = null,
    onBackClick: () -> Unit = {},
    onProductSaved: (Long) -> Unit = {},
    viewModel: AddEditProductViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var savedProductId by remember { mutableStateOf(0L) }

    // Mostrar mensajes
    LaunchedEffect(uiState) {
        if (uiState is AddEditProductUiState.Success) {
            val successState = uiState as AddEditProductUiState.Success
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
                    Text(if (viewModel.isEditing) "Editar Producto" else "Nuevo Producto")
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when (val state = uiState) {
            is AddEditProductUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is AddEditProductUiState.Error -> {
                Box(
                    modifier = Modifier
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
                            Icons.Default.Error,
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
            is AddEditProductUiState.Success -> {
                ProductForm(
                    modifier = Modifier.padding(paddingValues),
                    categories = state.categories,
                    viewModel = viewModel,
                    onSaveClick = {
                        viewModel.saveProduct { productId ->
                            savedProductId = productId
                            showSuccessDialog = true
                        }
                    }
                )
            }
        }
    }

    // Diálogo de éxito
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            icon = {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = {
                Text(if (viewModel.isEditing) "Producto Actualizado" else "Producto Creado")
            },
            text = {
                Text("El producto se ha guardado exitosamente.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onProductSaved(savedProductId)
                    }
                ) {
                    Text("Aceptar")
                }
            }
        )
    }
}

@Composable
private fun ProductForm(
    modifier: Modifier = Modifier,
    categories: List<Category>,
    viewModel: AddEditProductViewModel,
    onSaveClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // SECCIÓN: URL DE IMAGEN
        Text(
            text = "Imagen del Producto",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = viewModel.imageUrl,
            onValueChange = viewModel::updateImageUrl,
            label = { Text("URL de la Imagen*") },
            placeholder = { Text("https://ejemplo.com/imagen.jpg") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(Icons.Default.Image, null)
            },
            supportingText = {
                Text("Pega aquí la URL de la imagen del producto")
            },
            singleLine = true
        )

        HorizontalDivider()

        // SECCIÓN: INFORMACIÓN BÁSICA
        Text(
            text = "Información del Producto",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        // Nombre
        OutlinedTextField(
            value = viewModel.name,
            onValueChange = viewModel::updateName,
            label = { Text("Nombre del Producto*") },
            placeholder = { Text("Ej: iPhone 15 Pro Max 256GB") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Default.ShoppingBag, null)
            },
            supportingText = {
                Text("${viewModel.name.length}/80 caracteres")
            }
        )

        // Categoría
        CategoryDropdown(
            categories = categories,
            selectedCategory = viewModel.selectedCategory,
            onCategorySelected = viewModel::updateCategory
        )

        // Precio
        OutlinedTextField(
            value = viewModel.price,
            onValueChange = viewModel::updatePrice,
            label = { Text("Precio (S/)*") },
            placeholder = { Text("0.00") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            leadingIcon = {
                Text(
                    text = "S/",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 12.dp)
                )
            },
            supportingText = {
                Text("Ingresa el precio de venta")
            },
            singleLine = true
        )

        // Stock
        OutlinedTextField(
            value = viewModel.stock,
            onValueChange = viewModel::updateStock,
            label = { Text("Stock Disponible*") },
            placeholder = { Text("0") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            leadingIcon = {
                Icon(Icons.Default.Inventory, null)
            },
            supportingText = {
                Text("Cantidad de unidades disponibles para venta")
            },
            singleLine = true
        )

        // Descripción
        OutlinedTextField(
            value = viewModel.description,
            onValueChange = viewModel::updateDescription,
            label = { Text("Descripción*") },
            placeholder = { Text("Describe las características del producto...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            maxLines = 6,
            leadingIcon = {
                Icon(Icons.Default.Description, null)
            },
            supportingText = {
                Text("${viewModel.description.length}/500 caracteres")
            }
        )

        HorizontalDivider()

        // SECCIÓN: ESTADO (solo en edición)
        if (viewModel.isEditing) {
            Text(
                text = "Estado del Producto",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Producto Activo",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = if (viewModel.isActive) "Visible en la tienda" else "Oculto en la tienda",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = viewModel.isActive,
                    onCheckedChange = viewModel::updateIsActive
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // BOTÓN DE GUARDAR
        Button(
            onClick = onSaveClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = !viewModel.isSaving
        ) {
            if (viewModel.isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Guardando...")
            } else {
                Icon(
                    if (viewModel.isEditing) Icons.Default.Done else Icons.Default.Upload,
                    null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (viewModel.isEditing) "Guardar Cambios" else "Publicar Producto")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdown(
    categories: List<Category>,
    selectedCategory: Category?,
    onCategorySelected: (Category) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedCategory?.name ?: "Seleccionar categoría",
            onValueChange = {},
            readOnly = true,
            label = { Text("Categoría*") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            leadingIcon = {
                Icon(Icons.Default.Category, null)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.name) },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = when (category.name) {
                                "Electrónica" -> Icons.Default.Devices
                                "Ropa y Moda" -> Icons.Default.Checkroom
                                "Hogar y Jardín" -> Icons.Default.Home
                                "Deportes" -> Icons.Default.FitnessCenter
                                "Artesanía" -> Icons.Default.Palette
                                "Alimentos" -> Icons.Default.Restaurant
                                "Salud y Belleza" -> Icons.Default.HealthAndSafety
                                "Juguetes" -> Icons.Default.Toys
                                else -> Icons.Default.Category
                            },
                            contentDescription = null
                        )
                    }
                )
            }
        }
    }
}

