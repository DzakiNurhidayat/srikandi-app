package org.example.project.application.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.project.application.dtos.BaseResponse
import org.example.project.application.dtos.errorResponse
import org.example.project.application.routes.productRoute

fun Application.configureRouting() {
    install(StatusPages) {
        exception<IllegalArgumentException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                BaseResponse(false, cause.message ?: "Input data tidak valid", null)
            )
        }
        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                BaseResponse(false, cause.message ?: "Terjadi kesalahan pada server", null)
            )
        }
        exception<NotFoundException> { call, cause ->
            call.respond(HttpStatusCode.NotFound, errorResponse(cause.message ?: "Data tidak ditemukan"))
        }
    }
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        staticResources("static", "static")
        productRoute()
    }
}
