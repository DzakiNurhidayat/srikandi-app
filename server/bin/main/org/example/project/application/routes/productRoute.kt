package org.example.project.application.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.project.application.dtos.successResponse
import org.example.project.domain.services.inmemory.ProductService
import org.example.project.model.request.ProductRequest
import org.koin.ktor.ext.inject

fun Application.productRoute() {
    val productService: ProductService by inject()

    routing {
        route("/api/products") {
            post {
                val productRequest = call.receive<ProductRequest>()
                val response = productService.create(productRequest)
                call.respond(HttpStatusCode.Created, successResponse(response, "Produk berhasil dibuat"))
            }
            get {
                val response = productService.getAll()
                call.respond(HttpStatusCode.OK, successResponse(response, "Berhasil mengambil semua produk"))
            }
            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: throw IllegalArgumentException("ID tidak valid")
                val product = productService.getById(id)
                call.respond(HttpStatusCode.OK, successResponse(product, "Berhasil mengambil produk dengan ID $id"))
            }
            put("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: throw IllegalArgumentException("ID tidak valid")
                val productRequest = call.receive<ProductRequest>()
                val response = productService.update(id, productRequest)
                call.respond(HttpStatusCode.OK, successResponse(response, "Produk berhasil diupdate"))
            }

            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: throw IllegalArgumentException("ID tidak valid")
                productService.delete(id)
                call.respond(HttpStatusCode.OK, successResponse(null, "Berhasil menghapus produk dengan ID $id"))
            }
        }
    }
}
