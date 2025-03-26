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
        if (request.name.isBlank() || request.description.isBlank() || request.price <= 0) {
            throw IllegalArgumentException("Input data tidak valid: perlu nama, deskripsi, dan harga")
        }
        val newProduct = request.toEntity()
        return repository.create(newProduct)
    }

    override suspend fun update(id: Int, request: ProductRequest): Product {
        if (request.name.isBlank() || request.description.isBlank() || request.price <= 0) {
            throw IllegalArgumentException("Input data tidak valid: perlu nama, deskripsi, dan harga")
        }
        val existingProduct = getById(id)!!
        val updatedProduct = existingProduct.copy(
            name = request.name,
            description = request.description,
            price = request.price,
            imageUrl = request.imageUrl ?: existingProduct.imageUrl
        )
        return repository.update(id, updatedProduct)
    }
}