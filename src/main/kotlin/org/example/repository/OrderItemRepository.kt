// repository/OrderItemRepository.kt
package org.example.org.example.repository

import com.example.lvlupbackend.model.entity.Order
import com.example.lvlupbackend.model.entity.OrderItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface OrderItemRepository : JpaRepository<OrderItem, Long> {

    // Buscar items por orden
    fun findByOrder(order: Order): List<OrderItem>

    // Buscar items por ID de orden
    fun findByOrderId(orderId: Long): List<OrderItem>

    // Buscar items por ID de producto
    fun findByProductId(productId: Long): List<OrderItem>

    // Obtener productos más vendidos
    @Query("""
        SELECT oi.productId, oi.productName, SUM(oi.quantity) as totalQuantity 
        FROM OrderItem oi 
        GROUP BY oi.productId, oi.productName 
        ORDER BY totalQuantity DESC
    """)
    fun findTopSellingProducts(): List<Array<Any>>

    // Calcular total de unidades vendidas de un producto
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.productId = :productId")
    fun getTotalQuantitySoldByProduct(@Param("productId") productId: Long): Long?

    // Obtener ingresos totales por producto
    @Query("SELECT SUM(oi.quantity * oi.unitPrice) FROM OrderItem oi WHERE oi.productId = :productId")
    fun getTotalRevenueByProduct(@Param("productId") productId: Long): Long?
}