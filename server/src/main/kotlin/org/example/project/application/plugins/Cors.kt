package org.example.project.application.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

fun Application.configureCORS() {
    install(CORS) {
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Get)
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }
}