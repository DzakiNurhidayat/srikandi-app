package org.example.project.model.entities

import kotlinx.serialization.Serializable
import org.example.project.common.enums.StatusTerlapor
import org.example.project.common.serialization.LocalDateTimeSerializer
import java.time.LocalDateTime

@Serializable
data class FormSatu(
    var form1id: Int? = null,
    val reportId: Int,
    val ciriFisik: String,
    val domisili: String,
    val ceritaSingkat: String,
    val memilikiDisabilitas: Boolean,
    val statusTerlapor: StatusTerlapor,
    val jenisKelaminTerlapor: Boolean,
    val alasanPengaduan: String,
    val kontakLain: String,
    val kebutuhanKorban: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime? = null,
    @Serializable(with = LocalDateTimeSerializer::class)
    val updatedAt: LocalDateTime? = null
)