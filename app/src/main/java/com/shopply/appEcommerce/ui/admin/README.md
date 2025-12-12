# ui/admin — Panel de administración

Propósito

- Vistas para administradores: revisión de tiendas (PENDING → APPROVED/REJECTED), gestión de categorías.

Archivos clave

- `AdminStoresViewModel.kt` — lista y acciones sobre tiendas pendientes.
- `AdminStoresScreen.kt` — UI para aprobar/rechazar tiendas.
- `AdminCategoriesViewModel.kt` — CRUD de categorías.

Consideraciones

- Asegurar que sólo `UserRole.ADMIN` pueda acceder a estas rutas.
- Acciones administrativas deben registrar auditoría si corresponde (extensión futura).
