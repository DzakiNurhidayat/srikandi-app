package org.example.project.infastructure.repositories.inmemory

import io.ktor.server.plugins.*
import org.example.project.domain.entities.Evidences
import org.example.project.domain.entities.Reports
import org.example.project.infastructure.repositories.interfaces.IEvidenceRepository
import org.example.project.infastructure.repositories.interfaces.IReportRepository
import org.example.project.model.Evidence
import org.example.project.model.Report
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq


class EvidenceRepository : BasePairRepository<Evidences, Evidence, Pair<Int, Int>>(Evidences, Pair(Evidences.reportId, Evidences.buktiKe)), IEvidenceRepository {
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override suspend fun create(entity: Evidence): Evidence {

        val filePath = entity.filePath ?: throw IllegalArgumentException("File path tidak boleh kosong")
        val createdAt = entity.createdAt ?: throw IllegalArgumentException("Created at tidak boleh kosong")
        val updatedAt = entity.updatedAt ?: throw IllegalArgumentException("Updated at tidak boleh kosong")

        newSuspendedTransaction {
            Evidences.insert {
                it[reportId] = entity.reportId
                it[buktiKe] = entity.buktiKe
                it[Evidences.filePath] = filePath
                it[Evidences.createdAt] = createdAt.format(dateTimeFormatter)
                it[Evidences.updatedAt] = updatedAt.format(dateTimeFormatter)
            }
        }

        // Setelah penyisipan, ambil bukti berdasarkan reportId dan buktiKe
        return getById(Pair(entity.reportId, entity.buktiKe))
            ?: throw IllegalStateException("Gagal membuat bukti dengan ID: ${entity.reportId} dan bukti ke: ${entity.buktiKe}")
    }

    override suspend fun getByReportId(reportId: Int): List<Evidence> = newSuspendedTransaction {
        // Retrieve all evidence for a specific reportId
        Evidences.selectAll().where { Evidences.reportId eq reportId }
            .map { rowToEntity(it) }
    }

    override suspend fun update(id: Pair<Int, Int>, entity: Evidence): Evidence {
        newSuspendedTransaction {
            val updatedRows = Evidences.update({ (Evidences.reportId eq id.first) and (Evidences.buktiKe eq id.second) }) {
                it[reportId] = entity.reportId
                it[buktiKe] = entity.buktiKe
                it[filePath] = entity.filePath ?: throw IllegalArgumentException("File path tidak boleh kosong")
                it[updatedAt] = entity.updatedAt?.format(dateTimeFormatter) ?: throw IllegalArgumentException("Updated at tidak boleh kosong")
            }

            if (updatedRows == 0) throw NotFoundException("Evidence dengan ID ${id.second} dan Report ID ${id.first} tidak ditemukan")
        }

        return getById(id) ?: throw NotFoundException("Gagal mengambil evidence yang baru diperbarui dengan ID ${id.second} dan Report ID ${id.first}")
    }

    override suspend fun deleteByReportId(reportId: Int): Boolean = newSuspendedTransaction {
        val deletedCount = Evidences.deleteWhere { Evidences.reportId eq reportId }
        return@newSuspendedTransaction deletedCount > 0
    }

    override suspend fun delete(id: Pair<Int, Int>): Boolean = newSuspendedTransaction {
        val deletedCount = Evidences.deleteWhere {
            (Evidences.reportId eq id.first) and (Evidences.buktiKe eq id.second)
        }
        return@newSuspendedTransaction deletedCount > 0
    }

    protected override fun rowToEntity(row: ResultRow): Evidence {
        return Evidence(
            reportId = row[Evidences.reportId],
            buktiKe = row[Evidences.buktiKe],
            filePath = row[Evidences.filePath],
            createdAt = LocalDateTime.parse(row[Evidences.createdAt], dateTimeFormatter),
            updatedAt = LocalDateTime.parse(row[Evidences.updatedAt], dateTimeFormatter)
        )
    }
}
