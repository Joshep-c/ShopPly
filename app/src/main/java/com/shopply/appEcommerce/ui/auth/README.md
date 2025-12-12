# ui/auth — Autenticación (Login / SignUp)

Propósito

- Pantallas y ViewModels para login, registro y selección de rol (BUYER/SELLER).

Componentes

- `AuthViewModel.kt` — maneja validación, login y registro.
- `AuthScreen.kt`, `LoginScreen.kt`, `SignUpScreen.kt` — composables para interacción del usuario.

Flujo típico (registro vendedor)

1. Usuario elige "Crear cuenta" y marca tipo "Vendedor".
2. `AuthViewModel.register()` llama a `UserRepository.register(...)`.
3. Si la app debe crear una tienda automáticamente, esto se debe orquestar tras el registro (ver `StoreRepository`).

Eventos y mensajes

- La UI usa `UiState.message` para Snackbars; usar `LaunchedEffect` para consumirlos y limpiar el mensaje.
