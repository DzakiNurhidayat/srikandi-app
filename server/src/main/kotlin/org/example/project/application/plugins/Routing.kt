package org.example.project.application.plugins

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.project.application.routes.evidenceRoute
import org.example.project.application.routes.firebaseRoute
import org.example.project.application.routes.productRoute
import org.example.project.application.routes.reportRoute

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        staticResources("static", "static")
        productRoute()
        reportRoute()
        evidenceRoute()
        firebaseRoute()
    }
}
