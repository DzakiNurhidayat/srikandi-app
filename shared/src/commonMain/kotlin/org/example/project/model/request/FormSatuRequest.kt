package org.example.project.model.request

import org.example.project.common.enums.StatusTerlapor

data class FormSatuRequest(
    val reportId: Int?,
    val ciriFisik: String,
    val domisili: String,
    val ceritaSingkat: String,
    val memilikiDisabilitas: Boolean,
    val statusTerlapor: StatusTerlapor,
    val jenisKelaminTerlapor: Boolean,
    val alasanPengaduan: String,
    val kontakLain: String,
    val kebutuhanKorban: String
)
