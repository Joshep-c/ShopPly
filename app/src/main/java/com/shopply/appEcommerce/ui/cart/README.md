# ui/cart — Carrito de compras

Propósito

- Gestionar los ítems añadidos por el usuario, calcular totales y proceder al checkout (simplificado).

Archivos clave

- `CartViewModel.kt` — expone `Flow<List<CartItem>>`, operaciones: `addItem`, `removeItem`, `updateQuantity`, `clearCart`.
- `CartScreen.kt` — lista de ítems, resumen de pago y botón de checkout.

Métodos clave

- `CartRepository.addItem(cartItem): Result<Unit>` — añade o incrementa cantidad.
- `CartRepository.getCartForUser(userId): Flow<List<CartItem>>` — flujo reactivo.

Notas

- Validar stock antes de confirmar el checkout.
- Implementar orden y persistencia real en etapas posteriores (Orders module).
