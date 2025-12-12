# ui/addproduct — Añadir y editar productos

Propósito

- Pantallas y ViewModel para crear o editar productos por parte del vendedor.

Archivos clave

- `AddEditProductViewModel.kt` — validación del formulario, guardado de imagen y creación/actualización de la entidad `Product`.
- `AddEditProductScreen.kt` — formulario (nombre, descripción, precio, stock, categoría, imagen).

Flujo típico

1. Vendedor abre `AddEditProductScreen` (nuevo o editar).
2. El ViewModel valida campos y solicita a `LocalStorageService` guardar la imagen (si existe).
3. Llama a `ProductRepository` para `createProduct` o `updateProduct`.
4. Mostrar `UiState.message` con resultado y navegar atrás si fue exitoso.

Puntos importantes / Métodos clave

- `AddEditProductViewModel.saveProduct()` — valida y orquesta imagen + repositorio.
- `LocalStorageService.saveImage(bitmap, filename): String` — retorna URI local usada por el repositorio.
- `ProductRepository.createProduct(product): Result<Product>` — persiste producto y retorna resultado.

Notas de UX

- Comprimir la imagen antes de subir para ahorrar espacio.
- Validar precio > 0 y stock >= 0.
