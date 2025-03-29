package org.example.project.domain.services.inmemory

import io.ktor.server.plugins.*
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
        if (!repository.findById(id)) {
            throw NotFoundException("Product dengan id $id tidak ditemukan")
        }
        val updatedProduct = request.toEntity()
        return repository.update(id, updatedProduct)
    }

    override suspend fun getByName(name: String): Product? {
        TODO("Not yet implemented")
    }
}