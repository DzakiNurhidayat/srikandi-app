package org.example.project.infastructure.repositories.inmemory

import org.example.project.domain.entities.Products
import org.example.project.infastructure.repositories.interfaces.IProductRepository
import org.example.project.model.Product
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime

class ProductRepository : BaseRepository<Products, Product, Int>(Products, Products.id), IProductRepository {
    override suspend fun create(entity: Product): Product = dbQuery {
        val now = LocalDateTime.now().toString()
        val insertedId = Products.insert {
            it[name] = entity.name
            it[description] = entity.description
            it[price] = entity.price
            it[imageUrl] = entity.imageUrl
            it[createdAt] = now
            it[updatedAt] = now
        }[Products.id]

        entity.copy(id = insertedId, createdAt = now, updatedAt = now)
    }


    override suspend fun update(id: Int, entity: Product): Boolean = dbQuery {
        Products.update({ Products.id eq id }) {
            it[name] = entity.name
            it[description] = entity.description
            it[price] = entity.price
            it[imageUrl] = entity.imageUrl
            it[updatedAt] = LocalDateTime.now().toString()
        } > 0
    }

    override suspend fun findByName(name: String): Product? = dbQuery {
        Products.selectAll().where { Products.name eq name }
            .mapNotNull { rowToEntity(it) }
            .singleOrNull()
    }

    override fun rowToEntity(row: ResultRow): Product = Product(
        id = row[Products.id],
        name = row[Products.name],
        description = row[Products.description],
        price = row[Products.price],
        imageUrl = row[Products.imageUrl],
        createdAt = row[Products.createdAt],
        updatedAt = row[Products.updatedAt]
    )
}
