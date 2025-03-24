package org.example.project.domain.services.interfaces

import org.example.project.model.Product

interface IProductService: IEntityService<Product, Int> {
    suspend fun updateProduct(product: Product): Boolean
}