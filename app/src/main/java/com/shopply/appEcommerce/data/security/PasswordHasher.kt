package com.shopply.appEcommerce.data.security

import org.mindrot.jbcrypt.BCrypt
import javax.inject.Inject
import javax.inject.Singleton

/**
 * PasswordHasher - Utilidad para hashear y verificar contraseñas de forma segura
 *
 * Usa BCrypt, un algoritmo diseñado específicamente para contraseñas que:
 * - Agrega "salt" automáticamente (protege contra rainbow tables)
 * - Es computacionalmente costoso (protege contra fuerza bruta)
 * - Es resistente a ataques de timing
 *
 * Buenas prácticas de seguridad implementadas:
 * - NUNCA guardar contraseñas en texto plano
 * - Usar algoritmo industry-standard (BCrypt)
 * - Salt único por contraseña (automático en BCrypt)
 * - Comparación segura contra timing attacks
 */
@Singleton
class PasswordHasher @Inject constructor() {

    companion object {
        /**
         * Número de rondas de hashing (workload factor)
         *
         * Valores recomendados:
         * - 10: Rápido, menos seguro (desarrollo)
         * - 12: Balance (recomendado para producción)
         * - 14-15: Más seguro, más lento
         *
         * Cada incremento duplica el tiempo de procesamiento
         */
        private const val LOG_ROUNDS = 12
    }

    /**
     * Hashea una contraseña usando BCrypt
     *
     * @param plainPassword Contraseña en texto plano
     * @return Hash BCrypt de la contraseña (60 caracteres)
     *
     * Ejemplo de hash generado:
     * $2a$12$R9h/cIPz0gi.URNNX3kh2OPST9/PgBkqquzi.Ss7KIUgO2t0jWMUW
     * │  │  │                     │
     * │  │  └─ Salt (22 chars)    └─ Hash (31 chars)
     * │  └─ Cost factor (12)
     * └─ Algorithm version (2a)
     */
    fun hashPassword(plainPassword: String): String {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(LOG_ROUNDS))
    }

    /**
     * Verifica si una contraseña en texto plano coincide con un hash
     *
     * @param plainPassword Contraseña ingresada por el usuario
     * @param hashedPassword Hash guardado en la base de datos
     * @return true si la contraseña es correcta, false si no
     *
     * IMPORTANTE: Esta función usa comparación de tiempo constante
     * para proteger contra ataques de timing
     */
    fun verifyPassword(plainPassword: String, hashedPassword: String): Boolean {
        return try {
            BCrypt.checkpw(plainPassword, hashedPassword)
        } catch (e: Exception) {
            // Si el hash está corrupto o es inválido, retornar false
            false
        }
    }

    /**
     * Verifica si un hash necesita ser regenerado
     * Útil si cambias el LOG_ROUNDS en el futuro
     *
     * @param hashedPassword Hash existente
     * @return true si necesita rehashing
     */
    fun needsRehash(hashedPassword: String): Boolean {
        return try {
            // Extraer el cost factor del hash existente
            val parts = hashedPassword.split("$")
            if (parts.size < 4) return true

            val currentCost = parts[2].toIntOrNull() ?: return true
            currentCost < LOG_ROUNDS
        } catch (e: Exception) {
            true
        }
    }

    /**
     * Valida la fortaleza de una contraseña
     *
     * Reglas implementadas:
     * - Mínimo 6 caracteres (para prototipo, en producción usar 8-12)
     * - Al menos una letra
     * - Al menos un número (opcional, comentado)
     *
     * @param password Contraseña a validar
     * @return Par de (esValida, mensajeError)
     */
    fun validatePasswordStrength(password: String): Pair<Boolean, String?> {
        when {
            password.length < 6 -> {
                return false to "La contraseña debe tener al menos 6 caracteres"
            }
            !password.any { it.isLetter() } -> {
                return false to "La contraseña debe contener al menos una letra"
            }
            // Descomenta para requerir números en producción
            // !password.any { it.isDigit() } -> {
            //     return false to "La contraseña debe contener al menos un número"
            // }
            // Descomenta para requerir caracteres especiales
            // !password.any { !it.isLetterOrDigit() } -> {
            //     return false to "La contraseña debe contener al menos un carácter especial"
            // }
            else -> {
                return true to null
            }
        }
    }
}

