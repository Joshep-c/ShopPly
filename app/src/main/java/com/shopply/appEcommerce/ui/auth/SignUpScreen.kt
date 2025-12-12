package com.shopply.appEcommerce.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

/**
 * SignUpScreen - Pantalla de registro
 */
@Composable
fun SignUpScreen(
    viewModel: AuthViewModel,
    onLoginClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Título
        Text(
            text = "Crear Cuenta",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Selector de tipo de cuenta
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Tipo de Cuenta",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Botón Comprador
                    FilterChip(
                        selected = !viewModel.isBusinessAccount,
                        onClick = { if (viewModel.isBusinessAccount) viewModel.toggleBusinessAccount() },
                        label = { Text("Comprador") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Comprador"
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )

                    // Botón Vendedor
                    FilterChip(
                        selected = viewModel.isBusinessAccount,
                        onClick = { if (!viewModel.isBusinessAccount) viewModel.toggleBusinessAccount() },
                        label = { Text("Vendedor (PYME)") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Store,
                                contentDescription = "Vendedor"
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (viewModel.isBusinessAccount)
                        "Podrás crear tu tienda y vender productos"
                    else
                        "Podrás comprar productos de PYMEs peruanas",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Nombre completo
        OutlinedTextField(
            value = viewModel.registerName,
            onValueChange = viewModel::updateRegisterName,
            label = { Text("Nombre completo") },
            placeholder = { Text("Juan Pérez") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = uiState !is AuthUiState.Loading,
            isError = uiState is AuthUiState.Error
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Email
        OutlinedTextField(
            value = viewModel.registerEmail,
            onValueChange = viewModel::updateRegisterEmail,
            label = { Text("Correo electrónico") },
            placeholder = { Text("ejemplo@correo.com") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = uiState !is AuthUiState.Loading,
            isError = uiState is AuthUiState.Error
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Teléfono (opcional)
        OutlinedTextField(
            value = viewModel.registerPhone,
            onValueChange = viewModel::updateRegisterPhone,
            label = { Text("Teléfono (opcional)") },
            placeholder = { Text("987654321") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = uiState !is AuthUiState.Loading,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        // Campos adicionales para vendedores (tienda)
        AnimatedVisibility(visible = viewModel.isBusinessAccount) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                // Separador con título
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "📦 Información de tu Tienda",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            text = "Tu tienda quedará pendiente de aprobación",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }

                // Nombre de la tienda
                OutlinedTextField(
                    value = viewModel.storeName,
                    onValueChange = viewModel::updateStoreName,
                    label = { Text("Nombre de la Tienda*") },
                    placeholder = { Text("Mi Tienda PYME") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = uiState !is AuthUiState.Loading,
                    leadingIcon = {
                        Icon(Icons.Default.Store, contentDescription = null)
                    },
                    isError = uiState is AuthUiState.Error && viewModel.storeName.isBlank()
                )

                // RUC
                OutlinedTextField(
                    value = viewModel.storeRuc,
                    onValueChange = viewModel::updateStoreRuc,
                    label = { Text("RUC (11 dígitos)*") },
                    placeholder = { Text("20123456789") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = uiState !is AuthUiState.Loading,
                    leadingIcon = {
                        Icon(Icons.Default.Numbers, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    supportingText = {
                        Text("${viewModel.storeRuc.length}/11 dígitos")
                    },
                    isError = uiState is AuthUiState.Error &&
                        (viewModel.storeRuc.length != 11 || !viewModel.storeRuc.all { it.isDigit() })
                )

                // Descripción de la tienda (opcional)
                OutlinedTextField(
                    value = viewModel.storeDescription,
                    onValueChange = viewModel::updateStoreDescription,
                    label = { Text("Descripción de la tienda (opcional)") },
                    placeholder = { Text("¿Qué vendes? ¿Qué hace especial a tu negocio?") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    enabled = uiState !is AuthUiState.Loading,
                    leadingIcon = {
                        Icon(Icons.Default.Description, contentDescription = null)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Contraseña
        OutlinedTextField(
            value = viewModel.registerPassword,
            onValueChange = viewModel::updateRegisterPassword,
            label = { Text("Contraseña") },
            placeholder = { Text("Mínimo 6 caracteres") },
            visualTransformation = if (passwordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible)
                            Icons.Filled.Visibility
                        else
                            Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible)
                            "Ocultar contraseña"
                        else
                            "Mostrar contraseña"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = uiState !is AuthUiState.Loading,
            isError = uiState is AuthUiState.Error
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Confirmar contraseña
        OutlinedTextField(
            value = viewModel.registerConfirmPassword,
            onValueChange = viewModel::updateRegisterConfirmPassword,
            label = { Text("Confirmar contraseña") },
            placeholder = { Text("Repite tu contraseña") },
            visualTransformation = if (confirmPasswordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                        imageVector = if (confirmPasswordVisible)
                            Icons.Filled.Visibility
                        else
                            Icons.Filled.VisibilityOff,
                        contentDescription = if (confirmPasswordVisible)
                            "Ocultar contraseña"
                        else
                            "Mostrar contraseña"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = uiState !is AuthUiState.Loading,
            isError = uiState is AuthUiState.Error
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botón de Registro
        Button(
            onClick = { viewModel.register() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = uiState !is AuthUiState.Loading
        ) {
            if (uiState is AuthUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(
                    text = "Crear Cuenta",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de Login
        TextButton(onClick = onLoginClick) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }

        // Mensaje de error
        if (uiState is AuthUiState.Error) {
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = (uiState as AuthUiState.Error).message,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Limpiar error después de mostrarlo
            LaunchedEffect(uiState) {
                kotlinx.coroutines.delay(3000)
                viewModel.clearError()
            }
        }

        // Mensaje de éxito
        if (uiState is AuthUiState.Success) {
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = (uiState as AuthUiState.Success).message,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}