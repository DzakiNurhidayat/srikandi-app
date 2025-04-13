package org.example.project.model.entities

import kotlinx.serialization.Serializable

@Serializable
data class FileUploadResponse(
    val status: Boolean,
    val message: String,
    val data: FileData
)