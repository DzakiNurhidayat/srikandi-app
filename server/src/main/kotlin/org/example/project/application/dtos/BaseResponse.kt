package org.example.project.application.dtos

import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse<T>(
    val status: Boolean,
    val message: String,
    val data: T? = null
)

fun <T> successResponse(data: T, message: String): BaseResponse<T> {
    return BaseResponse(true, message, data)
}

fun errorResponse(message: String): BaseResponse<Nothing> {
    return BaseResponse(false, message)
}
