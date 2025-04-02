package org.example.project.infastructure.repositories.inmemory

import org.example.project.domain.entities.Reports
import org.example.project.infastructure.repositories.interfaces.IReportRepository
import org.example.project.model.Report
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.example.project.domain.entities.Products
import org.example.project.model.Product
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.format.DateTimeFormatter
import java.time.LocalDate as JavaLocalDate
import java.time.LocalDateTime as JavaLocalDateTime

class ReportRepository : BaseRepository<Reports, Report, Int>(Reports, Reports.id), IReportRepository {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override suspend fun create(entity: Report): Report {
        val id = dbQuery {
            Reports.insert {
                it[isKorban] = entity.isKorban
                it[deskripsi] = entity.deskripsi
                it[jenisKekerasan] = entity.jenisKekerasan
                it[tempatKejadian] = entity.tempatKejadian
                it[tanggalKejadian] = entity.tanggalKejadian.format(dateFormatter)
                it[statusLaporan] = entity.statusLaporan
                it[bukti] = null // Bukti ditambahkan di Evidence
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
                it[updatedAt] = getCurrentTimeAsString()
            }
        }
        return getById(id) ?: throw IllegalStateException("Gagal mengambil laporan yang baru diperbarui dengan id: $id")
    }

    override suspend fun getByStatus(status: String): List<Report> = dbQuery {
        Reports.selectAll().where { Reports.statusLaporan eq status }
            .map { rowToEntity(it) }
    }

    override suspend fun getByDateRange(startDate: String, endDate: String): List<Report> = dbQuery {
        Reports.selectAll().where {
            (Reports.tanggalKejadian greaterEq startDate.format(dateFormatter)) and
                    (Reports.tanggalKejadian lessEq endDate.format(dateFormatter))
        }.map { rowToEntity(it) }
    }

    override suspend fun delete(id: Int): Boolean = dbQuery {
        val updatedRows = Reports.update({ Reports.id eq id }) {
            it[statusLaporan] = "Dihapus" // Tidak menghapus, hanya mengganti status
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
            createdAt = JavaLocalDateTime.parse(row[Reports.createdAt], dateTimeFormatter),
            updatedAt = row[Reports.updatedAt].takeIf { it.isNotEmpty() }?.let { JavaLocalDateTime.parse(it, dateTimeFormatter) }
        )
    }

    private fun getCurrentTimeAsString(): String {
        return JavaLocalDateTime.now().format(dateTimeFormatter)
    }
}
