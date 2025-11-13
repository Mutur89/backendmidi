

// repository/CartItemRepository.kt
package org.example.org.example.repository

import com.example.lvlupbackend.model.entity.Product
import org.example.model.entity.Cart
import org.example.model.entity.CartItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface CartItemRepository : JpaRepository<CartItem, Long> {

    // Buscar items por carrito
    fun findByCart(cart: Cart): List<CartItem>

    // Buscar items por ID de carrito
    fun findByCartId(cartId: Long): List<CartItem>

    // Buscar un item específico en un carrito por producto
    fun findByCartAndProduct(cart: Cart, product: Product): Optional<CartItem>

    // Buscar un item por carrito ID y producto ID
    fun findByCartIdAndProductId(cartId: Long, productId: Long): Optional<CartItem>

    // Verificar si un producto existe en un carrito
    fun existsByCartAndProduct(cart: Cart, product: Product): Boolean

    // Verificar si un producto existe en un carrito por IDs
    fun existsByCartIdAndProductId(cartId: Long, productId: Long): Boolean

    // Eliminar item por carrito y producto
    fun deleteByCartAndProduct(cart: Cart, product: Product)

    // Eliminar todos los items de un carrito
    fun deleteByCart(cart: Cart)

    // Eliminar todos los items de un carrito por ID
    fun deleteByCartId(cartId: Long)

    // Contar items en un carrito
    fun countByCart(cart: Cart): Long

    // Contar items en un carrito por ID
    fun countByCartId(cartId: Long): Long

    // Obtener suma de cantidades en un carrito
    @Query("SELECT SUM(ci.quantity) FROM CartItem ci WHERE ci.cart.id = :cartId")
    fun getTotalQuantityInCart(@Param("cartId") cartId: Long): Int?

    // Obtener valor total de un carrito
    @Query("SELECT SUM(ci.quantity * ci.unitPrice) FROM CartItem ci WHERE ci.cart.id = :cartId")
    fun getTotalValueInCart(@Param("cartId") cartId: Long): java.math.BigDecimal?

    // Actualizar cantidad de un item
    @Modifying
    @Query("UPDATE CartItem ci SET ci.quantity = :quantity WHERE ci.id = :itemId")
    fun updateQuantity(@Param("itemId") itemId: Long, @Param("quantity") quantity: Int)

    // Buscar items por producto (para saber en qué carritos está)
    fun findByProduct(product: Product): List<CartItem>

    // Buscar items por producto ID
    fun findByProductId(productId: Long): List<CartItem>
}