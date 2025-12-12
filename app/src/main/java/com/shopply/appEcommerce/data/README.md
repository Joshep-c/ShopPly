# data (app-specific)

Propósito

- Implementa la capa de datos consumida por el módulo `app`.
- Exponer repositorios, DataStore session, y adaptadores a Room.

Estructura principal

- `local/` — DAOs, entidades y `AppDatabase` (Room).
- `repository/` — Repositorios que orquestan DAOs y servicios.
- `storage/` — Servicio para guardar imágenes/archivos locales.
- `security/` — `PasswordHasher` (BCrypt) y utilidades.

Contratos y prácticas

- Repositorios devuelven un `Result<T>` (Success/Error) o `Flow<T>` para lecturas continuas.
- Usar coroutines (`suspend`) para operaciones IO.
- Guardar `userId` en DataStore para sesión persistente.

Cómo probar localmente

- Ejecutar unit tests en `app/src/test`.
- Para probar DB, usa instrumented tests o inicializador `DataSeeder`.

Referencias

- `data/local/database/AppDatabase.kt`
- `data/repository/UserRepository.kt`
- `data/storage/LocalStorageService.kt`
