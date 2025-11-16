package org.example.org.example.model.dto

// model/dto/response/CartResponse.kt

import org.example.model.entity.Cart

data class CartResponse(
    val id: Long?,
    val userId: Long,
    val items: List<CartItemResponse>,
    val total: Int,
    val itemCount: Int
) {
    companion object {
        fun fromEntity(cart: Cart): CartResponse {
            val items = cart.items.map { CartItemResponse.fromEntity(it) }
            val total = items.sumOf { it.subtotal }

            return CartResponse(
                id = cart.id,
                userId = cart.user?.id ?: 0,
                items = items,
                total = total,
                itemCount = items.sumOf { it.quantity }
            )
        }
    }
}