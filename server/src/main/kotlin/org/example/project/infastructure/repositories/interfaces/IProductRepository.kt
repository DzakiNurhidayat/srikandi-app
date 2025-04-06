package org.example.project.infastructure.repositories.interfaces

import org.example.project.model.entities.Product

interface IProductRepository : IEntityRepository<Product, Int> {
    suspend fun getByName(name: String): Product?
}
