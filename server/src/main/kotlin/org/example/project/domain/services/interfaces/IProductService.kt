package org.example.project.domain.services.interfaces

import org.example.project.application.dtos.requests.ProductRequest
import org.example.project.model.Product

interface IProductService : IEntityService<ProductRequest, Int, Product> {
    suspend fun getByName(name: String): Product?
}