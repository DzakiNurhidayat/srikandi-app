package org.example.project.model.request

import kotlinx.serialization.Serializable

@Serializable
data class FcmTokenRequest(
    val token: String
)
