# data/repository — Orquestación de datos

Propósito

- Implementa la lógica de negocio entre DAOs, servicios externos y ViewModels.
- Normaliza respuestas (ej. `Result<T>`) y expone `Flow` para consumo UI.

Archivos y responsabilidades

- `UserRepository.kt` — login, register, sesión (DataStore).
- `StoreRepository.kt` — creación/actualización/estado de tiendas (PENDING/APPROVED/REJECTED).
- `ProductRepository.kt` — CRUD de productos y búsquedas.
- `DataSeeder.kt` — inicialización de datos para desarrollo.

Recomendaciones

- Mantén la lógica de validación del negocio en los repositorios o en una capa de casos de uso en `domain`.
- Evitar lógica UI (snackbars/strings) en repositorios; devolver estados/clases de error.

Pruebas

- Escriba tests unitarios para repositorios usando un `TestDatabase` in-memory y doubles/mocks para dependencias.
