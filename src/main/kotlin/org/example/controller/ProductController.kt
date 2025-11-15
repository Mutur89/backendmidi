// controller/ProductController.kt
package org.example.org.example.controller

import org.example.org.example.service.ProductService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = ["*"]) // Permitir peticiones desde Android
class ProductController(
    private val productService: ProductService
) {

    /**
     * Obtener todos los productos
     * GET /api/products
     */
    @GetMapping
    fun getAllProducts(): ResponseEntity<Any> {
        return try {
            val products = productService.getAllProducts()
            ResponseEntity.ok(mapOf(
                "success" to true,
                "data" to products,
                "count" to products.size
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al obtener productos: ${e.message}"
            ))
        }
    }

    /**
     * Obtener productos con stock disponible
     * GET /api/products/available
     */
    @GetMapping("/available")
    fun getProductsWithStock(): ResponseEntity<Any> {
        return try {
            val products = productService.getProductsWithStock()
            ResponseEntity.ok(mapOf(
                "success" to true,
                "data" to products,
                "count" to products.size
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al obtener productos: ${e.message}"
            ))
        }
    }

    /**
     * Obtener producto por ID
     * GET /api/products/{id}
     */
    @GetMapping("/{id}")
    fun getProductById(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            val product = productService.getProductById(id)
            ResponseEntity.ok(mapOf(
                "success" to true,
                "data" to product
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf(
                "success" to false,
                "message" to e.message
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al obtener producto: ${e.message}"
            ))
        }
    }

    /**
     * Obtener productos por categoría
     * GET /api/products/category/{categoria}
     */
    @GetMapping("/category/{categoria}")
    fun getProductsByCategory(@PathVariable categoria: String): ResponseEntity<Any> {
        return try {
            val products = productService.getProductsByCategory(categoria)
            ResponseEntity.ok(mapOf(
                "success" to true,
                "data" to products,
                "count" to products.size,
                "category" to categoria
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al obtener productos: ${e.message}"
            ))
        }
    }

    /**
     * Obtener todas las categorías
     * GET /api/products/categories
     */
    @GetMapping("/categories")
    fun getAllCategories(): ResponseEntity<Any> {
        return try {
            val categories = productService.getAllCategories()
            ResponseEntity.ok(mapOf(
                "success" to true,
                "data" to categories,
                "count" to categories.size
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al obtener categorías: ${e.message}"
            ))
        }
    }

    /**
     * Obtener categorías con "Todos"
     * GET /api/products/categories/all
     */
    @GetMapping("/categories/all")
    fun getCategoriesWithAll(): ResponseEntity<Any> {
        return try {
            val categories = productService.getCategoriesWithAll()
            ResponseEntity.ok(mapOf(
                "success" to true,
                "data" to categories,
                "count" to categories.size
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al obtener categorías: ${e.message}"
            ))
        }
    }

    /**
     * Buscar productos por nombre
     * GET /api/products/search?name={nombre}
     */
    @GetMapping("/search")
    fun searchProducts(@RequestParam name: String): ResponseEntity<Any> {
        return try {
            val products = productService.searchProductsByName(name)
            ResponseEntity.ok(mapOf(
                "success" to true,
                "data" to products,
                "count" to products.size,
                "searchTerm" to name
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al buscar productos: ${e.message}"
            ))
        }
    }

    /**
     * Obtener productos por rango de precio
     * GET /api/products/price-range?min={minPrice}&max={maxPrice}
     */
    @GetMapping("/price-range")
    fun getProductsByPriceRange(
        @RequestParam min: Int,
        @RequestParam max: Int
    ): ResponseEntity<Any> {
        return try {
            val products = productService.getProductsByPriceRange(min, max)
            ResponseEntity.ok(mapOf(
                "success" to true,
                "data" to products,
                "count" to products.size,
                "priceRange" to mapOf(
                    "min" to min,
                    "max" to max
                )
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al obtener productos: ${e.message}"
            ))
        }
    }

    /**
     * Obtener productos ordenados por precio (ascendente)
     * GET /api/products/sort/price-asc
     */
    @GetMapping("/sort/price-asc")
    fun getProductsSortedByPriceAsc(): ResponseEntity<Any> {
        return try {
            val products = productService.getProductsSortedByPriceAsc()
            ResponseEntity.ok(mapOf(
                "success" to true,
                "data" to products,
                "count" to products.size
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al obtener productos: ${e.message}"
            ))
        }
    }

    /**
     * Obtener productos ordenados por precio (descendente)
     * GET /api/products/sort/price-desc
     */
    @GetMapping("/sort/price-desc")
    fun getProductsSortedByPriceDesc(): ResponseEntity<Any> {
        return try {
            val products = productService.getProductsSortedByPriceDesc()
            ResponseEntity.ok(mapOf(
                "success" to true,
                "data" to products,
                "count" to products.size
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al obtener productos: ${e.message}"
            ))
        }
    }

    /**
     * Obtener productos más recientes
     * GET /api/products/recent
     */
    @GetMapping("/recent")
    fun getRecentProducts(): ResponseEntity<Any> {
        return try {
            val products = productService.getRecentProducts()
            ResponseEntity.ok(mapOf(
                "success" to true,
                "data" to products,
                "count" to products.size
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al obtener productos: ${e.message}"
            ))
        }
    }

    /**
     * Crear un nuevo producto (admin)
     * POST /api/products
     */
    @PostMapping
    fun createProduct(@RequestBody productData: Map<String, Any>): ResponseEntity<Any> {
        return try {
            val nombre = productData["nombre"] as? String
                ?: return ResponseEntity.badRequest().body(mapOf(
                    "success" to false,
                    "message" to "El nombre es requerido"
                ))

            val categoria = productData["categoria"] as? String
                ?: return ResponseEntity.badRequest().body(mapOf(
                    "success" to false,
                    "message" to "La categoría es requerida"
                ))

            val imagen = productData["imagen"] as? String ?: ""
            val descripcion = productData["descripcion"] as? String ?: ""
            val precio = (productData["precio"] as? Number)?.toInt()
                ?: return ResponseEntity.badRequest().body(mapOf(
                    "success" to false,
                    "message" to "El precio es requerido"
                ))

            val stock = (productData["stock"] as? Number)?.toInt()
                ?: return ResponseEntity.badRequest().body(mapOf(
                    "success" to false,
                    "message" to "El stock es requerido"
                ))

            val newProduct = productService.createProduct(
                nombre = nombre,
                categoria = categoria,
                imagen = imagen,
                descripcion = descripcion,
                precio = precio,
                stock = stock
            )

            ResponseEntity.status(HttpStatus.CREATED).body(mapOf(
                "success" to true,
                "message" to "Producto creado exitosamente",
                "data" to newProduct
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf(
                "success" to false,
                "message" to e.message
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al crear producto: ${e.message}"
            ))
        }
    }

    /**
     * Actualizar un producto (admin)
     * PUT /api/products/{id}
     */
    @PutMapping("/{id}")
    fun updateProduct(
        @PathVariable id: Long,
        @RequestBody productData: Map<String, Any>
    ): ResponseEntity<Any> {
        return try {
            val nombre = productData["nombre"] as? String
            val categoria = productData["categoria"] as? String
            val imagen = productData["imagen"] as? String
            val descripcion = productData["descripcion"] as? String
            val precio = (productData["precio"] as? Number)?.toInt()
            val stock = (productData["stock"] as? Number)?.toInt()

            val updatedProduct = productService.updateProduct(
                productId = id,
                nombre = nombre,
                categoria = categoria,
                imagen = imagen,
                descripcion = descripcion,
                precio = precio,
                stock = stock
            )

            ResponseEntity.ok(mapOf(
                "success" to true,
                "message" to "Producto actualizado exitosamente",
                "data" to updatedProduct
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf(
                "success" to false,
                "message" to e.message
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al actualizar producto: ${e.message}"
            ))
        }
    }

    /**
     * Actualizar solo el stock de un producto
     * PATCH /api/products/{id}/stock
     */
    @PatchMapping("/{id}/stock")
    fun updateProductStock(
        @PathVariable id: Long,
        @RequestBody stockData: Map<String, Int>
    ): ResponseEntity<Any> {
        return try {
            val newStock = stockData["stock"]
                ?: return ResponseEntity.badRequest().body(mapOf(
                    "success" to false,
                    "message" to "El stock es requerido"
                ))

            val updatedProduct = productService.updateProductStock(id, newStock)

            ResponseEntity.ok(mapOf(
                "success" to true,
                "message" to "Stock actualizado exitosamente",
                "data" to updatedProduct
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf(
                "success" to false,
                "message" to e.message
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al actualizar stock: ${e.message}"
            ))
        }
    }

    /**
     * Eliminar un producto (admin)
     * DELETE /api/products/{id}
     */
    @DeleteMapping("/{id}")
    fun deleteProduct(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            productService.deleteProduct(id)
            ResponseEntity.ok(mapOf(
                "success" to true,
                "message" to "Producto eliminado exitosamente"
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf(
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
     * Obtener estadísticas de productos (admin)
     * GET /api/products/stats
     */
    @GetMapping("/stats")
    fun getProductStats(): ResponseEntity<Any> {
        return try {
            val totalProducts = productService.getTotalProducts()
            val productsWithStock = productService.countProductsWithStock()
            val productsOutOfStock = productService.countProductsOutOfStock()

            ResponseEntity.ok(mapOf(
                "success" to true,
                "data" to mapOf(
                    "totalProducts" to totalProducts,
                    "withStock" to productsWithStock,
                    "outOfStock" to productsOutOfStock
                )
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al obtener estadísticas: ${e.message}"
            ))
        }
    }

    /**
     * Crear productos de prueba (solo para desarrollo)
     * POST /api/products/create-test-products
     */
    @PostMapping("/create-test-products")
    fun createTestProducts(): ResponseEntity<Any> {
        return try {
            productService.createTestProducts()
            ResponseEntity.ok(mapOf(
                "success" to true,
                "message" to "Productos de prueba creados exitosamente"
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al crear productos de prueba: ${e.message}"
            ))
        }
    }

    /**
     * Verificar disponibilidad de stock
     * GET /api/products/{id}/check-stock?quantity={quantity}
     */
    @GetMapping("/{id}/check-stock")
    fun checkStock(
        @PathVariable id: Long,
        @RequestParam quantity: Int
    ): ResponseEntity<Any> {
        return try {
            val hasStock = productService.hasStock(id, quantity)
            val product = productService.getProductById(id)

            ResponseEntity.ok(mapOf(
                "success" to true,
                "hasStock" to hasStock,
                "availableStock" to product.stock,
                "requestedQuantity" to quantity
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf(
                "success" to false,
                "message" to e.message
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al verificar stock: ${e.message}"
            ))
        }
    }
    /**
     * Cargar productos desde JSON
     * POST /api/products/load-from-json
     */
    @PostMapping("/load-from-json")
    fun loadProductsFromJson(@RequestBody products: List<Map<String, Any>>): ResponseEntity<Any> {
        return try {
            val loadedProducts = productService.loadProductsFromJson(products)

            ResponseEntity.ok(mapOf(
                "success" to true,
                "message" to "Productos cargados exitosamente",
                "count" to loadedProducts.size
            ))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
                "success" to false,
                "message" to "Error al cargar productos: ${e.message}"
            ))
        }
    }
}
