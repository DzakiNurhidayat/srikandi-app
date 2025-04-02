package org.example.project.model

import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Contextual

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
