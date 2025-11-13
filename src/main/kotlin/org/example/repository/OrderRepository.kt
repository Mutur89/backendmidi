
// repository/OrderRepository.kt
package org.example.org.example.repository

import com.example.lvlupbackend.model.entity.Order
import org.example.org.example.model.User
import org.example.org.example.model.enum.OrderStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface OrderRepository : JpaRepository<Order, Long> {

    // Buscar órdenes por usuario
    fun findByUser(user: User): List<Order>

    // Buscar órdenes por ID de usuario
    fun findByUserId(userId: Long): List<Order>

    // Buscar órdenes por estado
    fun findByEstado(estado: OrderStatus): List<Order>

    // Buscar órdenes por usuario y estado
    fun findByUserAndEstado(user: User, estado: OrderStatus): List<Order>

    // Buscar órdenes por usuario ordenadas por fecha descendente
    fun findByUserOrderByCreatedAtDesc(user: User): List<Order>

    // Buscar órdenes en un rango de fechas
    fun findByCreatedAtBetween(start: LocalDateTime, end: LocalDateTime): List<Order>

    // Buscar órdenes por método de pago
    fun findByMetodoPago(metodoPago: String): List<Order>

    // Buscar órdenes con cupón aplicado
    fun findByCodigoCuponIsNotNull(): List<Order>

    // Buscar órdenes por código de cupón específico
    fun findByCodigoCupon(codigoCupon: String): List<Order>

    // Obtener total de ventas por usuario
    @Query("SELECT SUM(o.total) FROM Order o WHERE o.user.id = :userId")
    fun getTotalSalesByUser(@Param("userId") userId: Long): Long?

    // Obtener total de ventas por estado
    @Query("SELECT SUM(o.total) FROM Order o WHERE o.estado = :estado")
    fun getTotalSalesByStatus(@Param("estado") estado: OrderStatus): Long?

    // Contar órdenes por usuario
    fun countByUser(user: User): Long

    // Órdenes recientes (últimas 10)
    fun findTop10ByOrderByCreatedAtDesc(): List<Order>
}