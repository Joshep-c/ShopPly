# Métodos clave (resumen global)

Este documento recoge los métodos y entradas/salidas más relevantes de la app — útil para nuevos colaboradores y para referencia rápida.

UserRepository

- `suspend fun login(email: String, password: String): Result<User>`
  - Verifica credenciales; guarda `userId` en DataStore si tiene éxito.

- `suspend fun register(email: String, name: String, password: String, phone: String?, userRole: UserRole): Result<User>`
  - Crea un usuario y retorna el resultado. Puede lanzar errores por duplicados o validación.

- `suspend fun logout()`
  - Limpia la sesión en DataStore.

- `fun getCurrentUser(): Flow<User?>`
  - Flujo reactivo con el usuario actual o `null`.

StoreRepository

- `suspend fun createStore(store: Store): Result<Store>`
  - Inserta una tienda con estado inicial (p.ej. `PENDING`) y retorna el objeto creado.

- `fun getPendingStores(): Flow<List<Store>>`
  - Flujo con tiendas pendientes de aprobación.

- `suspend fun approveStore(storeId: Long): Result<Unit>` / `suspend fun rejectStore(storeId: Long): Result<Unit>`
  - Cambia el estado de la tienda y notifica/o registra la acción.

ProductRepository

- `suspend fun createProduct(product: Product): Result<Product>`
- `suspend fun updateProduct(product: Product): Result<Product>`
- `fun getProductsByStore(storeId: Long): Flow<List<Product>>`
- `fun searchProducts(query: String): Flow<List<Product>>`

CartRepository

- `suspend fun addItem(userId: Long, item: CartItem): Result<Unit>`
- `fun getCartForUser(userId: Long): Flow<List<CartItem>>`
- `suspend fun updateQuantity(cartItemId: Long, qty: Int): Result<Unit>`

FavoriteRepository

- `suspend fun toggleFavorite(userId: Long, productId: Long): Result<Unit>`
- `fun getFavoritesForUser(userId: Long): Flow<List<Product>>`

DAOs (resumen)

- `UserDao` — `getUserByEmailAndPassword(email, password): User?`, `getUserById(id): Flow<User?>`, `insert(user)`
- `StoreDao` — `getStoreByOwner(userId): Flow<Store?>`, `getPendingStores(): Flow<List<Store>>`, `insert(store)`, `update(store)`
- `ProductDao` — `getProductsByStore(storeId): Flow<List<Product>>`, `searchByName(query): Flow<List<Product>>`, `insert/update/delete`

ViewModels (puntos de entrada comunes)

- `AuthViewModel.login()` — valida inputs, invoca `UserRepository.login()` y expone `AuthUiState`.
- `AuthViewModel.register()` — registra usuario y emite mensajes de UI.
- `StoreViewModel.loadMyProducts()` — observa `ProductRepository.getProductsByStore`.
- `AdminStoresViewModel.loadPending()` — observa `StoreRepository.getPendingStores()`.
- `AddEditProductViewModel.saveProduct()` — orquesta `LocalStorageService.saveImage()` + `ProductRepository.createProduct()`.

Patrones de estado / mensajes

- La mayoría de las pantallas exponen un `UiState` sellado con campos: `loading`, `data` (genérico), `message` (String?).
- Mensajes efímeros se consumen en la UI con `LaunchedEffect` y luego se limpian en el ViewModel.
- Flujos reactividad: `Flow` para emisiones contínuas; `StateFlow` en ViewModels para estado observable por Compose.

Notas finales

- Las firmas concretas pueden variar; usa este documento como referencia rápida. Para detalles exactos, revisar el archivo fuente del repositorio (`data/repository`, `data/local/dao`, `ui/*/ViewModel`).
