package org.example.project.model.request

data class TokenRequest<T>(
    val fcmToken: String,
    val data: T? = null
)
