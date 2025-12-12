# di — Inyección de dependencias (Hilt)

Propósito

- Define cómo se construyen y proveen las dependencias de la app (Room, repositorios, DataStore).

Archivos clave

- `DatabaseModule.kt` — proveedor de `AppDatabase` y DAOs.
- `RepositoryModule.kt` — bindings de repositorios para inyección.
- `DataStoreModule.kt` — proveedor de `DataStore<Preferences>` para sesión.

Puntos importantes

- `@HiltAndroidApp` se encuentra en `ShopPlyApplication.kt`.
- Evitar crear singletons con estado mutable fuera de Hilt.

Cómo extender

- Para exponer un nuevo DAO: añadir la DAO al `AppDatabase`, crear el provider en `DatabaseModule` y vincular el repositorio en `RepositoryModule`.
