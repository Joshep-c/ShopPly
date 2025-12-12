# data/storage — Manejo de archivos locales

Propósito

- Servicio responsable por guardar imágenes (productos) y devolver rutas locales (`file://...`).
- Maneja compresión y almacenamiento en el directorio de la app.

API (resumen)

- `saveImage(bitmap, filename): String` — guarda y retorna la ruta local.
- `deleteImage(path)` — elimina fichero si aplica.

Notas

- Las pruebas pueden usar un directorio temporal o la API in-memory del OS.
- Validar permisos de almacenamiento si se trabaja con APIs que lo requieran.
