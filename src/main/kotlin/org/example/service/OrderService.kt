// service/OrderService.kt
package org.example.org.example.service

import com.example.lvlupbackend.model.entity.Order
import com.example.lvlupbackend.model.entity.OrderItem
import com.example.lvlupbackend.repository.OrderRepository
import com.example.lvlupbackend.repository.OrderItemRepository
import com.example.lvlupbackend.repository.ProductRepository
import com.example.lvlupbackend.repository.UserRepository
import org.example.org.example.model.enum.OrderStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class OrderService(
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository,
    private val cartService: CartService
) {

    /**
     * Crear una orden desde el carrito del usuario
     * Este método replica el processPayment() de Android
     */
    fun createOrderFromCart(
        userId: Long,
        metodoPago: String,
        direccionEnvio: String,
        codigoCupon: String? = null
    ): Order {
        // 1. Obtener el carrito y validar que no esté vacío
        val cart = cartService.getCartByUserId(userId)
            ?: throw IllegalArgumentException("El carrito está vacío")

        if (cart.items.isEmpty()) {
            throw IllegalArgumentException("El carrito está vacío")
        }

        // 2. Verificar stock de todos los productos
        val stockValidation = cartService.validateCartStock(userId)
        if (stockValidation["valid"] == false) {
            val insufficientItems = stockValidation["insufficientStockItems"] as List<*>
            throw IllegalStateException("Stock insuficiente para algunos productos: $insufficientItems")
        }

        // 3. Calcular totales
        val subtotal = cartService.getCartSubtotal(userId)
        var descuento = 0

        // Aplicar cupón si existe
        if (!codigoCupon.isNullOrBlank()) {
            val couponResult = cartService.applyCoupon(userId, codigoCupon)
            if (couponResult["valid"] == true) {
                descuento = couponResult["discount"] as Int
            }
        }

        val total = (subtotal - descuento).coerceAtLeast(0)

        // 4. Obtener el usuario
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("Usuario no encontrado con ID: $userId") }

        // 5. Crear la orden
        val order = Order(
            user = user,
            total = total,
            descuento = descuento,
            codigoCupon = codigoCupon?.uppercase(),
            metodoPago = metodoPago,
            estado = OrderStatus.PENDIENTE,
            direccionEnvio = direccionEnvio,
            items = mutableListOf(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val savedOrder = orderRepository.save(order)

        // 6. Crear los OrderItems desde los CartItems
        for (cartItem in cart.items) {
            val orderItem = OrderItem(
                order = savedOrder,
                productId = cartItem.product.id,
                productName = cartItem.product.nombre,
                quantity = cartItem.quantity,
                unitPrice = cartItem.unitPrice.toInt()
            )
            orderItemRepository.save(orderItem)
            savedOrder.items.add(orderItem)
        }

        // 7. Reducir el stock de cada producto
        for (cartItem in cart.items) {
            val product = cartItem.product
            product.stock = product.stock - cartItem.quantity
            product.updatedAt = LocalDateTime.now()
            productRepository.save(product)
        }

        // 8. Limpiar el carrito
        cartService.clearCart(userId)

        return savedOrder
    }

    /**
     * Obtener orden por ID
     */
    fun getOrderById(orderId: Long): Order {
        return orderRepository.findById(orderId)
            .orElseThrow { IllegalArgumentException("Orden no encontrada con ID: $orderId") }
    }

    /**
     * Obtener todas las órdenes de un usuario
     */
    fun getOrdersByUserId(userId: Long): List<Order> {
        return orderRepository.findByUserId(userId)
    }

    /**
     * Obtener órdenes de un usuario ordenadas por fecha (más reciente primero)
     */
    fun getOrdersByUserIdSorted(userId: Long): List<Order> {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("Usuario no encontrado") }
        return orderRepository.findByUserOrderByCreatedAtDesc(user)
    }

    /**
     * Obtener órdenes por estado
     */
    fun getOrdersByStatus(estado: OrderStatus): List<Order> {
        return orderRepository.findByEstado(estado)
    }

    /**
     * Obtener órdenes de un usuario por estado
     */
    fun getOrdersByUserIdAndStatus(userId: Long, estado: OrderStatus): List<Order> {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("Usuario no encontrado") }
        return orderRepository.findByUserAndEstado(user, estado)
    }

    /**
     * Actualizar el estado de una orden
     */
    fun updateOrderStatus(orderId: Long, nuevoEstado: OrderStatus): Order {
        val order = getOrderById(orderId)
        order.estado = nuevoEstado
        order.updatedAt = LocalDateTime.now()
        return orderRepository.save(order)
    }

    /**
     * Marcar orden como PAGADO
     */
    fun markAsPaid(orderId: Long): Order {
        return updateOrderStatus(orderId, OrderStatus.PAGADO)
    }

    /**
     * Marcar orden como ENVIADO
     */
    fun markAsShipped(orderId: Long): Order {
        return updateOrderStatus(orderId, OrderStatus.ENVIADO)
    }

    /**
     * Marcar orden como COMPLETA
     */
    fun markAsCompleted(orderId: Long): Order {
        return updateOrderStatus(orderId, OrderStatus.COMPLETA)
    }

    /**
     * Cancelar una orden (solo si está PENDIENTE o PAGADO)
     */
    fun cancelOrder(orderId: Long): Order {
        val order = getOrderById(orderId)

        // Solo se puede cancelar si está PENDIENTE o PAGADO
        if (order.estado != OrderStatus.PENDIENTE && order.estado != OrderStatus.PAGADO) {
            throw IllegalStateException("No se puede cancelar una orden con estado: ${order.estado}")
        }

        // Restaurar el stock de los productos
        for (item in order.items) {
            val product = productRepository.findById(item.productId)
                .orElseThrow { IllegalArgumentException("Producto no encontrado: ${item.productId}") }

            product.stock = product.stock + item.quantity
            product.updatedAt = LocalDateTime.now()
            productRepository.save(product)
        }

        order.estado = OrderStatus.CANCELADA
        order.updatedAt = LocalDateTime.now()
        return orderRepository.save(order)
    }

    /**
     * Obtener items de una orden
     */
    fun getOrderItems(orderId: Long): List<OrderItem> {
        return orderItemRepository.findByOrderId(orderId)
    }

    /**
     * Obtener total de ventas de un usuario
     */
    fun getTotalSalesByUser(userId: Long): Long {
        return orderRepository.getTotalSalesByUser(userId) ?: 0L
    }

    /**
     * Contar órdenes de un usuario
     */
    fun countOrdersByUserId(userId: Long): Long {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("Usuario no encontrado") }
        return orderRepository.countByUser(user)
    }

    /**
     * Obtener las últimas órdenes (para admin)
     */
    fun getRecentOrders(): List<Order> {
        return orderRepository.findTop10ByOrderByCreatedAtDesc()
    }

    /**
     * Obtener detalles completos de una orden con items
     */
    fun getOrderWithDetails(orderId: Long): Map<String, Any> {
        val order = getOrderById(orderId)
        val items = order.items

        return mapOf(
            "order" to order,
            "items" to items,
            "itemCount" to items.size,
            "totalQuantity" to items.sumOf { it.quantity },
            "subtotal" to (order.total + order.descuento),
            "discount" to order.descuento,
            "total" to order.total,
            "status" to order.estado.name,
            "statusDisplay" to when(order.estado) {
                OrderStatus.PENDIENTE -> "Pendiente"
                OrderStatus.PAGADO -> "Pagado"
                OrderStatus.ENVIADO -> "Enviado"
                OrderStatus.COMPLETA -> "Completada"
                OrderStatus.CANCELADA -> "Cancelada"
            },
            "createdAt" to order.createdAt,
            "updatedAt" to order.updatedAt
        )
    }

    /**
     * Validar si se puede crear una orden desde el carrito
     */
    fun validateOrderCreation(userId: Long): Map<String, Any> {
        try {
            // Verificar que el carrito no esté vacío
            if (cartService.isCartEmpty(userId)) {
                return mapOf(
                    "valid" to false,
                    "message" to "El carrito está vacío"
                )
            }

            // Verificar stock
            val stockValidation = cartService.validateCartStock(userId)
            if (stockValidation["valid"] == false) {
                return mapOf(
                    "valid" to false,
                    "message" to "Stock insuficiente para algunos productos",
                    "details" to (stockValidation["insufficientStockItems"] ?: emptyList<Any>())
                )
            }

            return mapOf(
                "valid" to true,
                "message" to "Orden puede ser creada"
            )
        } catch (e: Exception) {
            return mapOf(
                "valid" to false,
                "message" to (e.message ?: "Error desconocido")
            )
        }
    }

    /**
     * Obtener historial completo de órdenes de un usuario con estadísticas
     */
    fun getUserOrderHistory(userId: Long): Map<String, Any> {
        val orders = getOrdersByUserId(userId)

        return mapOf(
            "orders" to orders,
            "totalOrders" to orders.size,
            "totalSpent" to orders.sumOf { it.total },
            "totalSaved" to orders.sumOf { it.descuento },
            "ordersByStatus" to mapOf(
                "pendiente" to orders.count { it.estado == OrderStatus.PENDIENTE },
                "pagado" to orders.count { it.estado == OrderStatus.PAGADO },
                "enviado" to orders.count { it.estado == OrderStatus.ENVIADO },
                "completa" to orders.count { it.estado == OrderStatus.COMPLETA },
                "cancelada" to orders.count { it.estado == OrderStatus.CANCELADA }
            )
        )
    }

    /**
     * Procesar el flujo completo de una orden (crear y marcar como pagada)
     */
    fun processOrderPayment(
        userId: Long,
        metodoPago: String,
        direccionEnvio: String,
        codigoCupon: String? = null
    ): Order {
        // Crear la orden
        val order = createOrderFromCart(userId, metodoPago, direccionEnvio, codigoCupon)

        // Marcar como pagada automáticamente
        return markAsPaid(order.id)
    }

    /**
     * Obtener órdenes pendientes (para admin)
     */
    fun getPendingOrders(): List<Order> {
        return orderRepository.findByEstado(OrderStatus.PENDIENTE)
    }

    /**
     * Obtener órdenes que necesitan ser enviadas (para admin)
     */
    fun getOrdersReadyToShip(): List<Order> {
        return orderRepository.findByEstado(OrderStatus.PAGADO)
    }
}