package org.example.project.domain.services.inmemory

import org.example.project.domain.services.interfaces.IProductService
import org.example.project.infastructure.repositories.interfaces.IProductRepository
import org.example.project.model.Product
import org.slf4j.LoggerFactory

class ProductService(private val productRepository: IProductRepository) : BaseService<Product, Int>(productRepository),
    IProductService {

    override suspend fun updateProduct(product: Product): Boolean {
        val existingProduct = productRepository.getById(product.id!!) ?: return false
        val updatedProduct = existingProduct.copy(
            name = product.name,
            description = product.description,
            price = product.price,
            imageUrl = product.imageUrl ?: existingProduct.imageUrl
        )
        return productRepository.update(updatedProduct.id!!, updatedProduct)
    }
}