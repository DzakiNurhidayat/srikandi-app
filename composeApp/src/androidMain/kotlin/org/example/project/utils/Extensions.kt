package org.example.project.utils

import org.example.project.common.enums.JenisKekerasan

fun JenisKekerasan.toReadableString(): String {
    return when (this) {
        JenisKekerasan.KekerasanFisik -> "Kekerasan Fisik"
        JenisKekerasan.KekerasanPsikis -> "Kekerasan Psikis"
        JenisKekerasan.KekerasanSeksual -> "Kekerasan Seksual"
        JenisKekerasan.Perundungan -> "Perundungan"
        JenisKekerasan.Diskriminasi -> "Diskriminasi"
        JenisKekerasan.Kebijakan -> "Kebijakan"
    }
}
