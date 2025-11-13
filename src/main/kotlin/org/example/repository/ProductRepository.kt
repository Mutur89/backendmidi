
// repository/ProductRepository.kt
package org.example.org.example.repository

import com.example.lvlupbackend.model.entity.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<Product, Long> {

    // Buscar productos por categoría
    fun findByCategoria(categoria: String): List<Product>

    // Buscar productos con stock disponible
    fun findByStockGreaterThan(stock: Int): List<Product>

    // Buscar productos por categoría con stock disponible
    fun findByCategoriaAndStockGreaterThan(categoria: String, stock: Int): List<Product>

    // Buscar productos por nombre (búsqueda parcial, case insensitive)
    fun findByNombreContainingIgnoreCase(nombre: String): List<Product>

    // Buscar productos por rango de precio
    fun findByPrecioBetween(precioMin: Int, precioMax: Int): List<Product>

    // Buscar productos por categoría y rango de precio
    fun findByCategoriaAndPrecioBetween(categoria: String, precioMin: Int, precioMax: Int): List<Product>

    // Obtener productos ordenados por precio ascendente
    fun findAllByOrderByPrecioAsc(): List<Product>

    // Obtener productos ordenados por precio descendente
    fun findAllByOrderByPrecioDesc(): List<Product>

    // Productos más recientes
    fun findTop10ByOrderByCreatedAtDesc(): List<Product>
}