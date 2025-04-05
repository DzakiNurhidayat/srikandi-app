package org.example.project.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import org.example.project.common.serialization.*
import org.example.project.common.enums.*

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
