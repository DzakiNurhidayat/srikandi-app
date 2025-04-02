package org.example.project.application.dtos.requests

import kotlinx.serialization.Serializable

@Serializable
data class ReportRequest(
    val isKorban: Boolean,
    val deskripsi: String,
    val jenisKekerasan: String,
    val tempatKejadian: String,
    val tanggalKejadian : String,
    val statusLaporan: String,
    val bukti: List<String> = emptyList(),
    val createdAt: String? = null,
    val updatedAt: String? = null
)
