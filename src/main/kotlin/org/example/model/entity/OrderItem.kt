package com.example.lvlupbackend.model.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.*

@Entity
@Table(name = "order_items")
class OrderItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference  //AGREGAR ESTO
    var order: Order? = null,

    @Column(nullable = false)
    var productId: Long = 0,

    @Column(nullable = false)
    var productName: String = "",

    @Column(nullable = false)
    var quantity: Int = 1,

    @Column(nullable = false)
    var unitPrice: Int = 0
)