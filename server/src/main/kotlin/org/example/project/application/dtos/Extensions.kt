package org.example.project.application.dtos

import org.example.project.application.dtos.requests.ProductRequest
import org.example.project.model.Product

fun ProductRequest.toEntity(): Product {
    return Product(
        id = 0,
        name = this.name,
        description = this.description,
        price = this.price,
        imageUrl = this.imageUrl
    )
}
