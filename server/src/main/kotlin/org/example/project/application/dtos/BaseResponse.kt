package org.example.project.application.dtos

import org.example.project.model.Response

fun <T> successResponse(data: T, message: String): Response<T> {
    return Response(true, message, data)
}

fun errorResponse(message: String): Response<Nothing> {
    return Response(false, message)
}
