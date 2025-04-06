package org.example.project.model.request

import kotlinx.serialization.Serializable
import org.example.project.common.enums.StatusLaporan

@Serializable
data class StatusLaporanRequest(
    val statusLaporan: StatusLaporan
)
