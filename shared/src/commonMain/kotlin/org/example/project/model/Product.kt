package org.example.project.model

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    var id: Int? = null,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String?,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
