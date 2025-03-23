package org.example.project.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.project.model.Product
import org.example.project.routes.productRoute
import org.example.project.services.ProductService
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val productService: ProductService by inject()
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        staticResources("static", "static")

        get("/tasks") {
            call.respond(
                listOf(
                    Product(1, "babi", "babi", 1.0, "https://www.google.com"),
                    Product(1, "babi", "babi", 1.0, "https://www.google.com"),
                    Product(1, "babi", "babi", 1.0, "https://www.google.com"),
                    Product(1, "babi", "babi", 1.0, "https://www.google.com")
                )
            )
        }
        productRoute()
    }
}
