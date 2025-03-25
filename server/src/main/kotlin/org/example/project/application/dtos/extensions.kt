package org.example.project.application.dtos

import org.example.project.application.dtos.requests.ProductRequest
import org.example.project.model.Product

fun ProductRequest.toEntity(): Product? {
    return if (name != null && price != null && description != null) {
        Product(
            id = 0,
            name = name,
            description = description,
            price = price,
            imageUrl = imageUrl,
        )
    } else {
        null
    }
}