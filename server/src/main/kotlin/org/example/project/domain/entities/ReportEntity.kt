package org.example.project.domain.entities

import org.example.project.common.enums.JenisKekerasan
import org.example.project.common.enums.StatusLaporan
import org.jetbrains.exposed.sql.Table

object Reports : Table("Reports") {
    val id = integer("id").autoIncrement()
    val isKorban = bool("is_korban")
    val deskripsi = text("deskripsi")
    val jenisKekerasan = enumerationByName("jenis_kekerasan", 50, JenisKekerasan::class)
    val tempatKejadian = varchar("tempat_kejadian", 255)
    val tanggalKejadian = varchar("tanggal_kejadian", 10)
    val statusLaporan = enumerationByName("status_laporan", 50, StatusLaporan::class)
    val tanggalPemanggilan = varchar("tanggal_pemanggilan", 10).nullable()
    val createdAt = varchar("created_at", 25)
    val updatedAt = varchar("updated_at", 25)

    override val primaryKey = PrimaryKey(id)
}