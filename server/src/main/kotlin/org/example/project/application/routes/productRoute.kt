package org.example.project.application.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.project.application.dtos.ProductResponse
import org.example.project.application.dtos.toData
import org.example.project.domain.services.inmemory.ProductService
import org.example.project.model.Product
import org.koin.ktor.ext.inject
import org.slf4j.LoggerFactory

fun Application.productRoute() {
    val productService: ProductService by inject()
    routing {
        route("/api/products") {
            post {
                try {
                    val product = call.receive<Product>()
                    val createdProduct = productService.create(product)
                    val data = createdProduct.toData()
                    call.respond(HttpStatusCode.Created, ProductResponse("Success", "Product created successfully", data))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid request: ${e.localizedMessage}")
                }
            }
            get {
                val products = productService.getAll()
                if (products.isEmpty()) {
                    call.respond(HttpStatusCode.NoContent)
                }
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
                    val product = productService.getById(id)
                    if (product == null) {
                        call.respond(HttpStatusCode.NotFound, "Product not found")
                    } else {
                        call.respond(HttpStatusCode.OK, product)
                    }
                } catch (e: NumberFormatException) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID format: must be an integer")
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid request: ${e.localizedMessage}")
                }
            }

            put("/{id}") {
                val idString = call.parameters["id"]
                if (idString == null) {
                    call.respond(HttpStatusCode.BadRequest, "Missing ID parameter")
                    return@put
                }
                try {
                    val id = idString.toInt()
                    val logger = LoggerFactory.getLogger("ProductRoute")
                    val updateProduct = call.receive<Product>()
                    updateProduct.id = id
                    logger.info(updateProduct.toString())
                    val updated = productService.updateProduct(updateProduct)
                    if (updated) {
                        call.respond(HttpStatusCode.OK, "Product updated successfully")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Product not found")
                    }
                } catch (e: NumberFormatException) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid ID format: must be an integer")
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid request: ${e.localizedMessage}")
                }
            }

            delete("/{id}") {
                val idString = call.parameters["id"]
                if (idString == null) {
                    call.respond(HttpStatusCode.BadRequest, "Missing ID parameter")
                    return@delete
                }

                try {
                    val id = idString.toInt()
                    val product = productService.getById(id)
                    if (product == null) {
                        call.respond(HttpStatusCode.NotFound, "Product not found")
                        return@delete
                    }

                    val result = productService.delete(id)
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
