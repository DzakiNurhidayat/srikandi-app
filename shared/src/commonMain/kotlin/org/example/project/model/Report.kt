package org.example.project.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime


@Serializable
data class Report(
    var id: Int? = null,
    val isKorban: Boolean,
    val deskripsi: String,
    val jenisKekerasan: String,
    val tempatKejadian: String,
    @Contextual
    val tanggalKejadian: LocalDate,
    val statusLaporan: String,
    val bukti: List<String> = emptyList(),
    @Contextual
    val createdAt: LocalDateTime? = null,
    @Contextual
    val updatedAt: LocalDateTime? = null
)
