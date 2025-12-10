package com.shopply.appEcommerce.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.shopply.appEcommerce.data.local.entities.User
import com.shopply.appEcommerce.data.local.entities.UserRole
import kotlinx.coroutines.flow.Flow


// UserDao - Acceso a datos de usuarios

@Dao
interface UserDao {

    // CONSULTAS BÁSICAS

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: Long): Flow<User?>

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserByIdSync(userId: Long): User?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE userRole = :role AND isBanned = 0")
    fun getUsersByRole(role: UserRole): Flow<List<User>>

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<User>>

    // OPERACIONES DE ESCRITURA

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("UPDATE users SET lastLoginAt = :timestamp WHERE id = :userId")
    suspend fun updateLastLogin(
        userId: Long,
        timestamp: Long = System.currentTimeMillis()
    )

    // ADMIN: GESTIÓN DE USUARIOS

    @Query("UPDATE users SET isBanned = :isBanned, banReason = :reason WHERE id = :userId")
    suspend fun banUser(userId: Long, isBanned: Boolean, reason: String?)

    @Query("SELECT COUNT(*) FROM users WHERE userRole = :role")
    suspend fun countUsersByRole(role: UserRole): Int

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getTotalUserCount(): Int
}