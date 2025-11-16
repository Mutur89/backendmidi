package org.example.org.example.model.dto

// model/dto/response/CartItemResponse.kt


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
            return CartItemResponse(
                id = cartItem.id,
                productId = cartItem.product.id,
                productName = cartItem.product.nombre,
                productImage = cartItem.product.imagen,
                productCategory = cartItem.product.categoria,
                productDescription = cartItem.product.descripcion,
                quantity = cartItem.quantity,
                unitPrice = cartItem.unitPrice.toInt(),
                subtotal = (cartItem.unitPrice * BigDecimal(cartItem.quantity)).toInt()
            )
        }
    }
}