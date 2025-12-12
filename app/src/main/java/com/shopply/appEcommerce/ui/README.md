# ui — Arquitectura de UI y patrones

Propósito

- Contiene pantallas implementadas con Jetpack Compose, ViewModels y NavGraph.

Estructura

- `main/` — `MainActivity`, `MainViewModel`, inicio de la app.
- `navigation/` — `NavGraph.kt`, `Screen.kt`, `BottomNavItem.kt`.
- `auth/`, `home/`, `store/`, `productdetail/`, `admin/` — features con pantallas y ViewModels.
- `components/` — componentes reutilizables (cards, inputs, dialogs).

Patrones recomendados

- MVVM: ViewModel + StateFlow para la UI.
- Un único `UiState` sellado por pantalla con campos `loading`, `data`, `message`.
- Usar `LaunchedEffect` en las pantallas para interceptar `message` y mostrar Snackbars.

Cómo añadir una pantalla

1. Crear `XxxViewModel` y exponer un `StateFlow<UiState>`.
2. Añadir la `@Composable XxxScreen(...)` en la carpeta feature.
3. Registrar la ruta en `NavGraph.kt` y pasar los handlers necesarios.
