package org.example.project.application.dtos.requests

import kotlinx.serialization.Serializable

@Serializable
data class EvidenceRequest(
    val reportId : Int,
    val buktiKe : Int,
    val filePath : String,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
