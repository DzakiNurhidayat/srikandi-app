package org.example.project.domain.services.inmemory

import io.ktor.server.plugins.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.project.application.dtos.requests.ReportRequest
import org.example.project.application.dtos.toEntity
import org.example.project.domain.services.interfaces.IReportService
import org.example.project.infastructure.repositories.interfaces.IReportRepository
import org.example.project.model.Report
import java.time.format.DateTimeFormatter
import java.time.LocalDate as JavaLocalDate
import java.time.LocalDateTime as JavaLocalDateTime

class ReportService(
    private val reportRepository: IReportRepository
) : BaseService<ReportRequest, Int, Report>(reportRepository), IReportService {

    override suspend fun create(request: ReportRequest): Report {
        if (request.deskripsi.isBlank() || request.statusLaporan.isBlank() || request.jenisKekerasan.isBlank() ||
            request.tempatKejadian.isBlank() || request.tanggalKejadian.isBlank()) {
            throw IllegalArgumentException("Laporan tidak valid: perlu deskripsi, status laporan, jenis kekerasan, tempat kejadian, dan tanggal kejadian")
        }
        val newReport = request.toEntity().copy(
            statusLaporan = "Verifikasi",
            createdAt = JavaLocalDateTime.now(),
            updatedAt = JavaLocalDateTime.now()
        )
        return repository.create(newReport)
    }

    override suspend fun update(id: Int, request: ReportRequest): Report {
        if (request.deskripsi.isBlank() || request.statusLaporan.isBlank() || request.jenisKekerasan.isBlank() ||
            request.tempatKejadian.isBlank() || request.tanggalKejadian.isBlank()) {
            throw IllegalArgumentException("Laporan tidak valid: perlu deskripsi, status laporan, jenis kekerasan, tempat kejadian, dan tanggal kejadian")
        }
        if (!repository.findById(id)) {
            throw NotFoundException("Report dengan id $id tidak ditemukan")
        }
        val updatedReport = request.toEntity().copy(
            updatedAt = JavaLocalDateTime.now()
        )
        return repository.update(id, updatedReport)
    }

    override suspend fun delete(id: Int): Boolean {
        val report = repository.getById(id) ?: throw NotFoundException("Report dengan id $id tidak ditemukan")
        val updatedReport = report.copy(
            statusLaporan = "Dihapus",
            updatedAt = JavaLocalDateTime.now()
        )
        repository.update(id, updatedReport)
        return true
    }

    override suspend fun getByStatus(status: String): List<Report> {
        return reportRepository.getByStatus(status)
    }

    override suspend fun getByDateRange(startDate: String, endDate: String): List<Report> {
        return reportRepository.getByDateRange(startDate, endDate)
    }
}