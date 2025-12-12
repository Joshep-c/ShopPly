# ui/store — Gestión de tienda (SELLER)

Propósito

- Pantallas para que un vendedor gestione su tienda y productos.

Archivos clave

- `StoreViewModel.kt` — gestiona productos por tienda, estadísticas y operaciones CRUD.
- `StoreScreen.kt` — vista principal de la tienda del vendedor.
- `AddEditProductScreen.kt` — formulario para crear/editar productos.

Consideraciones

- Verifica que el usuario sea `UserRole.SELLER` antes de permitir acciones.
- Asegurar que la `Store` esté `APPROVED` para permitir publicación de productos; si la tienda está `PENDING` mostrar un mensaje apropiado.
