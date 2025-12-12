# ui/favorites — Favoritos

Propósito

- Mostrar y gestionar la lista de productos marcados como favoritos por el usuario.

Archivos clave

- `FavoritesViewModel.kt` — expone `Flow<List<Product>>` de favoritos, operaciones: `toggleFavorite`, `removeFavorite`.
- `FavoritesScreen.kt` — UI con grid/list de productos y acciones.

Métodos clave

- `FavoriteRepository.getFavoritesForUser(userId): Flow<List<Product>>` — flujo reactivo.
- `FavoriteRepository.toggleFavorite(productId, userId)` — agrega o elimina favorito.

Notas

- Para rendimiento, cargar solo las vistas necesarias y usar paginación si la lista es grande.
