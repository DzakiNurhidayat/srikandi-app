package org.example.project.model.entities

import kotlinx.serialization.Serializable

@Serializable
data class FileData(
    val filePath: String
)
