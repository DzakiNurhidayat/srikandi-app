package org.example.project.model.request

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
    val tanggalPemanggilan: String? = null,
    val statusLaporan: StatusLaporan,
    val bukti: List<String> = emptyList(),
    val createdAt: String? = null,
    val updatedAt: String? = null
)
