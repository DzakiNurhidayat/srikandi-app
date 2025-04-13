package org.example.project.domain.entities

import org.jetbrains.exposed.sql.Table

object FormSatuTable : Table("form_satu") {
    val form1id = integer("form1id").autoIncrement()
    val reportId = integer("report_id")
    val ciriFisik = varchar("ciri_fisik", 255)
    val domisili = varchar("domisili", 255)
    val ceritaSingkat = text("cerita_singkat")
    val memilikiDisabilitas = bool("memiliki_disabilitas")
    val statusTerlapor = varchar("status_terlapor", 50)
    val jenisKelaminTerlapor = bool("jenis_kelamin_terlapor")
    val alasanPengaduan = text("alasan_pengaduan")
    val kontakLain = varchar("kontak_lain", 255)
    val kebutuhanKorban = text("kebutuhan_korban")
    val createdAt = varchar("created_at", 50)
    val updatedAt = varchar("updated_at", 50).nullable()

    override val primaryKey = PrimaryKey(form1id, reportId)
}
