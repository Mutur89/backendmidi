// service/CartService.kt
package org.example.org.example.service

import com.example.lvlupbackend.model.entity.Product
import com.example.lvlupbackend.repository.CartItemRepository
import com.example.lvlupbackend.repository.CartRepository
import com.example.lvlupbackend.repository.ProductRepository
import com.example.lvlupbackend.repository.UserRepository
import org.example.model.entity.Cart
import org.example.model.entity.CartItem
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
@Transactional
class CartService(
    private val cartRepository: CartRepository,
    private val cartItemRepository: CartItemRepository,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository
) {

    /**
     * Obtener o crear el carrito de un usuario
     */
    fun getOrCreateCart(userId: Long): Cart {
        val existingCart = cartRepository.findByUserId(userId)

        if (existingCart.isPresent) {
            return existingCart.get()
        }

        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("Usuario no encontrado con ID: $userId") }

        val newCart = Cart(
            id = null,
            user = user,
            items = mutableListOf()
        )

        return cartRepository.save(newCart)
    }

    /**
     * Obtener carrito por ID de usuario
     */
    fun getCartByUserId(userId: Long): Cart? {
        return cartRepository.findByUserId(userId).orElse(null)
    }

    /**
     * Agregar producto al carrito
     */
    fun addProductToCart(userId: Long, productId: Long, quantity: Int = 1): Cart {
        if (quantity <= 0) {
            throw IllegalArgumentException("La cantidad debe ser mayor a 0")
        }

        val cart = getOrCreateCart(userId)
        val product = productRepository.findById(productId)
            .orElseThrow { IllegalArgumentException("Producto no encontrado con ID: $productId") }

        // Verificar stock disponible
        if (product.stock < quantity) {
            throw IllegalStateException("Producto sin stock disponible")
        }

        val existingItem = cartItemRepository.findByCartAndProduct(cart, product)

        if (existingItem.isPresent) {
            val item = existingItem.get()
            val newQuantity = item.quantity + quantity

            if (product.stock < newQuantity) {
                throw IllegalStateException("No puedes agregar más, stock máximo alcanzado")
            }

            item.quantity = newQuantity
            item.unitPrice = BigDecimal(product.precio)
            cartItemRepository.save(item)
        } else {
            val newItem = CartItem(
                id = null,
                cart = cart,
                product = product,
                quantity = quantity,
                unitPrice = BigDecimal(product.precio)
            )
            cartItemRepository.save(newItem)
            cart.items.add(newItem)
        }

        return cartRepository.save(cart)
    }

    /**
     * Aumentar cantidad de un producto (similar a increaseQuantity)
     */
    fun increaseQuantity(userId: Long, productId: Long): Cart {
        val cart = cartRepository.findByUserId(userId)
            .orElseThrow { IllegalArgumentException("Carrito no encontrado") }

        val product = productRepository.findById(productId)
            .orElseThrow { IllegalArgumentException("Producto no encontrado") }

        val cartItem = cartItemRepository.findByCartAndProduct(cart, product)
            .orElseThrow { IllegalArgumentException("Producto no encontrado en el carrito") }

        if (cartItem.quantity >= product.stock) {
            throw IllegalStateException("No puedes agregar más, stock máximo alcanzado")
        }

        cartItem.quantity += 1
        cartItemRepository.save(cartItem)

        return cartRepository.save(cart)
    }

    /**
     * Disminuir cantidad de un producto (similar a decreaseQuantity)
     */
    fun decreaseQuantity(userId: Long, productId: Long): Cart {
        val cart = cartRepository.findByUserId(userId)
            .orElseThrow { IllegalArgumentException("Carrito no encontrado") }

        val product = productRepository.findById(productId)
            .orElseThrow { IllegalArgumentException("Producto no encontrado") }

        val cartItem = cartItemRepository.findByCartAndProduct(cart, product)
            .orElseThrow { IllegalArgumentException("Producto no encontrado en el carrito") }

        if (cartItem.quantity > 1) {
            cartItem.quantity -= 1
            cartItemRepository.save(cartItem)
        } else {
            // Si la cantidad es 1, eliminar el item
            cart.items.remove(cartItem)
            cartItemRepository.delete(cartItem)
        }

        return cartRepository.save(cart)
    }

    /**
     * Actualizar cantidad exacta de un producto
     */
    fun updateCartItemQuantity(userId: Long, productId: Long, quantity: Int): Cart {
        if (quantity < 0) {
            throw IllegalArgumentException("La cantidad no puede ser negativa")
        }

        if (quantity == 0) {
            return removeProductFromCart(userId, productId)
        }

        val cart = cartRepository.findByUserId(userId)
            .orElseThrow { IllegalArgumentException("Carrito no encontrado") }

        val product = productRepository.findById(productId)
            .orElseThrow { IllegalArgumentException("Producto no encontrado") }

        if (product.stock < quantity) {
            throw IllegalStateException("Stock insuficiente. Disponible: ${product.stock}")
        }

        val cartItem = cartItemRepository.findByCartAndProduct(cart, product)
            .orElseThrow { IllegalArgumentException("Producto no encontrado en el carrito") }

        cartItem.quantity = quantity
        cartItemRepository.save(cartItem)

        return cartRepository.save(cart)
    }

    /**
     * Eliminar un producto del carrito
     */
    fun removeProductFromCart(userId: Long, productId: Long): Cart {
        val cart = cartRepository.findByUserId(userId)
            .orElseThrow { IllegalArgumentException("Carrito no encontrado") }

        val product = productRepository.findById(productId)
            .orElseThrow { IllegalArgumentException("Producto no encontrado") }

        val cartItem = cartItemRepository.findByCartAndProduct(cart, product)
            .orElseThrow { IllegalArgumentException("Producto no encontrado en el carrito") }

        cart.items.remove(cartItem)
        cartItemRepository.delete(cartItem)

        return cartRepository.save(cart)
    }

    /**
     * Vaciar el carrito completamente
     */
    fun clearCart(userId: Long): Cart {
        val cart = cartRepository.findByUserId(userId)
            .orElseThrow { IllegalArgumentException("Carrito no encontrado") }

        cartItemRepository.deleteByCart(cart)
        cart.items.clear()

        return cartRepository.save(cart)
    }

    /**
     * Eliminar carrito completo
     */
    fun deleteCart(userId: Long) {
        val cart = cartRepository.findByUserId(userId)
            .orElseThrow { IllegalArgumentException("Carrito no encontrado") }

        cartRepository.delete(cart)
    }

    /**
     * Obtener total de items en el carrito
     */
    fun getTotalItemsInCart(userId: Long): Int {
        return cartRepository.getTotalItemsInCart(userId) ?: 0
    }

    /**
     * Calcular el subtotal del carrito (sin descuentos)
     */
    fun getCartSubtotal(userId: Long): Int {
        val total = cartRepository.getTotalCartValue(userId) ?: BigDecimal.ZERO
        return total.toInt()
    }

    /**
     * ✨ Validar y aplicar cupón de descuento
     */
    fun applyCoupon(userId: Long, couponCode: String): Map<String, Any> {
        val code = couponCode.trim().uppercase()
        val subtotal = getCartSubtotal(userId)

        val discount = when (code) {
            "DESCUENTO10" -> (subtotal * 0.10).toInt()
            "DESCUENTO20" -> (subtotal * 0.20).toInt()
            "PRIMERACOMPRA" -> (subtotal * 0.15).toInt()
            "LVLUP50" -> 5000
            else -> 0
        }

        val finalTotal = (subtotal - discount).coerceAtLeast(0)

        return mapOf(
            "valid" to (discount > 0),
            "couponCode" to code,
            "subtotal" to subtotal,
            "discount" to discount,
            "finalTotal" to finalTotal
        )
    }

    /**
     * Verificar si el carrito está vacío
     */
    fun isCartEmpty(userId: Long): Boolean {
        val cart = cartRepository.findByUserId(userId).orElse(null)
        return cart == null || cart.items.isEmpty()
    }

    /**
     * Obtener todos los items del carrito
     */
    fun getCartItems(userId: Long): List<CartItem> {
        val cart = cartRepository.findByUserId(userId).orElse(null)
        return cart?.items ?: emptyList()
    }

    /**
     * ✨ Verificar disponibilidad de stock para todos los productos del carrito
     */
    fun validateCartStock(userId: Long): Map<String, Any> {
        val cart = cartRepository.findByUserId(userId)
            .orElseThrow { IllegalArgumentException("Carrito no encontrado") }

        val insufficientStockItems = mutableListOf<Map<String, Any>>()

        for (item in cart.items) {
            if (item.product.stock < item.quantity) {
                insufficientStockItems.add(mapOf(
                    "productId" to item.product.id,
                    "productName" to item.product.nombre,
                    "requestedQuantity" to item.quantity,
                    "availableStock" to item.product.stock
                ))
            }
        }

        return mapOf(
            "valid" to insufficientStockItems.isEmpty(),
            "insufficientStockItems" to insufficientStockItems
        )
    }

    /**
     * Obtener detalles completos del carrito
     */
    fun getCartWithDetails(userId: Long): Map<String, Any> {
        val cart = getCartByUserId(userId) ?: return mapOf(
            "cart" to null,
            "items" to emptyList<CartItem>(),
            "totalItems" to 0,
            "subtotal" to 0
        )

        val totalItems = cart.items.sumOf { it.quantity }
        val subtotal = cart.items.sumOf {
            it.quantity.toBigDecimal() * it.unitPrice
        }.toInt()

        return mapOf(
            "cart" to cart,
            "items" to cart.items,
            "totalItems" to totalItems,
            "subtotal" to subtotal
        )
    }
}