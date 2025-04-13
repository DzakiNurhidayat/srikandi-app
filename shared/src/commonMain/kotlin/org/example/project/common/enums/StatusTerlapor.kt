package org.example.project.common.enums

import kotlinx.serialization.Serializable

@Serializable
enum class StatusTerlapor {
    MAHASISWA,
    PENDIDIK,
    TENAGA_KEPENDIDIKAN,
    WARGA_KAMPUS,
    MASYARAKAT_UMUM
}
