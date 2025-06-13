package org.example.project.application.routes

import io.ktor.server.application.*

fun ApplicationCall.idParam(name: String = "id"): Int {
    return this.parameters[name]?.toIntOrNull()
        ?: throw IllegalArgumentException("Parameter '$name' tidak valid atau tidak ditemukan")
}