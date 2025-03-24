package org.example.project.application.dtos

import kotlinx.serialization.Serializable
import org.example.project.model.Product

@Serializable
data class Data(
    val id: Int,
    val createdAt: String,
    val updatedAt: String,
)

@Serializable
data class ProductResponse(
    val status: String,
    val message: String,
    val data: Data
)

fun Product.toData(): Data = Data(
    id = this.id!!,
    createdAt = this.createdAt!!,
    updatedAt = this.updatedAt!!
)


