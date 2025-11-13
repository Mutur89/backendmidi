// kotlin
package com.example.lvlupbackend.model.entity

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "order_items")
class OrderItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    var order: Order? = null, // debe llamarse "order" para matchedBy

    @Column(nullable = false)
    var productId: Long = 0,

    @Column(nullable = false)
    var productName: String = "",

    @Column(nullable = false)
    var quantity: Int = 1,

    @Column(nullable = false)
    var unitPrice: Int = 0
)
