# data/local — Room & DAOs

Propósito

- Contiene la definición de entidades, DAOs y la clase `AppDatabase`.
- Gestiona migrations y acceso local a datos.

Archivos clave

- `AppDatabase.kt` — entidades registradas y migraciones (ver MIGRATION_3_4).
- `dao/` — `UserDao`, `ProductDao`, `StoreDao`, `CategoryDao`, `CartDao`, `FavoriteDao`.

Buenas prácticas

- Evitar `fallbackToDestructiveMigration()` en producción.
- Añadir migrations cuando se cambia el esquema y documentarlas en este README.
- Usar `Flow<T>` desde los DAOs para obtener actualizaciones reactivas.

Cómo añadir una columna (ejemplo)

1. Crear una `Migration` en `AppDatabase.kt` con SQL `ALTER TABLE ...`.
2. Bump la versión de la DB.
3. Añadir tests/migrate localmente antes de desplegar.
