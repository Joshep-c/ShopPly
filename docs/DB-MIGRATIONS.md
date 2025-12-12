# DB Migrations

Este documento explica las migraciones definidas en `AppDatabase.kt` y cómo añadir nuevas migraciones.

Migraciones actuales

- `MIGRATION_3_4` — añade columna `imageUrl` a la tabla `categories`:

```sql
ALTER TABLE categories ADD COLUMN imageUrl TEXT
```

Practicas para nuevas migraciones

1. Cambia el schema en la entidad correspondiente y crea una `Migration` con SQL puro.
2. Incrementa la versión de la DB en `AppDatabase`.
3. Añade la migración al builder de Room: `addMigrations(MIGRATION_X_Y)`.
4. No usar `fallbackToDestructiveMigration()` en producción; solo en desarrollo cuando los datos pueden perderse.
5. Probar la migración en un dispositivo/emulador con la versión antigua antes de ejecutar la nueva.

Ejemplo de Migration

```kotlin
val MIGRATION_4_5 = object : Migration(4,5) {
  override fun migrate(database: SupportSQLiteDatabase) {
    database.execSQL("ALTER TABLE products ADD COLUMN sku TEXT")
  }
}
```

Pruebas

- Crear una base de datos con la versión antigua usando `Room.inMemoryDatabaseBuilder` o test fixtures y aplicar la migración.
- Verificar que los datos persisten y la nueva columna existe.
