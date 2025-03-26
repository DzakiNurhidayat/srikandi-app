package org.example.project.application.dtos.requests

import kotlinx.serialization.Serializable

@Serializable
data class ProductRequest(
    val name: String,
    val price: Double,
    val description: String,
    val imageUrl: String? = null,
)
