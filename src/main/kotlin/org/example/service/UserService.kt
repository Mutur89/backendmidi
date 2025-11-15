// service/UserService.kt (VERSIÓN SIMPLE PARA PROYECTO EDUCATIVO)
package org.example.org.example.service

import com.example.lvlupbackend.model.dto.request.LoginRequest
import com.example.lvlupbackend.model.dto.request.RegisterRequest
import com.example.lvlupbackend.model.dto.response.LoginResponse
import org.example.org.example.repository.UserRepository
import org.example.org.example.model.User
import org.example.org.example.model.enum.UserRole
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository
) {

    /**
     * Registrar un nuevo usuario
     */
    fun registerUser(request: RegisterRequest): User {
        // Validar que el correo no esté registrado
        if (userRepository.existsByCorreo(request.correo)) {
            throw IllegalArgumentException("El correo ${request.correo} ya está registrado")
        }

        // Validar que el RUT no esté registrado
        if (userRepository.existsByRut(request.rut)) {
            throw IllegalArgumentException("El RUT ${request.rut} ya está registrado")
        }

        // Crear el usuario (contraseña en texto plano para proyecto educativo)
        val newUser = User(
            nombre = request.nombre,
            apellido = request.apellido,
            correo = request.correo,
            contrasena = request.contrasena, // Sin encriptar
            telefono = request.telefono,
            fechaNacimiento = request.fechaNacimiento,
            direccion = request.direccion,
            rut = request.rut,
            region = request.region,
            comuna = request.comuna,
            rol = UserRole.CLIENTE, // Por defecto todos son CLIENTE
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        return userRepository.save(newUser)
    }

    /**
     * Login de usuario
     */
    fun loginUser(request: LoginRequest): LoginResponse {
        // Buscar usuario por correo
        val user = userRepository.findByCorreo(request.correo)
            .orElseThrow { IllegalArgumentException("Correo o contraseña incorrectos") }

        // Validar contraseña (comparación directa)
        if (user.contrasena != request.contrasena) {
            throw IllegalArgumentException("Correo o contraseña incorrectos")
        }

        // Generar un token simple
        val simpleToken = Base64.getEncoder().encodeToString(
            "${user.id}:${user.correo}:${System.currentTimeMillis()}".toByteArray()
        )

        // Retornar respuesta de login
        return LoginResponse(
            token = simpleToken,
            type = "Bearer",
            userId = user.id,
            correo = user.correo,
            nombre = "${user.nombre} ${user.apellido}",
            rol = user.rol
        )
    }

    /**
     * Obtener usuario por ID
     */
    fun getUserById(userId: Long): User {
        return userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("Usuario no encontrado con ID: $userId") }
    }

    /**
     * Obtener usuario por correo
     */
    fun getUserByCorreo(correo: String): User {
        return userRepository.findByCorreo(correo)
            .orElseThrow { IllegalArgumentException("Usuario no encontrado con correo: $correo") }
    }

    /**
     * Obtener usuario por RUT
     */
    fun getUserByRut(rut: String): User {
        return userRepository.findByRut(rut)
            .orElseThrow { IllegalArgumentException("Usuario no encontrado con RUT: $rut") }
    }

    /**
     * Obtener todos los usuarios
     */
    fun getAllUsers(): List<User> {
        return userRepository.findAll()
    }

    /**
     * Obtener usuarios por rol
     */
    fun getUsersByRole(rol: UserRole): List<User> {
        return userRepository.findByRol(rol)
    }

    /**
     * Actualizar perfil de usuario
     */
    fun updateUserProfile(
        userId: Long,
        nombre: String? = null,
        apellido: String? = null,
        telefono: String? = null,
        direccion: String? = null,
        region: String? = null,
        comuna: String? = null
    ): User {
        val user = getUserById(userId)

        // Crear copia con campos actualizados
        val updatedUser = user.copy(
            nombre = nombre ?: user.nombre,
            apellido = apellido ?: user.apellido,
            telefono = telefono ?: user.telefono,
            direccion = direccion ?: user.direccion,
            region = region ?: user.region,
            comuna = comuna ?: user.comuna,
            updatedAt = LocalDateTime.now()
        )

        return userRepository.save(updatedUser)
    }

    /**
     * Cambiar contraseña de usuario
     */
    fun changePassword(userId: Long, oldPassword: String, newPassword: String): User {
        val user = getUserById(userId)

        // Validar que la contraseña antigua sea correcta
        if (user.contrasena != oldPassword) {
            throw IllegalArgumentException("La contraseña actual es incorrecta")
        }

        // Validar que la nueva contraseña tenga al menos 6 caracteres
        if (newPassword.length < 6) {
            throw IllegalArgumentException("La nueva contraseña debe tener al menos 6 caracteres")
        }

        // Actualizar contraseña
        val updatedUser = user.copy(
            contrasena = newPassword,
            updatedAt = LocalDateTime.now()
        )

        return userRepository.save(updatedUser)
    }

    /**
     * Cambiar rol de usuario (solo admin)
     */
    fun changeUserRole(userId: Long, newRole: UserRole): User {
        val user = getUserById(userId)
        val updatedUser = user.copy(
            rol = newRole,
            updatedAt = LocalDateTime.now()
        )
        return userRepository.save(updatedUser)
    }

    /**
     * Eliminar usuario
     */
    fun deleteUser(userId: Long) {
        val user = getUserById(userId)
        userRepository.delete(user)
    }

    /**
     * Verificar si un correo ya está registrado
     */
    fun isEmailRegistered(correo: String): Boolean {
        return userRepository.existsByCorreo(correo)
    }

    /**
     * Verificar si un RUT ya está registrado
     */
    fun isRutRegistered(rut: String): Boolean {
        return userRepository.existsByRut(rut)
    }

    /**
     * Obtener usuarios por región
     */
    fun getUsersByRegion(region: String): List<User> {
        return userRepository.findByRegion(region)
    }

    /**
     * Obtener usuarios por región y comuna
     */
    fun getUsersByRegionAndComuna(region: String, comuna: String): List<User> {
        return userRepository.findByRegionAndComuna(region, comuna)
    }

    /**
     * Contar total de usuarios
     */
    fun getTotalUsers(): Long {
        return userRepository.count()
    }

    /**
     * Contar usuarios por rol
     */
    fun countUsersByRole(rol: UserRole): Long {
        return userRepository.findByRol(rol).size.toLong()
    }

    /**
     * Validar credenciales de usuario
     */
    fun validateCredentials(correo: String, contrasena: String): Boolean {
        return try {
            val user = userRepository.findByCorreo(correo).orElse(null) ?: return false
            user.contrasena == contrasena
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Obtener información resumida del usuario (sin contraseña)
     */
    fun getUserSummary(userId: Long): Map<String, Any> {
        val user = getUserById(userId)
        return mapOf(
            "id" to user.id,
            "nombre" to user.nombre,
            "apellido" to user.apellido,
            "correo" to user.correo,
            "telefono" to user.telefono,
            "direccion" to user.direccion,
            "rut" to user.rut,
            "region" to user.region,
            "comuna" to user.comuna,
            "rol" to user.rol,
            "createdAt" to user.createdAt
        )
    }

    /**
     * Crear usuarios de prueba (útil para desarrollo)
     */
    fun createTestUsers() {
        // Crear admin si no existe
        if (!userRepository.existsByCorreo("admin@duocuc.cl")) {
            val admin = User(
                nombre = "Admin",
                apellido = "User",
                correo = "admin@duocuc.cl",
                contrasena = "admin123",
                telefono = "987654321",
                fechaNacimiento = 0,
                direccion = "DuocUC",
                rut = "12.345.678-9",
                region = "Valparaiso",
                comuna = "Viña del mar",
                rol = UserRole.ADMIN,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
            userRepository.save(admin)
        }

        // Crear cliente si no existe
        if (!userRepository.existsByCorreo("cliente@gmail.com")) {
            val cliente = User(
                nombre = "Cliente",
                apellido = "User",
                correo = "cliente@gmail.com",
                contrasena = "cliente123",
                telefono = "123456789",
                fechaNacimiento = 0,
                direccion = "Por ahi",
                rut = "98.765.432-1",
                region = "Valparaiso",
                comuna = "Viña del mar",
                rol = UserRole.CLIENTE,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
            userRepository.save(cliente)
        }
    }
}