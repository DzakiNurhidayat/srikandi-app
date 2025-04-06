package org.example.project.model.request

import kotlinx.serialization.Serializable

@Serializable
data class EvidenceRequest(
    val reportId : Int,
    val buktiKe : Int,
    val filePath : String,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
