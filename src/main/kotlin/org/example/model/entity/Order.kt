package com.example.lvlupbackend.model.entity

import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
import org.example.org.example.model.User
import org.example.org.example.model.enum.OrderStatus
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
data class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false)
    val total: Int,

    @Column(nullable = false)
    val descuento: Int = 0,

    @Column
    val codigoCupon: String? = null,

    @Column(nullable = false)
    val metodoPago: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var estado: OrderStatus = OrderStatus.PENDIENTE,

    @Column(nullable = false)
    val direccionEnvio: String,

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonManagedReference  // ✅ AGREGAR ESTO
    val items: MutableList<OrderItem> = mutableListOf(),

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)