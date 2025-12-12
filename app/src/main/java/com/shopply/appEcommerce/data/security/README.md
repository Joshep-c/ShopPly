# data/security — Hashing y seguridad ligera

Propósito

- Proveer utilidades de seguridad usadas por `UserRepository` (ej.: `PasswordHasher` con BCrypt).

Consideraciones

- BCrypt se usa para hashing local; no reemplaza prácticas de seguridad en servidores.
- No almacenar contraseñas en texto plano; guardar sólo hashes.

Pruebas

- Validar que `verify(password, hash)` funcione contra vectores de prueba conocidos.
