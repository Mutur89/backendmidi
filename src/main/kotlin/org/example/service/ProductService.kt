// service/ProductService.kt
package org.example.org.example.service

import com.example.lvlupbackend.model.entity.Product
import org.example.org.example.repository.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class ProductService(
    private val productRepository: ProductRepository
) {

    /**
     * Obtener todos los productos
     */
    fun getAllProducts(): List<Product> {
        return productRepository.findAll()
    }

    /**
     * Obtener productos con stock disponible
     */
    fun getProductsWithStock(): List<Product> {
        return productRepository.findByStockGreaterThan(0)
    }

    /**
     * Obtener producto por ID
     */
    fun getProductById(productId: Long): Product {
        return productRepository.findById(productId)
            .orElseThrow { IllegalArgumentException("Producto no encontrado con ID: $productId") }
    }

    /**
     * Obtener productos por categoría
     */
    fun getProductsByCategory(categoria: String): List<Product> {
        // Si la categoría es "Todos", retornar todos los productos
        if (categoria.equals("Todos", ignoreCase = true)) {
            return getAllProducts()
        }
        return productRepository.findByCategoria(categoria)
    }

    /**
     * Obtener todas las categorías únicas
     */
    fun getAllCategories(): List<String> {
        val allProducts = productRepository.findAll()
        return allProducts.map { it.categoria }.distinct().sorted()
    }

    /**
     * Obtener categorías con "Todos" incluido (para el filtro)
     */
    fun getCategoriesWithAll(): List<String> {
        val categories = getAllCategories().toMutableList()
        categories.add(0, "Todos") // Agregar "Todos" al inicio
        return categories
    }

    /**
     * Buscar productos por nombre (búsqueda parcial)
     */
    fun searchProductsByName(nombre: String): List<Product> {
        return productRepository.findByNombreContainingIgnoreCase(nombre)
    }

    /**
     * Obtener productos por rango de precio
     */
    fun getProductsByPriceRange(minPrice: Int, maxPrice: Int): List<Product> {
        return productRepository.findByPrecioBetween(minPrice, maxPrice)
    }

    /**
     * Obtener productos por categoría y con stock disponible
     */
    fun getProductsByCategoryWithStock(categoria: String): List<Product> {
        if (categoria.equals("Todos", ignoreCase = true)) {
            return getProductsWithStock()
        }
        return productRepository.findByCategoriaAndStockGreaterThan(categoria, 0)
    }

    /**
     * Crear un nuevo producto
     */
    fun createProduct(
        nombre: String,
        categoria: String,
        imagen: String,
        descripcion: String,
        precio: Int,
        stock: Int
    ): Product {
        // Validaciones
        if (nombre.isBlank()) {
            throw IllegalArgumentException("El nombre del producto es obligatorio")
        }
        if (categoria.isBlank()) {
            throw IllegalArgumentException("La categoría es obligatoria")
        }
        if (precio < 0) {
            throw IllegalArgumentException("El precio no puede ser negativo")
        }
        if (stock < 0) {
            throw IllegalArgumentException("El stock no puede ser negativo")
        }

        val newProduct = Product(
            nombre = nombre,
            categoria = categoria,
            imagen = imagen,
            descripcion = descripcion,
            precio = precio,
            stock = stock,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        return productRepository.save(newProduct)
    }

    /**
     * Actualizar un producto existente
     */
    fun updateProduct(
        productId: Long,
        nombre: String? = null,
        categoria: String? = null,
        imagen: String? = null,
        descripcion: String? = null,
        precio: Int? = null,
        stock: Int? = null
    ): Product {
        val product = getProductById(productId)

        // Validar precio si se proporciona
        precio?.let {
            if (it < 0) throw IllegalArgumentException("El precio no puede ser negativo")
        }

        // Validar stock si se proporciona
        stock?.let {
            if (it < 0) throw IllegalArgumentException("El stock no puede ser negativo")
        }

        // Actualizar producto
        val updatedProduct = product.copy(
            nombre = nombre ?: product.nombre,
            categoria = categoria ?: product.categoria,
            imagen = imagen ?: product.imagen,
            descripcion = descripcion ?: product.descripcion,
            precio = precio ?: product.precio,
            stock = stock ?: product.stock,
            updatedAt = LocalDateTime.now()
        )

        return productRepository.save(updatedProduct)
    }

    /**
     * Actualizar solo el stock de un producto
     */
    fun updateProductStock(productId: Long, newStock: Int): Product {
        if (newStock < 0) {
            throw IllegalArgumentException("El stock no puede ser negativo")
        }

        val product = getProductById(productId)
        val updatedProduct = product.copy(
            stock = newStock,
            updatedAt = LocalDateTime.now()
        )

        return productRepository.save(updatedProduct)
    }

    /**
     * Reducir stock de un producto (usado al crear órdenes)
     */
    fun reduceStock(productId: Long, quantity: Int): Product {
        if (quantity <= 0) {
            throw IllegalArgumentException("La cantidad debe ser mayor a 0")
        }

        val product = getProductById(productId)

        if (product.stock < quantity) {
            throw IllegalStateException("Stock insuficiente. Disponible: ${product.stock}, Solicitado: $quantity")
        }

        val updatedProduct = product.copy(
            stock = product.stock - quantity,
            updatedAt = LocalDateTime.now()
        )

        return productRepository.save(updatedProduct)
    }

    /**
     * Aumentar stock de un producto (usado al cancelar órdenes)
     */
    fun increaseStock(productId: Long, quantity: Int): Product {
        if (quantity <= 0) {
            throw IllegalArgumentException("La cantidad debe ser mayor a 0")
        }

        val product = getProductById(productId)

        val updatedProduct = product.copy(
            stock = product.stock + quantity,
            updatedAt = LocalDateTime.now()
        )

        return productRepository.save(updatedProduct)
    }

    /**
     * Eliminar un producto
     */
    fun deleteProduct(productId: Long) {
        val product = getProductById(productId)
        productRepository.delete(product)
    }

    /**
     * Verificar si hay stock disponible
     */
    fun hasStock(productId: Long, quantity: Int): Boolean {
        val product = getProductById(productId)
        return product.stock >= quantity
    }

    /**
     * Obtener productos ordenados por precio (ascendente)
     */
    fun getProductsSortedByPriceAsc(): List<Product> {
        return productRepository.findAllByOrderByPrecioAsc()
    }

    /**
     * Obtener productos ordenados por precio (descendente)
     */
    fun getProductsSortedByPriceDesc(): List<Product> {
        return productRepository.findAllByOrderByPrecioDesc()
    }

    /**
     * Obtener productos más recientes
     */
    fun getRecentProducts(limit: Int = 10): List<Product> {
        return productRepository.findTop10ByOrderByCreatedAtDesc()
    }

    /**
     * Contar productos por categoría
     */
    fun countProductsByCategory(categoria: String): Long {
        return productRepository.findByCategoria(categoria).size.toLong()
    }

    /**
     * Contar total de productos
     */
    fun getTotalProducts(): Long {
        return productRepository.count()
    }

    /**
     * Contar productos con stock
     */
    fun countProductsWithStock(): Long {
        return productRepository.findByStockGreaterThan(0).size.toLong()
    }

    /**
     * Contar productos sin stock
     */
    fun countProductsOutOfStock(): Long {
        return productRepository.findAll().count { it.stock == 0 }.toLong()
    }

    /**
     * Obtener resumen de un producto
     */
    fun getProductSummary(productId: Long): Map<String, Any> {
        val product = getProductById(productId)
        return mapOf(
            "id" to product.id,
            "nombre" to product.nombre,
            "categoria" to product.categoria,
            "imagen" to product.imagen,
            "descripcion" to product.descripcion,
            "precio" to product.precio,
            "stock" to product.stock,
            "available" to (product.stock > 0),
            "createdAt" to product.createdAt,
            "updatedAt" to product.updatedAt
        )
    }

    /**
     * Crear productos de prueba (útil para desarrollo)
     */
    fun createTestProducts() {
        val testProducts = listOf(
            Product(
                nombre = "Laptop Gaming",
                categoria = "Electrónica",
                imagen = "https://example.com/laptop.jpg",
                descripcion = "Laptop gaming de alta gama con RTX 4070",
                precio = 1500000,
                stock = 10,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            ),
            Product(
                nombre = "Mouse Gamer RGB",
                categoria = "Accesorios",
                imagen = "https://example.com/mouse.jpg",
                descripcion = "Mouse gaming con luces RGB y 16000 DPI",
                precio = 45000,
                stock = 25,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            ),
            Product(
                nombre = "Teclado Mecánico",
                categoria = "Accesorios",
                imagen = "https://example.com/keyboard.jpg",
                descripcion = "Teclado mecánico switches azules",
                precio = 85000,
                stock = 15,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        )

        testProducts.forEach { product ->
            // Solo crear si no existe un producto con el mismo nombre
            try {
                productRepository.save(product)
            } catch (e: Exception) {
                // Ignorar si ya existe
            }
        }
    }
}