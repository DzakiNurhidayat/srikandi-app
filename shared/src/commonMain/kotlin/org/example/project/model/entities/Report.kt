package org.example.project.model.entities

import kotlinx.serialization.Serializable
import org.example.project.common.enums.JenisKekerasan
import org.example.project.common.enums.StatusLaporan
import org.example.project.common.serialization.LocalDateSerializer
import org.example.project.common.serialization.LocalDateTimeSerializer
import java.time.LocalDate
import java.time.LocalDateTime

@Serializable
data class Report(
    var id: Int? = null,
    val isKorban: Boolean,
    val deskripsi: String,
    val jenisKekerasan: JenisKekerasan,
    val tempatKejadian: String,

    @Serializable(with = LocalDateSerializer::class)
    val tanggalKejadian: LocalDate,
    val statusLaporan: StatusLaporan,
    val bukti: List<String> = emptyList(),

    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime? = null,

    @Serializable(with = LocalDateTimeSerializer::class)
    val updatedAt: LocalDateTime? = null
)