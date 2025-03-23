package org.example.project.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.project.model.Product
import org.example.project.services.ProductService
import org.koin.ktor.ext.inject

fun Application.productRoute() {
    val productService: ProductService by inject()
    routing {
        route("/api/products") {
            post {
                val product = call.receive<Product>()
                val createdProduct = productService.createProduct(product)
                if (createdProduct != null) {
                    call.respond(HttpStatusCode.Created, createdProduct)
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Failed to create product")
                }
            }
            get {
                val products = productService.getAllProducts()
                call.respond(products)
            }
            get("/{id}") {
                val idString = call.parameters["id"]
                if (idString == null) {
                    call.respond(HttpStatusCode.BadRequest, "Missing ID parameter")
                    return@get
                }
                
                try {
                    val id = idString.toInt()
                    val product = productService.getProductById(id)
                    if (product == null) {
                        call.respond(HttpStatusCode.NotFound, "Product not found")
                    } else {
                        call.respond(HttpStatusCode.OK, product)
                    }
                } catch (e: NumberFormatException) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID format: must be an integer")
                }
            }

            put("{id}") {
                val idString = call.parameters["id"]
                if (idString == null) {
                    call.respond(HttpStatusCode.BadRequest, "Missing ID parameter")
                    return@put
                }

                try {
                    val id = idString.toInt()
                    val product = productService.getProductById(id)
                    if (product == null) {
                        call.respond(HttpStatusCode.NotFound, "Product not found")
                        return@put
                    }

                    val updatedProduct = call.receive<Product>()
                    val newProduct = product.copy(
                        name = updatedProduct.name,
                        description = updatedProduct.description,
                        price = updatedProduct.price,
                        imageUrl = updatedProduct.imageUrl
                    )
                    val result = productService.updateProduct(newProduct)
                    if (result != null) {
                        call.respond(HttpStatusCode.OK, result)
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Failed to update product")
                    }
                } catch (e: NumberFormatException) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID format: must be an integer")
                }
            }
            delete("{id}") {
                val idString = call.parameters["id"]
                if (idString == null) {
                    call.respond(HttpStatusCode.BadRequest, "Missing ID parameter")
                    return@delete
                }

                try {
                    val id = idString.toInt()
                    val product = productService.getProductById(id)
                    if (product == null) {
                        call.respond(HttpStatusCode.NotFound, "Product not found")
                        return@delete
                    }

                    val result = productService.deleteProduct(id)
                    if (result) {
                        call.respond(HttpStatusCode.OK, "Product deleted successfully")
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Failed to delete product")
                    }
                } catch (e: NumberFormatException) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID format: must be an integer")
                }
            }
        }
    }
}
