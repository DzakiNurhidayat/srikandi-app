package org.example.project.model

import kotlinx.serialization.Serializable

@Serializable
data class Response<T>(
    val status: Boolean,
    val message: String? = null,
    val data: T? = null
)
