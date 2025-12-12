# ui/profile — Perfil de usuario

Propósito

- Mostrar y editar información del usuario; permitir logout y, si aplica, acceso a la gestión de tienda.

Archivos clave

- `ProfileViewModel.kt` — obtiene `getCurrentUser()` desde `UserRepository`, maneja `logout()`.
- `ProfileScreen.kt` — muestra datos (nombre, email, rol, teléfono) y acciones.

Métodos clave

- `UserRepository.getCurrentUser(): Flow<User?>` — flujo con el usuario actual.
- `UserRepository.logout()` — limpia sesión en DataStore.

Comportamiento dependiendo del rol

- `SELLER`: mostrar enlace a `StoreScreen` (si la tienda existe y está APPROVED) o mostrar mensaje si está PENDING.
- `ADMIN`: acceso a panel admin.
