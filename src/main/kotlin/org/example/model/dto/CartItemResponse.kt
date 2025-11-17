package org.example.model.dto

import org.example.model.entity.CartItem
import java.math.BigDecimal

data class CartItemResponse(
    val id: Long?,
    val productId: Long,
    val productName: String,
    val productImage: String,
    val productCategory: String,
    val productDescription: String,
    val quantity: Int,
    val unitPrice: Int,
    val subtotal: Int
) {
    companion object {
        fun fromEntity(cartItem: CartItem): CartItemResponse {
            val product = cartItem.product ?: throw IllegalStateException("CartItem debe tener un Product asociado")
            val unitPrice = product.precio
            val subtotal = unitPrice * cartItem.quantity

            return CartItemResponse(
                id = cartItem.id,
                productId = product.id ?: 0,
                productName = product.nombre,
                productImage = product.imagen ?: "",
                productCategory = product.categoria ?: "",
                productDescription = product.descripcion ?: "",
                quantity = cartItem.quantity,
                unitPrice = unitPrice,
                subtotal = subtotal
            )
        }
    }
}
