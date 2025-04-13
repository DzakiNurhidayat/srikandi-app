package org.example.project.infastructure.repositories.inmemory

import org.example.project.domain.entities.FormSatuTable
import org.example.project.infastructure.repositories.interfaces.IFormSatuRepository
import org.example.project.model.entities.FormSatu
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FormSatuRepository : BasePairRepository<FormSatuTable, FormSatu, Pair<Int, Int>>(FormSatuTable, Pair(FormSatuTable.form1id, FormSatuTable.reportId)), IFormSatuRepository {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override suspend fun create(entity: FormSatu): FormSatu {
        val id = dbQuery {
            FormSatuTable.insert {
                it[reportId] = entity.reportId
                it[ciriFisik] = entity.ciriFisik
                it[domisili] = entity.domisili
                it[ceritaSingkat] = entity.ceritaSingkat
                it[memilikiDisabilitas] = entity.memilikiDisabilitas
                it[statusTerlapor] = entity.statusTerlapor.name
                it[jenisKelaminTerlapor] = entity.jenisKelaminTerlapor
                it[alasanPengaduan] = entity.alasanPengaduan
                it[kontakLain] = entity.kontakLain
                it[kebutuhanKorban] = entity.kebutuhanKorban
                it[createdAt] = getCurrentTimeAsString()
                it[updatedAt] = getCurrentTimeAsString()
            }[FormSatuTable.form1id]
        }
        return getById(Pair(id, entity.reportId)) ?: throw IllegalStateException("Gagal mengambil FormSatu yang baru dibuat.")
    }

    override suspend fun update(id: Pair<Int, Int>, entity: FormSatu): FormSatu {
        dbQuery {
            FormSatuTable.update({ (FormSatuTable.form1id eq id.first) and (FormSatuTable.reportId eq id.second) }) {
                it[ciriFisik] = entity.ciriFisik
                it[domisili] = entity.domisili
                it[ceritaSingkat] = entity.ceritaSingkat
                it[memilikiDisabilitas] = entity.memilikiDisabilitas
                it[statusTerlapor] = entity.statusTerlapor.name
                it[jenisKelaminTerlapor] = entity.jenisKelaminTerlapor
                it[alasanPengaduan] = entity.alasanPengaduan
                it[kontakLain] = entity.kontakLain
                it[kebutuhanKorban] = entity.kebutuhanKorban
                it[updatedAt] = getCurrentTimeAsString()
            }
        }
        return getById(id) ?: throw IllegalStateException("Gagal memperbarui FormSatu.")
    }

    override suspend fun delete(id: Pair<Int, Int>): Boolean {
        // Bisa ganti dengan soft delete kalau kamu menambahkan field status
        TODO("Soft delete belum diimplementasikan")
    }

    override fun rowToEntity(row: ResultRow): FormSatu {
        return FormSatu(
            id = row[FormSatuTable.form1id],
            reportId = row[FormSatuTable.reportId],
            ciriFisik = row[FormSatuTable.ciriFisik],
            domisili = row[FormSatuTable.domisili],
            ceritaSingkat = row[FormSatuTable.ceritaSingkat],
            memilikiDisabilitas = row[FormSatuTable.memilikiDisabilitas],
            statusTerlapor = enumValueOf(row[FormSatuTable.statusTerlapor]),
            jenisKelaminTerlapor = row[FormSatuTable.jenisKelaminTerlapor],
            alasanPengaduan = row[FormSatuTable.alasanPengaduan],
            kontakLain = row[FormSatuTable.kontakLain],
            kebutuhanKorban = row[FormSatuTable.kebutuhanKorban],
            createdAt = LocalDateTime.parse(row[FormSatuTable.createdAt], formatter),
            updatedAt = row[FormSatuTable.updatedAt]?.let { LocalDateTime.parse(it, formatter) }
        )
    }
}
