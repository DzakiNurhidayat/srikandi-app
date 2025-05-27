package org.example.project.application.dtos

import org.example.project.model.Response

fun <T> successResponse(data: T?, message: String): Response<T?> {
    return Response(true, message, data)
}

fun errorResponse(message: String? = null): Response<Unit> {
    val errorMessage = message ?: "Terjadi kesalahan pada server."
    return Response(false, errorMessage)
}
