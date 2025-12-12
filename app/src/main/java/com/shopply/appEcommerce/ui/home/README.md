# ui/home — Pantalla Home

Propósito

- Mostrar feed de productos, banners, categorías y recomendaciones.

Archivos clave

- `HomeViewModel.kt` — expone productos destacados y estados de UI.
- `HomeScreen.kt` — composable principal que arma secciones (banners, categorías, productos).

Notas de diseño

- Reutilizar `ProductCard` desde `components/`.
- Paginación si la lista crece (considerar Paging 3).
