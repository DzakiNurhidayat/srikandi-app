package org.example.project.model.entities

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class Evidence(
    val reportId: Int,
    val buktiKe: Int,
    val filePath: String?,
    @Contextual
    val createdAt: java.time.LocalDateTime? = null,
    @Contextual
    val updatedAt: java.time.LocalDateTime? = null
)