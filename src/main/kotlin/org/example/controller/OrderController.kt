// controller/OrderController.kt
package org.example.org.example.controller

import org.example.org.example.model.enum.OrderStatus
import org.example.org.example.service.OrderService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = ["*"]) // Permitir peticiones desde Android
class OrderController(
    private val orderService: OrderService
) {

    /**
     * Crear una orden desde el carrito
     * POST /api/orders/create
     */
    @PostMapping("/create")
    fun createOrder(@RequestBody orderData: Map<String, Any>): ResponseEntity<Any> {
        return try {
            val userId = (orderData["userId"] as? Number)?.toLong()
                ?: return ResponseEntity.badRequest().body(mapOf(
                    "success" to false,
                    "message" to "El ID del usuario es requerido"
                ))

            val metodoPago = orderData["metodoPago"] as? String
                ?: return ResponseEntity.badRequest().body(mapOf(
                    "success" to false,
                    "message" to "El método de pago es requerido"
                ))

            val direccionEnvio = orderData["direccionEnvio"] as? String
                ?: return ResponseEntity.badRequest().body(mapOf(
                    "success" to false,
                    "message" to "La dirección de envío es requerida"
                ))

            val codigoCupon = orderData["codigoCupon"] as? String

            val order = orderService.createOrderFromCart(
                userId = userId,
                metodoPago = metodoPago,
                direccionEnvio = direccionEnvio,
                codigoCupon = codigoCupon
            )

            ResponseEntity.status(HttpStatus.CREATED).body(mapOf(
                "success" to true,
                "message" to "Orden creada exitosamente",
                "data" to order
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
                "message" to "Error al crear orden: ${e.message}"
            ))
        }
    }

    /**
     * Procesar pago y crear orden (crear + marcar como pagada)
     * POST /api/orders/process-payment
     */
    @PostMapping("/process-payment")
    fun processPayment(@RequestBody paymentData: Map<String, Any>): ResponseEntity<Any> {
        return try {
            val userId = (paymentData["userId"] as? Number)?.toLong()
                ?: return ResponseEntity.badRequest().body(mapOf(
                    "success" to false,
                    "message" to "El ID del usuario es requerido"
                ))

            val metodoPago = paymentData["metodoPago"] as? String
                ?: return ResponseEntity.badRequest().body(mapOf(
                    "success" to false,
                    "message" to "El método de pago es requerido"
                ))

            val direccionEnvio = paymentData["direccionEnvio"] as? String
                ?: return ResponseEntity.badRequest().body(mapOf(
                    "success" to false,
                    "message" to "La dirección de envío es requerida"
                ))

            val codigoCupon = paymentData["codigoCupon"] as? String

            val order = orderService.processOrderPayment(
                userId = userId,
                metodoPago = metodoPago,
                direccionEnvio = direccionEnvio,
                codigoCupon = codigoCupon
            )

            ResponseEntity.status(HttpStatus.CREATED).body(mapOf(
                "success" to true,
                "message" to "Pago procesado exitosamente",
                "data" to order
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
                "message" to "Error al procesar pago: ${e.message}"
            ))
        }
    }

    /**
     * Obtener orden por ID
     * GET /api/orders/{orderId}
     */
    @GetMapping("/{orderId}")
    fun getOrderById(@PathVariable orderId: Long): ResponseEntity<Any> {
        return try {
            val order = orderService.getOrderById(orderId)
            ResponseEntity.ok(mapOf(
                "success" to true,
                "data" to order
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf(
                "success" to false,
                "message" to e.message
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al obtener orden: ${e.message}"
            ))
        }
    }

    /**
     * Obtener detalles completos de una orden
     * GET /api/orders/{orderId}/details
     */
    @GetMapping("/{orderId}/details")
    fun getOrderDetails(@PathVariable orderId: Long): ResponseEntity<Any> {
        return try {
            val orderDetails = orderService.getOrderWithDetails(orderId)
            ResponseEntity.ok(mapOf(
                "success" to true,
                "data" to orderDetails
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf(
                "success" to false,
                "message" to e.message
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al obtener detalles: ${e.message}"
            ))
        }
    }

    /**
     * Obtener todas las órdenes de un usuario
     * GET /api/orders/user/{userId}
     */
    @GetMapping("/user/{userId}")
    fun getOrdersByUser(@PathVariable userId: Long): ResponseEntity<Any> {
        return try {
            val orders = orderService.getOrdersByUserId(userId)
            ResponseEntity.ok(mapOf(
                "success" to true,
                "data" to orders,
                "count" to orders.size
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al obtener órdenes: ${e.message}"
            ))
        }
    }

    /**
     * Obtener órdenes de un usuario ordenadas por fecha
     * GET /api/orders/user/{userId}/sorted
     */
    @GetMapping("/user/{userId}/sorted")
    fun getOrdersByUserSorted(@PathVariable userId: Long): ResponseEntity<Any> {
        return try {
            val orders = orderService.getOrdersByUserIdSorted(userId)
            ResponseEntity.ok(mapOf(
                "success" to true,
                "data" to orders,
                "count" to orders.size
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf(
                "success" to false,
                "message" to e.message
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al obtener órdenes: ${e.message}"
            ))
        }
    }

    /**
     * Obtener historial completo de órdenes de un usuario con estadísticas
     * GET /api/orders/user/{userId}/history
     */
    @GetMapping("/user/{userId}/history")
    fun getUserOrderHistory(@PathVariable userId: Long): ResponseEntity<Any> {
        return try {
            val history = orderService.getUserOrderHistory(userId)
            ResponseEntity.ok(mapOf(
                "success" to true,
                "data" to history
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al obtener historial: ${e.message}"
            ))
        }
    }

    /**
     * Obtener órdenes por estado
     * GET /api/orders/status/{estado}
     */
    @GetMapping("/status/{estado}")
    fun getOrdersByStatus(@PathVariable estado: String): ResponseEntity<Any> {
        return try {
            val orderStatus = OrderStatus.valueOf(estado.uppercase())
            val orders = orderService.getOrdersByStatus(orderStatus)
            ResponseEntity.ok(mapOf(
                "success" to true,
                "data" to orders,
                "count" to orders.size,
                "status" to estado
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf(
                "success" to false,
                "message" to "Estado inválido: $estado. Estados válidos: PENDIENTE, PAGADO, ENVIADO, COMPLETA, CANCELADA"
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al obtener órdenes: ${e.message}"
            ))
        }
    }

    /**
     * Obtener órdenes de un usuario por estado
     * GET /api/orders/user/{userId}/status/{estado}
     */
    @GetMapping("/user/{userId}/status/{estado}")
    fun getOrdersByUserAndStatus(
        @PathVariable userId: Long,
        @PathVariable estado: String
    ): ResponseEntity<Any> {
        return try {
            val orderStatus = OrderStatus.valueOf(estado.uppercase())
            val orders = orderService.getOrdersByUserIdAndStatus(userId, orderStatus)
            ResponseEntity.ok(mapOf(
                "success" to true,
                "data" to orders,
                "count" to orders.size,
                "status" to estado
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf(
                "success" to false,
                "message" to e.message
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al obtener órdenes: ${e.message}"
            ))
        }
    }

    /**
     * Actualizar estado de una orden
     * PUT /api/orders/{orderId}/status
     */
    @PutMapping("/{orderId}/status")
    fun updateOrderStatus(
        @PathVariable orderId: Long,
        @RequestBody statusData: Map<String, String>
    ): ResponseEntity<Any> {
        return try {
            val nuevoEstado = statusData["estado"]
                ?: return ResponseEntity.badRequest().body(mapOf(
                    "success" to false,
                    "message" to "El estado es requerido"
                ))

            val orderStatus = OrderStatus.valueOf(nuevoEstado.uppercase())
            val order = orderService.updateOrderStatus(orderId, orderStatus)

            ResponseEntity.ok(mapOf(
                "success" to true,
                "message" to "Estado actualizado exitosamente",
                "data" to order
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf(
                "success" to false,
                "message" to e.message
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al actualizar estado: ${e.message}"
            ))
        }
    }

    /**
     * Marcar orden como pagada
     * PUT /api/orders/{orderId}/mark-paid
     */
    @PutMapping("/{orderId}/mark-paid")
    fun markAsPaid(@PathVariable orderId: Long): ResponseEntity<Any> {
        return try {
            val order = orderService.markAsPaid(orderId)
            ResponseEntity.ok(mapOf(
                "success" to true,
                "message" to "Orden marcada como pagada",
                "data" to order
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf(
                "success" to false,
                "message" to e.message
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al actualizar orden: ${e.message}"
            ))
        }
    }

    /**
     * Marcar orden como enviada
     * PUT /api/orders/{orderId}/mark-shipped
     */
    @PutMapping("/{orderId}/mark-shipped")
    fun markAsShipped(@PathVariable orderId: Long): ResponseEntity<Any> {
        return try {
            val order = orderService.markAsShipped(orderId)
            ResponseEntity.ok(mapOf(
                "success" to true,
                "message" to "Orden marcada como enviada",
                "data" to order
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf(
                "success" to false,
                "message" to e.message
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al actualizar orden: ${e.message}"
            ))
        }
    }

    /**
     * Marcar orden como completada
     * PUT /api/orders/{orderId}/mark-completed
     */
    @PutMapping("/{orderId}/mark-completed")
    fun markAsCompleted(@PathVariable orderId: Long): ResponseEntity<Any> {
        return try {
            val order = orderService.markAsCompleted(orderId)
            ResponseEntity.ok(mapOf(
                "success" to true,
                "message" to "Orden marcada como completada",
                "data" to order
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf(
                "success" to false,
                "message" to e.message
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al actualizar orden: ${e.message}"
            ))
        }
    }

    /**
     * Cancelar una orden
     * PUT /api/orders/{orderId}/cancel
     */
    @PutMapping("/{orderId}/cancel")
    fun cancelOrder(@PathVariable orderId: Long): ResponseEntity<Any> {
        return try {
            val order = orderService.cancelOrder(orderId)
            ResponseEntity.ok(mapOf(
                "success" to true,
                "message" to "Orden cancelada exitosamente",
                "data" to order
            ))
        } catch (e: IllegalStateException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf(
                "success" to false,
                "message" to e.message
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf(
                "success" to false,
                "message" to e.message
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al cancelar orden: ${e.message}"
            ))
        }
    }

    /**
     * Obtener items de una orden
     * GET /api/orders/{orderId}/items
     */
    @GetMapping("/{orderId}/items")
    fun getOrderItems(@PathVariable orderId: Long): ResponseEntity<Any> {
        return try {
            val items = orderService.getOrderItems(orderId)
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

    /**
     * Validar si se puede crear una orden desde el carrito
     * GET /api/orders/validate/{userId}
     */
    @GetMapping("/validate/{userId}")
    fun validateOrderCreation(@PathVariable userId: Long): ResponseEntity<Any> {
        return try {
            val validation = orderService.validateOrderCreation(userId)
            ResponseEntity.ok(mapOf(
                "success" to true,
                "data" to validation
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al validar orden: ${e.message}"
            ))
        }
    }

    /**
     * Obtener órdenes recientes (admin)
     * GET /api/orders/recent
     */
    @GetMapping("/recent")
    fun getRecentOrders(): ResponseEntity<Any> {
        return try {
            val orders = orderService.getRecentOrders()
            ResponseEntity.ok(mapOf(
                "success" to true,
                "data" to orders,
                "count" to orders.size
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al obtener órdenes: ${e.message}"
            ))
        }
    }

    /**
     * Obtener órdenes pendientes (admin)
     * GET /api/orders/pending
     */
    @GetMapping("/pending")
    fun getPendingOrders(): ResponseEntity<Any> {
        return try {
            val orders = orderService.getPendingOrders()
            ResponseEntity.ok(mapOf(
                "success" to true,
                "data" to orders,
                "count" to orders.size
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al obtener órdenes: ${e.message}"
            ))
        }
    }

    /**
     * Obtener órdenes listas para enviar (admin)
     * GET /api/orders/ready-to-ship
     */
    @GetMapping("/ready-to-ship")
    fun getOrdersReadyToShip(): ResponseEntity<Any> {
        return try {
            val orders = orderService.getOrdersReadyToShip()
            ResponseEntity.ok(mapOf(
                "success" to true,
                "data" to orders,
                "count" to orders.size
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al obtener órdenes: ${e.message}"
            ))
        }
    }

    /**
     * Obtener total de ventas de un usuario
     * GET /api/orders/user/{userId}/total-sales
     */
    @GetMapping("/user/{userId}/total-sales")
    fun getTotalSalesByUser(@PathVariable userId: Long): ResponseEntity<Any> {
        return try {
            val totalSales = orderService.getTotalSalesByUser(userId)
            ResponseEntity.ok(mapOf(
                "success" to true,
                "totalSales" to totalSales
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al obtener ventas: ${e.message}"
            ))
        }
    }

    /**
     * Contar órdenes de un usuario
     * GET /api/orders/user/{userId}/count
     */
    @GetMapping("/user/{userId}/count")
    fun countOrdersByUser(@PathVariable userId: Long): ResponseEntity<Any> {
        return try {
            val count = orderService.countOrdersByUserId(userId)
            ResponseEntity.ok(mapOf(
                "success" to true,
                "count" to count
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al contar órdenes: ${e.message}"
            ))
        }
    }
}