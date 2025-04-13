package org.example.project.infastructure.repositories.inmemory

import org.example.project.domain.entities.Products
import org.example.project.infastructure.repositories.interfaces.IProductRepository
import org.example.project.model.entities.Product
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

class ProductRepository : BaseRepository<Products, Product, Int>(Products, Products.id), IProductRepository {

    override suspend fun create(entity: Product): Product {
        val id = dbQuery {
            Products.insert {
                it[name] = entity.name
                it[description] = entity.description
                it[price] = entity.price
                it[imageUrl] = entity.imageUrl
                it[createdAt] = getCurrentTimestamp()
                it[updatedAt] = getCurrentTimestamp()
            }[Products.id]
        }
        return getById(id) ?: throw IllegalStateException("Gagal mengambil produk yang baru dibuat dengan id: $id")
    }

    override suspend fun update(id: Int, entity: Product): Product {
        dbQuery {
            Products.update({ Products.id eq id }) {
                it[name] = entity.name
                it[description] = entity.description
                it[price] = entity.price
                it[imageUrl] = entity.imageUrl
                it[updatedAt] = getCurrentTimestamp()
            }
        }
        return getById(id) ?: throw IllegalStateException("Gagal mengambil produk yang baru dibuat dengan id: $id")
    }

    override suspend fun getByName(name: String): Product? = dbQuery {
        Products.selectAll().where { Products.name eq name }
            .mapNotNull { rowToEntity(it) }
            .singleOrNull()
    }

    override fun rowToEntity(row: ResultRow): Product {
        return Product(
            id = row[Products.id],
            name = row[Products.name],
            description = row[Products.description],
            price = row[Products.price],
            imageUrl = row[Products.imageUrl],
            createdAt = row[Products.createdAt],
            updatedAt = row[Products.updatedAt]
        )
    }
}