# ui/productdetail — Detalle de producto

Propósito

- Mostrar información completa del producto, tienda asociada, y acciones (añadir al carrito, favoritos).

Archivos clave

- `ProductDetailViewModel.kt` — carga producto + tienda, maneja acciones (favorito, añadir al carrito).
- `ProductDetailScreen.kt` — UI que representa detalle, imágenes y CTA.

Notas

- Manejar mensajes efímeros vía `UiState.message` y `LaunchedEffect`.
- Cargar imágenes por URI locales usando Coil.
