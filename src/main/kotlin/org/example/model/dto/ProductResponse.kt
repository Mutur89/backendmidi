package org.example.org.example.model.dto

// model/dto/response/ProductResponse.kt


import com.example.lvlupbackend.model.entity.Product
import java.time.LocalDateTime

data class ProductResponse(
    val id: Long,
    val nombre: String,
    val categoria: String,
    val imagen: String,
    val descripcion: String,
    val precio: Int,
    val stock: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun fromEntity(product: Product): ProductResponse {
            return ProductResponse(
                id = product.id,
                nombre = product.nombre,
                categoria = product.categoria,
                imagen = product.imagen,
                descripcion = product.descripcion,
                precio = product.precio,
                stock = product.stock,
                createdAt = product.createdAt,
                updatedAt = product.updatedAt
            )
        }
    }
}