// repository/UserRepository.kt
package org.example.org.example.repository

import org.example.org.example.model.User
import org.example.org.example.model.enum.UserRole
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository : JpaRepository<User, Long> {

    // Buscar por correo (para login)
    fun findByCorreo(correo: String): Optional<User>

    // Verificar si existe un correo
    fun existsByCorreo(correo: String): Boolean

    // Verificar si existe un RUT
    fun existsByRut(rut: String): Boolean

    // Buscar por RUT
    fun findByRut(rut: String): Optional<User>

    // Buscar usuarios por rol
    fun findByRol(rol: UserRole): List<User>

    // Buscar por región
    fun findByRegion(region: String): List<User>

    // Buscar por región y comuna
    fun findByRegionAndComuna(region: String, comuna: String): List<User>
}