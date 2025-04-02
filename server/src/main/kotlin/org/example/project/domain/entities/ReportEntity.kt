package org.example.project.domain.entities

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.datetime

object Reports : Table("Reports") {
    val id = integer("id").autoIncrement()
    val isKorban = bool("is_korban")
    val deskripsi = text("deskripsi")
    val jenisKekerasan = varchar("jenis_kekerasan", 255)
    val tempatKejadian = varchar("tempat_kejadian", 255)
    val tanggalKejadian = varchar("tanggal_kejadian", 10)
    val statusLaporan = varchar("status_laporan", 255)
    val bukti = text("bukti").nullable()
    val createdAt = varchar("created_at", 25)
    val updatedAt = varchar("updated_at", 25)

    override val primaryKey = PrimaryKey(id)
}