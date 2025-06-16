package org.example.project.infastructure.repositories.inmemory

import org.example.project.common.enums.StatusLaporan
import org.example.project.domain.entities.Reports
import org.example.project.infastructure.repositories.interfaces.IReportRepository
import org.example.project.model.entities.Report
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update
import java.time.format.DateTimeFormatter
import java.time.LocalDate as JavaLocalDate
import java.time.LocalDateTime as JavaLocalDateTime

class ReportRepository : BaseRepository<Reports, Report, Int>(Reports, Reports.id), IReportRepository {

    override suspend fun create(entity: Report): Report {
        val id = dbQuery {
            Reports.insert {
                it[isKorban] = entity.isKorban
                it[deskripsi] = entity.deskripsi
                it[jenisKekerasan] = entity.jenisKekerasan
                it[tempatKejadian] = entity.tempatKejadian
                it[tanggalKejadian] = entity.tanggalKejadian.format(dateFormatter)
                it[statusLaporan] = entity.statusLaporan
                it[tanggalPemanggilan] = entity.tanggalPemanggilan?.format(dateFormatter) ?: "" // Gunakan null-safe
                it[createdAt] = getCurrentTimeAsString()
                it[updatedAt] = getCurrentTimeAsString()
            }[Reports.id]
        }
        return getById(id) ?: throw IllegalStateException("Gagal mengambil laporan yang baru dibuat dengan id: $id")
    }

    override suspend fun update(id: Int, entity: Report): Report {
        dbQuery {
            Reports.update({ Reports.id eq id }) {
                it[isKorban] = entity.isKorban
                it[deskripsi] = entity.deskripsi
                it[jenisKekerasan] = entity.jenisKekerasan
                it[tempatKejadian] = entity.tempatKejadian
                it[tanggalKejadian] = entity.tanggalKejadian.format(dateFormatter)
                it[statusLaporan] = entity.statusLaporan
                it[tanggalPemanggilan] = entity.tanggalPemanggilan?.format(dateFormatter) ?: "" // Gunakan null-safe
                it[updatedAt] = getCurrentTimeAsString()
            }
        }
        return getById(id) ?: throw IllegalStateException("Gagal mengambil laporan yang baru diperbarui dengan id: $id")
    }

    override suspend fun delete(id: Int): Boolean = dbQuery {
        val updatedRows = Reports.update({ Reports.id eq id }) {
            it[statusLaporan] = StatusLaporan.DELETED
            it[updatedAt] = getCurrentTimestamp()
        }
        updatedRows > 0
    }

    override fun rowToEntity(row: ResultRow): Report {
        return Report(
            id = row[Reports.id],
            isKorban = row[Reports.isKorban],
            deskripsi = row[Reports.deskripsi],
            jenisKekerasan = row[Reports.jenisKekerasan],
            tempatKejadian = row[Reports.tempatKejadian],
            tanggalKejadian = JavaLocalDate.parse(row[Reports.tanggalKejadian], dateFormatter),
            statusLaporan = row[Reports.statusLaporan],
            tanggalPemanggilan = row[Reports.tanggalPemanggilan]?.let { JavaLocalDate.parse(it, dateFormatter) },
            createdAt = JavaLocalDateTime.parse(row[Reports.createdAt], dateTimeFormatter),
            updatedAt = row[Reports.updatedAt]?.let { JavaLocalDateTime.parse(it, dateTimeFormatter) }
        )
    }

    override suspend fun updateStatusLaporan(id: Int, status: StatusLaporan): Boolean = dbQuery {
        Reports.update({ Reports.id eq id }) {
            it[statusLaporan] = status
            it[updatedAt] = getCurrentTimestamp()
        } > 0
    }


}
