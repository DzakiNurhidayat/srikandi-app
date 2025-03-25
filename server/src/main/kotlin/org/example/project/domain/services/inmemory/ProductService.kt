package org.example.project.domain.services.inmemory

import org.example.project.application.dtos.requests.ProductRequest
import org.example.project.application.dtos.toEntity
import org.example.project.domain.services.interfaces.IProductService
import org.example.project.infastructure.repositories.interfaces.IProductRepository
import org.example.project.model.Product

class ProductService(
    productRepository: IProductRepository
) : BaseService<ProductRequest, Int, Product>(productRepository), IProductService {

    override suspend fun create(request: ProductRequest): Product {
        val newProduct = request.toEntity() ?: throw IllegalArgumentException("Invalid product request")
        return repository.create(newProduct)
    }

    override suspend fun update(id: Int, request: ProductRequest): Product {
        val existingProduct = repository.getById(id) ?: throw NoSuchElementException("Product with ID $id not found")

        val updatedProduct = existingProduct.copy(
            name = request.name ?: existingProduct.name,
            description = request.description ?: existingProduct.description,
            price = request.price ?: existingProduct.price,
            imageUrl = request.imageUrl ?: existingProduct.imageUrl
        )

        return repository.update(id, updatedProduct)
    }

    override suspend fun delete(id: Int): Boolean {
        val existingProduct = repository.getById(id) ?: throw NoSuchElementException("Product with ID $id not found")
        return repository.delete(id)
    }
}
