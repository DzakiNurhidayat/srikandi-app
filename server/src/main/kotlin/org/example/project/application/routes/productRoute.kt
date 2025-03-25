package org.example.project.application.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.project.application.dtos.requests.ProductRequest
import org.example.project.application.dtos.responses.BaseResponse
import org.example.project.domain.services.inmemory.ProductService
import org.koin.ktor.ext.inject

fun Application.productRoute() {
    val productService: ProductService by inject()

    routing {
        route("/api/products") {
            post {
                try {
                    val productRequest = call.receive<ProductRequest>()
                    val response = productService.create(productRequest)
                    call.respond(HttpStatusCode.Created, BaseResponse(true, "Product created", response))
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        BaseResponse(false, "Failed to create product", null)
                    )
                }
            }

            get {
                try {
                    val products = productService.getAll()
                    call.respond(HttpStatusCode.OK, BaseResponse(true, "Get all products", products))
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        BaseResponse(false, "Failed to get products", null)
                    )
                }
            }
            get("/{id}") {
                try {
                    println("tes")
                    val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID format")
                    println(id)
                    val response = productService.getById(id) ?: throw NoSuchElementException("Product not found")
                    call.respond(HttpStatusCode.Found, BaseResponse(true, "Get a product", response))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, BaseResponse(false, "Failed to get product", null))
                }
            }

            put("/{id}") {
                try {
                    val id = call.parameters["id"]?.toIntOrNull()
                        ?: throw IllegalArgumentException("Invalid ID format")
                    val productRequest = call.receive<ProductRequest>()
                    val response = productService.update(id, productRequest)
                    call.respond(HttpStatusCode.OK, BaseResponse(true, "Product updated successfully", response))
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, BaseResponse(false, e.message ?: "Invalid request", null))
                } catch (e: NoSuchElementException) {
                    call.respond(HttpStatusCode.NotFound, BaseResponse(false, e.message ?: "Invalid request", null))
                } catch (e: ContentTransformationException) {
                    call.respond(HttpStatusCode.BadRequest, BaseResponse(false, "Invalid request body format", null))
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        BaseResponse(false, "Failed to update product", null)
                    )
                }
            }


            delete("/{id}") {
                try {
                    val id = call.parameters["id"]?.toIntOrNull()
                        ?: throw IllegalArgumentException("Invalid ID format")

                    val isDeleted = productService.delete(id)

                    if (isDeleted) {
                        call.respond(HttpStatusCode.OK, BaseResponse(true, "Product deleted successfully", null))
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound,
                            BaseResponse(false, "Product with ID $id not found", null)
                        )
                    }

                } catch (e: NoSuchElementException) {
                    call.respond(HttpStatusCode.BadRequest, BaseResponse(false, e.message ?: "Invalid request", null))
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        BaseResponse(false, "Failed to delete product", null)
                    )
                }
            }
        }
    }
}
