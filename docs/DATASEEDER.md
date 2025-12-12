# DataSeeder

Propósito

- Inicializar datos de desarrollo en la base de datos local para facilitar pruebas manuales.
- Crear usuarios de ejemplo (admin, vendedores, compradores), categorías y productos de muestra.

Cuándo usar

- Durante desarrollo local o para preparar un entorno de demostración.
- No ejecutar en producción salvo que esté diseñado explícitamente para ello.

Métodos clave (resumen)

- `fun seedIfEmpty()` — función pública que comprueba si la BD está vacía y ejecuta el seeding.
- `suspend fun seedUsers()` — crea usuarios por defecto (ej.: admin@example.com, seller@example.com).
- `suspend fun seedCategories()` — inserta categorías iniciales (Electrónica, Ropa, Hogar, etc.).
- `suspend fun seedProducts()` — crea varios productos de ejemplo asociados a tiendas y categorías.

Ejemplo de uso

- En `MainActivity` o en el inicializador de la app (`DatabaseInitializer`), llamar a:

```kotlin
// ejemplo simplificado
lifecycleScope.launch {
    dataSeeder.seedIfEmpty()
}
```

Consideraciones

- El `DataSeeder` normalmente usa los repositorios para insertar datos (no escribe directamente en los DAOs) para mantener invariantes de negocio.
- Evitar datos sensibles. Los passwords de ejemplo deben estar hasheados (usar `PasswordHasher`).
- Documenta cualquier dato especial (por ejemplo: `RUC` o permisos admin) para facilitar pruebas.

Pruebas

- Para tests, crear un `TestDatabase` en memoria y ejecutar métodos del `DataSeeder` verificando que las tablas se llenan correctamente.
- Si el seeding es determinístico, los tests pueden validar IDs/valores esperados.
