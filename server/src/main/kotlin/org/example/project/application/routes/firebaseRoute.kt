package org.example.project.application.routes


import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.project.firebase.FirebaseService
import org.koin.ktor.ext.inject

fun Application.firebaseRoute() {
    val firebaseService: FirebaseService by inject()
    routing {
        route("/firebase") {
//            get("/send-notification") {
//                try {
//                    val userId = "user123"
//                    val token = firebaseService.getToken(userId)
//                    sendFcmNotification(token, "Hello", "Test from server")
//                    call.respondText("Notification sent!")
//                } catch (e: Exception) {
//                    call.respondText("Error: ${e.message}", status = HttpStatusCode.InternalServerError)
//                }
//            }
            post("/register-token") {
                val params = call.receiveParameters()
                val userId = params["userId"] ?: return@post call.respondText(
                    "userId required",
                    status = HttpStatusCode.BadRequest
                )
                val token = params["token"] ?: return@post call.respondText(
                    "token required",
                    status = HttpStatusCode.BadRequest
                )
                try {
                    firebaseService.saveToken(userId, token)
                    call.respondText("Token registered")
                } catch (e: Exception) {
                    call.respondText(
                        "Failed to register token: ${e.message}",
                        status = HttpStatusCode.InternalServerError
                    )
                }
            }
        }
    }
}


