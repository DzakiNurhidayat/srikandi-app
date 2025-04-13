package org.example.project.domain.services.interfaces

import org.example.project.model.entities.Product
import org.example.project.model.request.ProductRequest

interface IProductService : IEntityService<ProductRequest, Int, Product> {
    suspend fun getByName(name: String): Product?
}