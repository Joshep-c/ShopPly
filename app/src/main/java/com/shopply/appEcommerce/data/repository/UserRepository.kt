package com.shopply.appEcommerce.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.shopply.appEcommerce.data.local.dao.UserDao
import com.shopply.appEcommerce.data.local.entities.User
import com.shopply.appEcommerce.data.local.entities.UserRole
import com.shopply.appEcommerce.data.security.PasswordHasher
import com.shopply.appEcommerce.domain.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


 // UserRepository - Repositorio de usuarios
 
 // Gestiona: 
 // - Autenticación (login/registro) con contraseñas hasheadas
 // - Sesión de usuario (DataStore)
 // - CRUD de usuarios
 // - Gestión de roles y permisos
 //
 // SEGURIDAD:
 // - Contraseñas hasheadas con BCrypt
 // - Validación de fortaleza de contraseña
 // - Protección contra timing attacks

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val dataStore: DataStore<Preferences>,
    private val passwordHasher: PasswordHasher
) {
    companion object {
        private val CURRENT_USER_ID = longPreferencesKey("current_user_id")
    }

    // GESTIÓN DE SESIÓN
    // Obtiene el usuario actual de la sesión
     
    fun getCurrentUser(): Flow<User?> {
        return dataStore.data.map { it[CURRENT_USER_ID] }
            .map { userId -> userId?.let { userDao.getUserByIdSync(it) } }
    }

    
    // Obtiene el ID del usuario actual
    
    suspend fun getCurrentUserId(): Long? {
        return dataStore.data.first()[CURRENT_USER_ID]
    }

    
    // Verifica si hay una sesión activa
    
    suspend fun isLoggedIn(): Boolean {
        return dataStore.data.first()[CURRENT_USER_ID] != null
    }

    // AUTENTICACIÓN
    /**
     * Login de usuario
     * Valida credenciales usando BCrypt y guarda sesión
     *
     * SEGURIDAD:
     * - Verifica hash con BCrypt (protección contra timing attacks)
     * - Verifica si el usuario está baneado
     * - Actualiza timestamp de último login
     */
    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val user = userDao.getUserByEmail(email)

            when {
                user == null -> {
                    Result.Error(Exception("Usuario no encontrado"))
                }
                user.isBanned -> {
                    Result.Error(Exception("Tu cuenta ha sido suspendida: ${user.banReason}"))
                }
                !passwordHasher.verifyPassword(password, user.passwordHash) -> {
                    // BCrypt hace comparación de tiempo constante
                    Result.Error(Exception("Contraseña incorrecta"))
                }
                else -> {
                    // Login exitoso: guardar sesión
                    dataStore.edit { it[CURRENT_USER_ID] = user.id }
                    userDao.updateLastLogin(user.id)

                    // Verificar si el hash necesita actualización
                    if (passwordHasher.needsRehash(user.passwordHash)) {
                        // Rehashear con el nuevo factor de costo
                        val newHash = passwordHasher.hashPassword(password)
                        userDao.updateUser(user.copy(passwordHash = newHash))
                    }

                    Result.Success(user)
                }
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    /**
     * Registro de nuevo usuario
     *
     * SEGURIDAD:
     * - Valida fortaleza de contraseña
     * - Hashea contraseña con BCrypt antes de guardar
     * - Verifica que el email no esté duplicado
     * - Auto-login después de registro exitoso
     */
    suspend fun register(
        email: String,
        name: String,
        password: String,
        phone: String? = null,
        userRole: UserRole = UserRole.BUYER
    ): Result<User> {
        return try {
            // 1. Validar fortaleza de contraseña
            val (isValid, errorMessage) = passwordHasher.validatePasswordStrength(password)
            if (!isValid) {
                return Result.Error(Exception(errorMessage ?: "Contraseña inválida"))
            }

            // 2. Verificar si el email ya existe
            if (userDao.getUserByEmail(email) != null) {
                return Result.Error(Exception("El email ya está registrado"))
            }

            // 3. Hashear la contraseña con BCrypt
            val passwordHash = passwordHasher.hashPassword(password)

            // 4. Crear usuario con contraseña hasheada
            val user = User(
                email = email,
                passwordHash = passwordHash,  // ✅ Hash, NO texto plano
                name = name,
                phone = phone,
                userRole = userRole
            )

            val userId = userDao.insertUser(user)
            val newUser = user.copy(id = userId)

            // 5. Auto-login
            dataStore.edit { it[CURRENT_USER_ID] = userId }

            Result.Success(newUser)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // Cierre de sesión
    suspend fun logout() {
        dataStore.edit { it.remove(CURRENT_USER_ID) }
    }

    // GESTIÓN DE PERFIL
    /**
     * Actualizar información del usuario
     */
    suspend fun updateUser(user: User): Result<Unit> {
        return try {
            userDao.updateUser(user)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Actualizar información del perfil (sin cambiar contraseña)
     */
    suspend fun updateProfile(user: User): Result<Unit> {
        return try {
            userDao.updateUser(user)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Cambiar contraseña del usuario
     */
    suspend fun changePassword(userId: Long, newPassword: String): Result<Unit> {
        return try {
            // Validar fortaleza de contraseña
            val (isValid, errorMessage) = passwordHasher.validatePasswordStrength(newPassword)
            if (!isValid) {
                return Result.Error(Exception(errorMessage ?: "Contraseña inválida"))
            }

            // Hashear nueva contraseña
            val newPasswordHash = passwordHasher.hashPassword(newPassword)

            // Actualizar contraseña
            val user = userDao.getUserByIdSync(userId)
                ?: return Result.Error(Exception("Usuario no encontrado"))

            userDao.updateUser(user.copy(passwordHash = newPasswordHash))
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Eliminar cuenta de usuario
     */
    suspend fun deleteUser(userId: Long): Result<Unit> {
        return try {
            val user = userDao.getUserByIdSync(userId)
                ?: return Result.Error(Exception("Usuario no encontrado"))

            userDao.deleteUser(user)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // ADMIN: GESTIÓN DE USUARIOS
    // Obtener todos los usuarios (Admin)
     
    fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()

    // Obtener usuarios por rol (Admin)
    
    fun getUsersByRole(role: UserRole): Flow<List<User>> = userDao.getUsersByRole(role)

    // Banear usuario (Admin)
    
    suspend fun banUser(userId: Long, reason: String): Result<Unit> {
        return try {
            userDao.banUser(userId, true, reason)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // Desbanear usuario (Admin)
     
    suspend fun unbanUser(userId: Long): Result<Unit> {
        return try {
            userDao.banUser(userId, false, null)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // ESTADÍSTICAS
    // Obtener estadísticas de usuarios
    
    suspend fun getUserStats(): UserStats {
        return UserStats(
            totalUsers = userDao.getTotalUserCount(),
            totalBuyers = userDao.countUsersByRole(UserRole.BUYER),
            totalSellers = userDao.countUsersByRole(UserRole.SELLER),
            totalAdmins = userDao.countUsersByRole(UserRole.ADMIN)
        )
    }
}


// Estadísticas de usuarios

data class UserStats(
    val totalUsers: Int,
    val totalBuyers: Int,
    val totalSellers: Int,
    val totalAdmins: Int
)