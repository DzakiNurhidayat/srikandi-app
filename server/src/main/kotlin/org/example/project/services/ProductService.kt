package org.example.project.services

import org.example.project.model.Product
import org.example.project.repositories.ProductRepository

class ProductService(private val repository: ProductRepository) {
    fun createProduct(product: Product): Product? {
        return repository.addProduct(product)
    }

    fun getAllProducts(): List<Product> {
        return repository.getAllProducts()
    }

    fun getProductById(id: Int): Product? {
        return repository.getProductById(id)
    }

    fun updateProduct(product: Product): Product? {
        return repository.updateProduct(product)
    }

    fun deleteProduct(id: Int): Boolean {
        return repository.deleteProduct(id)
    }
}
