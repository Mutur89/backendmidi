package com.example.lvlupbackend.model.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "products")
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val nombre: String,

    @Column(nullable = false)
    val categoria: String,

    @Column(nullable = false, length = 2000)
    val imagen: String, // URL

    @Column(length = 2000)
    val descripcion: String,

    @Column(nullable = false)
    val precio: Int,

    @Column(nullable = false)
    var stock: Int,

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)

