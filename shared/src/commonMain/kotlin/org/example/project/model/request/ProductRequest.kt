package org.example.project.model.request

import kotlinx.serialization.Serializable

@Serializable
data class ProductRequest(
    val name: String,
    val price: Double,
    val description: String,
    val imageUrl: String? = null,
)
