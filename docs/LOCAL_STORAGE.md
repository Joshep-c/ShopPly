# LocalStorageService

Propósito

- Servicio responsable por guardar y eliminar archivos locales (principalmente imágenes de productos) y devolver URIs locales reutilizables por la app.

API principal (resumen)

- `suspend fun saveImage(bitmap: Bitmap, filename: String): String`
  - Guarda la imagen comprimida en el directorio de archivos de la app y retorna una URI local (ej.: `file:///data/data/<package>/files/<filename>`).
  - Maneja compresión para reducir tamaño (calidad configurable).

- `suspend fun deleteImage(path: String): Boolean`
  - Elimina el fichero en la ruta indicada y retorna `true` si se borró correctamente.

- `fun getImageFilePath(filename: String): String`
  - Convierte un nombre de archivo a la ruta absoluta usada por la app.

Notas de implementación y consideraciones

- La librería Coil o similar puede cargar las URIs devueltas sin necesidad de cambios.
- No requiere permisos de almacenamiento externos si guarda en `context.filesDir` (almacenamiento privado de la app).
- Para archivos accesibles fuera de la app (p.ej. compartir), usar `FileProvider` y permisos adecuados.
- Comprimir imágenes antes de guardar para ahorrar espacio y evitar OOM al cargar.

Errores comunes

- Guardar con un nombre duplicado: decidir si sobrescribir o generar nombre único (timestamp o UUID).
- No cerrar streams: usar `use {}` o try-with-resources para evitar leaks.

Pruebas

- Usar un directorio temporal en tests o un `Context` de prueba.
- Validar que `saveImage` retorna una ruta existente y que `deleteImage` elimina el fichero.
