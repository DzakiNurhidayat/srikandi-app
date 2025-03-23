package org.example.project.repositories

import org.example.project.data.entity.ProductTable
import org.example.project.model.Product
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ProductRepository {
    private val logger = LoggerFactory.getLogger(ProductRepository::class.java)
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    private fun getCurrentTimestamp(): String {
        return LocalDateTime.now().format(formatter)
    }

    fun addProduct(product: Product): Product? {
        return try {
            transaction {
                val now = getCurrentTimestamp()
                val id = ProductTable.insert {
                    it[name] = product.name
                    it[description] = product.description
                    it[price] = product.price
                    it[imageUrl] = product.imageUrl
                    it[createdAt] = now
                    it[updatedAt] = now
                } get ProductTable.id

                logger.info("Product added successfully with ID: $id")
                product.copy(id = id.value, createdAt = now, updatedAt = now)
            }
        } catch (e: Exception) {
            logger.error("Failed to add product", e)
            null
        }
    }

    fun getAllProducts(): List<Product> {
        return transaction {
            ProductTable.selectAll().map {
                Product(
                    id = it[ProductTable.id].value,
                    name = it[ProductTable.name],
                    description = it[ProductTable.description],
                    price = it[ProductTable.price],
                    imageUrl = it[ProductTable.imageUrl],
                    createdAt = it[ProductTable.createdAt],
                    updatedAt = it[ProductTable.updatedAt]
                )
            }
        }
    }

    fun getProductById(id: Int): Product? {
        return transaction {
            val result = ProductTable
                .selectAll()
                .where { ProductTable.id eq id }
                .singleOrNull()

            if (result == null) {
                return@transaction null
            }
            Product(
                id = result[ProductTable.id].value,
                name = result[ProductTable.name],
                description = result[ProductTable.description],
                price = result[ProductTable.price],
                imageUrl = result[ProductTable.imageUrl],
                createdAt = result[ProductTable.createdAt],
                updatedAt = result[ProductTable.updatedAt]
            )
        }
    }

    fun updateProduct(product: Product): Product? {
        return try {
            transaction {
                val now = getCurrentTimestamp()
                ProductTable.update({ ProductTable.id eq product.id }) {
                    it[name] = product.name
                    it[description] = product.description
                    it[price] = product.price
                    it[imageUrl] = product.imageUrl
                    it[updatedAt] = now
                }
                logger.info("Product updated successfully with ID: ${product.id}")
                product.copy(updatedAt = now)
            }
        } catch (e: Exception) {
            logger.error("Failed to update product", e)
            null
        }
    }

    fun deleteProduct(id: Int): Boolean {
        return try {
            transaction {
                ProductTable.deleteWhere { ProductTable.id eq id }
            }
            true
        } catch (e: Exception) {
            logger.error("Failed to delete product", e)
            false
        }
    }
}
