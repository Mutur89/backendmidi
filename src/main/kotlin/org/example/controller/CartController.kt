// controller/CartController.kt
package org.example.org.example.controller

import org.example.org.example.service.CartService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = ["*"]) // Permitir peticiones desde Android
class CartController(
    private val cartService: CartService
) {

    /**
     * Obtener carrito del usuario
     * GET /api/cart/{userId}
     */
    @GetMapping("/{userId}")
    fun getCart(@PathVariable userId: Long): ResponseEntity<Any> {
        return try {
            val cart = cartService.getCartByUserId(userId)

            if (cart == null) {
                ResponseEntity.ok(mapOf(
                    "success" to true,
                    "message" to "El carrito está vacío",
                    "data" to null
                ))
            } else {
                ResponseEntity.ok(mapOf(
                    "success" to true,
                    "data" to cart
                ))
            }
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al obtener carrito: ${e.message}"
            ))
        }
    }

    /**
     * Obtener detalles completos del carrito
     * GET /api/cart/{userId}/details
     */
    @GetMapping("/{userId}/details")
    fun getCartDetails(@PathVariable userId: Long): ResponseEntity<Any> {
        return try {
            val cartDetails = cartService.getCartWithDetails(userId)
            ResponseEntity.ok(mapOf(
                "success" to true,
                "data" to cartDetails
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al obtener detalles del carrito: ${e.message}"
            ))
        }
    }

    /**
     * Agregar producto al carrito
     * POST /api/cart/{userId}/add
     */
    @PostMapping("/{userId}/add")
    fun addProductToCart(
        @PathVariable userId: Long,
        @RequestBody productData: Map<String, Any>
    ): ResponseEntity<Any> {
        return try {
            val productId = (productData["productId"] as? Number)?.toLong()
                ?: return ResponseEntity.badRequest().body(mapOf(
                    "success" to false,
                    "message" to "El ID del producto es requerido"
                ))

            val quantity = (productData["quantity"] as? Number)?.toInt() ?: 1

            val cart = cartService.addProductToCart(userId, productId, quantity)

            ResponseEntity.ok(mapOf(
                "success" to true,
                "message" to "Producto agregado al carrito",
                "data" to cart
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf(
                "success" to false,
                "message" to e.message
            ))
        } catch (e: IllegalStateException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf(
                "success" to false,
                "message" to e.message
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al agregar producto: ${e.message}"
            ))
        }
    }

    /**
     * Aumentar cantidad de un producto
     * POST /api/cart/{userId}/increase
     */
    @PostMapping("/{userId}/increase")
    fun increaseQuantity(
        @PathVariable userId: Long,
        @RequestBody productData: Map<String, Any>
    ): ResponseEntity<Any> {
        return try {
            val productId = (productData["productId"] as? Number)?.toLong()
                ?: return ResponseEntity.badRequest().body(mapOf(
                    "success" to false,
                    "message" to "El ID del producto es requerido"
                ))

            val cart = cartService.increaseQuantity(userId, productId)

            ResponseEntity.ok(mapOf(
                "success" to true,
                "message" to "Cantidad aumentada",
                "data" to cart
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf(
                "success" to false,
                "message" to e.message
            ))
        } catch (e: IllegalStateException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf(
                "success" to false,
                "message" to e.message
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al aumentar cantidad: ${e.message}"
            ))
        }
    }

    /**
     * Disminuir cantidad de un producto
     * POST /api/cart/{userId}/decrease
     */
    @PostMapping("/{userId}/decrease")
    fun decreaseQuantity(
        @PathVariable userId: Long,
        @RequestBody productData: Map<String, Any>
    ): ResponseEntity<Any> {
        return try {
            val productId = (productData["productId"] as? Number)?.toLong()
                ?: return ResponseEntity.badRequest().body(mapOf(
                    "success" to false,
                    "message" to "El ID del producto es requerido"
                ))

            val cart = cartService.decreaseQuantity(userId, productId)

            ResponseEntity.ok(mapOf(
                "success" to true,
                "message" to "Cantidad disminuida",
                "data" to cart
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf(
                "success" to false,
                "message" to e.message
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al disminuir cantidad: ${e.message}"
            ))
        }
    }

    /**
     * Actualizar cantidad exacta de un producto
     * PUT /api/cart/{userId}/update-quantity
     */
    @PutMapping("/{userId}/update-quantity")
    fun updateQuantity(
        @PathVariable userId: Long,
        @RequestBody quantityData: Map<String, Any>
    ): ResponseEntity<Any> {
        return try {
            val productId = (quantityData["productId"] as? Number)?.toLong()
                ?: return ResponseEntity.badRequest().body(mapOf(
                    "success" to false,
                    "message" to "El ID del producto es requerido"
                ))

            val quantity = (quantityData["quantity"] as? Number)?.toInt()
                ?: return ResponseEntity.badRequest().body(mapOf(
                    "success" to false,
                    "message" to "La cantidad es requerida"
                ))

            val cart = cartService.updateCartItemQuantity(userId, productId, quantity)

            ResponseEntity.ok(mapOf(
                "success" to true,
                "message" to "Cantidad actualizada",
                "data" to cart
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf(
                "success" to false,
                "message" to e.message
            ))
        } catch (e: IllegalStateException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf(
                "success" to false,
                "message" to e.message
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al actualizar cantidad: ${e.message}"
            ))
        }
    }

    /**
     * Eliminar producto del carrito
     * DELETE /api/cart/{userId}/remove/{productId}
     */
    @DeleteMapping("/{userId}/remove/{productId}")
    fun removeProduct(
        @PathVariable userId: Long,
        @PathVariable productId: Long
    ): ResponseEntity<Any> {
        return try {
            val cart = cartService.removeProductFromCart(userId, productId)

            ResponseEntity.ok(mapOf(
                "success" to true,
                "message" to "Producto eliminado del carrito",
                "data" to cart
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf(
                "success" to false,
                "message" to e.message
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al eliminar producto: ${e.message}"
            ))
        }
    }

    /**
     * Vaciar carrito completamente
     * DELETE /api/cart/{userId}/clear
     */
    @DeleteMapping("/{userId}/clear")
    fun clearCart(@PathVariable userId: Long): ResponseEntity<Any> {
        return try {
            val cart = cartService.clearCart(userId)

            ResponseEntity.ok(mapOf(
                "success" to true,
                "message" to "Carrito vaciado exitosamente",
                "data" to cart
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf(
                "success" to false,
                "message" to e.message
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al vaciar carrito: ${e.message}"
            ))
        }
    }

    /**
     * Obtener total de items en el carrito
     * GET /api/cart/{userId}/count
     */
    @GetMapping("/{userId}/count")
    fun getCartItemCount(@PathVariable userId: Long): ResponseEntity<Any> {
        return try {
            val count = cartService.getTotalItemsInCart(userId)

            ResponseEntity.ok(mapOf(
                "success" to true,
                "count" to count
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al contar items: ${e.message}"
            ))
        }
    }

    /**
     * Obtener subtotal del carrito
     * GET /api/cart/{userId}/subtotal
     */
    @GetMapping("/{userId}/subtotal")
    fun getCartSubtotal(@PathVariable userId: Long): ResponseEntity<Any> {
        return try {
            val subtotal = cartService.getCartSubtotal(userId)

            ResponseEntity.ok(mapOf(
                "success" to true,
                "subtotal" to subtotal
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al calcular subtotal: ${e.message}"
            ))
        }
    }

    /**
     * Aplicar cupón de descuento
     * POST /api/cart/{userId}/apply-coupon
     */
    @PostMapping("/{userId}/apply-coupon")
    fun applyCoupon(
        @PathVariable userId: Long,
        @RequestBody couponData: Map<String, String>
    ): ResponseEntity<Any> {
        return try {
            val couponCode = couponData["couponCode"]
                ?: return ResponseEntity.badRequest().body(mapOf(
                    "success" to false,
                    "message" to "El código de cupón es requerido"
                ))

            val result = cartService.applyCoupon(userId, couponCode)

            if (result["valid"] == true) {
                ResponseEntity.ok(mapOf(
                    "success" to true,
                    "message" to "Cupón aplicado exitosamente",
                    "data" to result
                ))
            } else {
                ResponseEntity.ok(mapOf(
                    "success" to false,
                    "message" to "Cupón inválido",
                    "data" to result
                ))
            }
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al aplicar cupón: ${e.message}"
            ))
        }
    }

    /**
     * Validar stock del carrito
     * GET /api/cart/{userId}/validate-stock
     */
    @GetMapping("/{userId}/validate-stock")
    fun validateCartStock(@PathVariable userId: Long): ResponseEntity<Any> {
        return try {
            val validation = cartService.validateCartStock(userId)

            ResponseEntity.ok(mapOf(
                "success" to true,
                "data" to validation
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf(
                "success" to false,
                "message" to e.message
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al validar stock: ${e.message}"
            ))
        }
    }

    /**
     * Verificar si el carrito está vacío
     * GET /api/cart/{userId}/is-empty
     */
    @GetMapping("/{userId}/is-empty")
    fun isCartEmpty(@PathVariable userId: Long): ResponseEntity<Any> {
        return try {
            val isEmpty = cartService.isCartEmpty(userId)

            ResponseEntity.ok(mapOf(
                "success" to true,
                "isEmpty" to isEmpty
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al verificar carrito: ${e.message}"
            ))
        }
    }

    /**
     * Obtener items del carrito
     * GET /api/cart/{userId}/items
     */
    @GetMapping("/{userId}/items")
    fun getCartItems(@PathVariable userId: Long): ResponseEntity<Any> {
        return try {
            val items = cartService.getCartItems(userId)

            ResponseEntity.ok(mapOf(
                "success" to true,
                "data" to items,
                "count" to items.size
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al obtener items: ${e.message}"
            ))
        }
    }
}