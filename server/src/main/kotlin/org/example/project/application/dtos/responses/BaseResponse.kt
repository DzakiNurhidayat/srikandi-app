package org.example.project.application.dtos.responses

import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse<T>(
    val status: Boolean,
    val message: String,
    val data: T? = null
)
