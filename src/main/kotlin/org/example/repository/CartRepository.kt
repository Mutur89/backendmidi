
// repository/CartRepository.kt
package org.example.org.example.repository


import org.example.model.entity.Cart
import org.example.org.example.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface CartRepository : JpaRepository<Cart, Long> {

    // Buscar carrito por usuario
    fun findByUser(user: User): Optional<Cart>

    // Buscar carrito por ID de usuario
    fun findByUserId(userId: Long): Optional<Cart>

    // Verificar si un usuario tiene carrito
    fun existsByUserId(userId: Long): Boolean

    // Eliminar carrito por usuario
    fun deleteByUser(user: User)

    // Eliminar carrito por ID de usuario
    fun deleteByUserId(userId: Long)

    // Obtener cantidad total de items en el carrito de un usuario
    @Query("SELECT SUM(ci.quantity) FROM Cart c JOIN c.items ci WHERE c.user.id = :userId")
    fun getTotalItemsInCart(@Param("userId") userId: Long): Int?

    // Obtener valor total del carrito de un usuario
    @Query("SELECT SUM(ci.quantity * ci.unitPrice) FROM Cart c JOIN c.items ci WHERE c.user.id = :userId")
    fun getTotalCartValue(@Param("userId") userId: Long): java.math.BigDecimal?
}