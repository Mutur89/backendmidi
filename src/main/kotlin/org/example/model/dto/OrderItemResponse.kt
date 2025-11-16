package org.example.org.example.model.dto

// model/dto/response/OrderItemResponse.kt

import com.example.lvlupbackend.model.entity.OrderItem

data class OrderItemResponse(
    val id: Long,
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val unitPrice: Int,
    val subtotal: Int
) {
    companion object {
        fun fromEntity(orderItem: OrderItem): OrderItemResponse {
            return OrderItemResponse(
                id = orderItem.id,
                productId = orderItem.productId,
                productName = orderItem.productName,
                quantity = orderItem.quantity,
                unitPrice = orderItem.unitPrice,
                subtotal = orderItem.unitPrice * orderItem.quantity
            )
        }
    }
}