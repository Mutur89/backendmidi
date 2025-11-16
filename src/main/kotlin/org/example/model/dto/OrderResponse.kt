package org.example.org.example.model.dto

// model/dto/response/OrderResponse.kt

import com.example.lvlupbackend.model.entity.Order
import org.example.org.example.model.enum.OrderStatus
import java.time.LocalDateTime

data class OrderResponse(
    val id: Long,
    val userId: Long,
    val total: Int,
    val descuento: Int,
    val codigoCupon: String?,
    val metodoPago: String,
    val estado: OrderStatus,
    val direccionEnvio: String,
    val items: List<OrderItemResponse>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun fromEntity(order: Order): OrderResponse {
            return OrderResponse(
                id = order.id,
                userId = order.user.id ?: 0,
                total = order.total,
                descuento = order.descuento,
                codigoCupon = order.codigoCupon,
                metodoPago = order.metodoPago,
                estado = order.estado,
                direccionEnvio = order.direccionEnvio,
                items = order.items.map { OrderItemResponse.fromEntity(it) },
                createdAt = order.createdAt,
                updatedAt = order.updatedAt
            )
        }
    }
}