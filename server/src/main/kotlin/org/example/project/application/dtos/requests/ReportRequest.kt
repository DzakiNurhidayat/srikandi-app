package org.example.project.application.dtos.requests

import kotlinx.serialization.Serializable
import org.example.project.common.enums.JenisKekerasan
import org.example.project.common.enums.StatusLaporan

@Serializable
data class ReportRequest(
    val isKorban: Boolean,
    val deskripsi: String,
    val jenisKekerasan: JenisKekerasan,
    val tempatKejadian: String,
    val tanggalKejadian : String,
    val statusLaporan: StatusLaporan,
    val bukti: List<String> = emptyList(),
    val createdAt: String? = null,
    val updatedAt: String? = null
)
