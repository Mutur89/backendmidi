// controller/UserController.kt
package org.example.org.example.controller

import com.example.lvlupbackend.model.dto.request.LoginRequest
import com.example.lvlupbackend.model.dto.request.RegisterRequest
import com.example.lvlupbackend.model.dto.response.LoginResponse
import jakarta.validation.Valid
import org.example.org.example.model.User
import org.example.org.example.model.enum.UserRole
import org.example.org.example.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["*"]) // Permitir peticiones desde Android
class UserController(
    private val userService: UserService
) {

    /**
     * Registrar un nuevo usuario
     * POST /api/auth/register
     */
    @PostMapping("/auth/register")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<Any> {
        return try {
            val newUser = userService.registerUser(request)
            ResponseEntity.status(HttpStatus.CREATED).body(mapOf(
                "success" to true,
                "message" to "Usuario registrado exitosamente",
                "user" to mapOf(
                    "id" to newUser.id,
                    "nombre" to newUser.nombre,
                    "apellido" to newUser.apellido,
                    "correo" to newUser.correo,
                    "rol" to newUser.rol
                )
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf(
                "success" to false,
                "message" to (e.message ?: "No se pudo completar el registro. Verifique que el correo no esté registrado")
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "No se pudo completar el registro. Intente nuevamente"
            ))
        }
    }

    /**
     * Login de usuario
     * POST /api/auth/login
     */
    @PostMapping("/auth/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<Any> {
        return try {
            val loginResponse = userService.loginUser(request)
            ResponseEntity.ok(mapOf(
                "success" to true,
                "message" to "Login exitoso",
                "data" to loginResponse
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf(
                "success" to false,
                "message" to (e.message ?: "Credenciales incorrectas. Verifique su correo y contraseña")
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "No se pudo iniciar sesión. Intente nuevamente"
            ))
        }
    }

    /**
     * Obtener usuario por ID
     * GET /api/users/{id}
     */
    @GetMapping("/users/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            val user = userService.getUserById(id)
            ResponseEntity.ok(mapOf(
                "success" to true,
                "data" to userService.getUserSummary(id)
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf(
                "success" to false,
                "message" to (e.message ?: "No se encontró el usuario solicitado")
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "No se pudo obtener la información del usuario. Intente nuevamente"
            ))
        }
    }

    /**
     * Obtener todos los usuarios (solo admin)
     * GET /api/users
     */
    @GetMapping("/users")
    fun getAllUsers(): ResponseEntity<Any> {
        return try {
            val users = userService.getAllUsers()
            ResponseEntity.ok(mapOf(
                "success" to true,
                "data" to users.map { userService.getUserSummary(it.id) },
                "count" to users.size
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al obtener usuarios: ${e.message}"
            ))
        }
    }

    /**
     * Obtener usuarios por rol
     * GET /api/users/role/{rol}
     */
    @GetMapping("/users/role/{rol}")
    fun getUsersByRole(@PathVariable rol: String): ResponseEntity<Any> {
        return try {
            val userRole = UserRole.valueOf(rol.uppercase())
            val users = userService.getUsersByRole(userRole)
            ResponseEntity.ok(mapOf(
                "success" to true,
                "data" to users.map { userService.getUserSummary(it.id) },
                "count" to users.size
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf(
                "success" to false,
                "message" to "Rol inválido: $rol"
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al obtener usuarios: ${e.message}"
            ))
        }
    }

    /**
     * Actualizar perfil de usuario
     * PUT /api/users/{id}
     */
    @PutMapping("/users/{id}")
    fun updateUserProfile(
        @PathVariable id: Long,
        @RequestBody updateData: Map<String, String>
    ): ResponseEntity<Any> {
        return try {
            val updatedUser = userService.updateUserProfile(
                userId = id,
                nombre = updateData["nombre"],
                apellido = updateData["apellido"],
                telefono = updateData["telefono"],
                direccion = updateData["direccion"],
                region = updateData["region"],
                comuna = updateData["comuna"]
            )
            ResponseEntity.ok(mapOf(
                "success" to true,
                "message" to "Perfil actualizado exitosamente",
                "data" to userService.getUserSummary(updatedUser.id)
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf(
                "success" to false,
                "message" to e.message
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al actualizar perfil: ${e.message}"
            ))
        }
    }

    /**
     * Cambiar contraseña
     * PUT /api/users/{id}/change-password
     */
    @PutMapping("/users/{id}/change-password")
    fun changePassword(
        @PathVariable id: Long,
        @RequestBody passwordData: Map<String, String>
    ): ResponseEntity<Any> {
        return try {
            val oldPassword = passwordData["oldPassword"]
                ?: return ResponseEntity.badRequest().body(mapOf(
                    "success" to false,
                    "message" to "La contraseña actual es requerida"
                ))

            val newPassword = passwordData["newPassword"]
                ?: return ResponseEntity.badRequest().body(mapOf(
                    "success" to false,
                    "message" to "La nueva contraseña es requerida"
                ))

            userService.changePassword(id, oldPassword, newPassword)

            ResponseEntity.ok(mapOf(
                "success" to true,
                "message" to "Contraseña actualizada exitosamente"
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf(
                "success" to false,
                "message" to e.message
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al cambiar contraseña: ${e.message}"
            ))
        }
    }

    /**
     * Cambiar rol de usuario (solo admin)
     * PUT /api/users/{id}/change-role
     */
    @PutMapping("/users/{id}/change-role")
    fun changeUserRole(
        @PathVariable id: Long,
        @RequestBody roleData: Map<String, String>
    ): ResponseEntity<Any> {
        return try {
            val newRole = roleData["rol"]
                ?: return ResponseEntity.badRequest().body(mapOf(
                    "success" to false,
                    "message" to "El rol es requerido"
                ))

            val userRole = UserRole.valueOf(newRole.uppercase())
            val updatedUser = userService.changeUserRole(id, userRole)

            ResponseEntity.ok(mapOf(
                "success" to true,
                "message" to "Rol actualizado exitosamente",
                "data" to userService.getUserSummary(updatedUser.id)
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf(
                "success" to false,
                "message" to e.message
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al cambiar rol: ${e.message}"
            ))
        }
    }

    /**
     * Eliminar usuario
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/users/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            userService.deleteUser(id)
            ResponseEntity.ok(mapOf(
                "success" to true,
                "message" to "Usuario eliminado exitosamente"
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf(
                "success" to false,
                "message" to e.message
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al eliminar usuario: ${e.message}"
            ))
        }
    }

    /**
     * Verificar si un correo está registrado
     * GET /api/users/check-email/{correo}
     */
    @GetMapping("/users/check-email/{correo}")
    fun checkEmail(@PathVariable correo: String): ResponseEntity<Any> {
        return try {
            val exists = userService.isEmailRegistered(correo)
            ResponseEntity.ok(mapOf(
                "success" to true,
                "exists" to exists
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al verificar correo: ${e.message}"
            ))
        }
    }

    /**
     * Verificar si un RUT está registrado
     * GET /api/users/check-rut/{rut}
     */
    @GetMapping("/users/check-rut/{rut}")
    fun checkRut(@PathVariable rut: String): ResponseEntity<Any> {
        return try {
            val exists = userService.isRutRegistered(rut)
            ResponseEntity.ok(mapOf(
                "success" to true,
                "exists" to exists
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al verificar RUT: ${e.message}"
            ))
        }
    }

    /**
     * Obtener estadísticas de usuarios (admin)
     * GET /api/users/stats
     */
    @GetMapping("/users/stats")
    fun getUserStats(): ResponseEntity<Any> {
        return try {
            val totalUsers = userService.getTotalUsers()
            val totalClientes = userService.countUsersByRole(UserRole.CLIENTE)
            val totalAdmins = userService.countUsersByRole(UserRole.ADMIN)

            ResponseEntity.ok(mapOf(
                "success" to true,
                "data" to mapOf(
                    "totalUsers" to totalUsers,
                    "clientes" to totalClientes,
                    "admins" to totalAdmins
                )
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al obtener estadísticas: ${e.message}"
            ))
        }
    }

    /**
     * Crear usuarios de prueba (solo para desarrollo)
     * POST /api/users/create-test-users
     */
    @PostMapping("/users/create-test-users")
    fun createTestUsers(): ResponseEntity<Any> {
        return try {
            userService.createTestUsers()
            ResponseEntity.ok(mapOf(
                "success" to true,
                "message" to "Usuarios de prueba creados exitosamente"
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al crear usuarios de prueba: ${e.message}"
            ))
        }
    }
}