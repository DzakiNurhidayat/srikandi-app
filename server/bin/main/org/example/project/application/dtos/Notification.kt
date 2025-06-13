package org.example.project.application.dtos

import kotlinx.serialization.Serializable

@Serializable
data class NotificationRequest(
    val token: String,
    val title: String,
    val body: String
)

@Serializable
data class NotificationTopicRequest(
    val topic: String,
    val title: String,
    val body: String
)

@Serializable
data class NotificationResponse(
    val messageId: String
)

@Serializable
data class ErrorResponse(
    val error: String
)