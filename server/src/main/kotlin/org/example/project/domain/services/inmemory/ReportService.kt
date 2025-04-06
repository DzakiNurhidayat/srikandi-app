package org.example.project.domain.services.inmemory

import io.ktor.server.plugins.*
import org.example.project.application.dtos.toEntity
import org.example.project.common.enums.JenisKekerasan
import org.example.project.common.enums.StatusLaporan
import org.example.project.domain.services.interfaces.IReportService
import org.example.project.infastructure.repositories.interfaces.IEvidenceRepository
import org.example.project.infastructure.repositories.interfaces.IReportRepository
import org.example.project.model.entities.Report
import org.example.project.model.entities.Evidence
import org.example.project.model.request.ReportRequest
import org.example.project.model.request.StatusLaporanRequest
import java.time.LocalDateTime as JavaLocalDateTime

class ReportService(
    private val reportRepository: IReportRepository,
    private val evidenceRepository: IEvidenceRepository
) : BaseService<ReportRequest, Int, Report>(reportRepository), IReportService {

    override suspend fun create(request: ReportRequest): Report {
        if (request.deskripsi.isBlank() || request.jenisKekerasan !in JenisKekerasan.entries || request.jenisKekerasan !in JenisKekerasan.entries ||
            request.tempatKejadian.isBlank() || request.tanggalKejadian.isBlank()) {
            throw IllegalArgumentException("Laporan tidak valid: perlu deskripsi, status laporan, jenis kekerasan, tempat kejadian, dan tanggal kejadian")
        }

        if (request.bukti.isEmpty()) {
            throw IllegalArgumentException("Setiap laporan harus memiliki minimal satu bukti")
        }

        // Simpan laporan terlebih dahulu
        val newReport = request.toEntity().copy(
            statusLaporan = StatusLaporan.DRAFT,
            createdAt = JavaLocalDateTime.now(),
            updatedAt = JavaLocalDateTime.now()
        )
        val savedReport = repository.create(newReport)

        // Simpan bukti yang terkait dengan laporan ini
        request.bukti.forEachIndexed { index, filePath ->
            val newEvidence = Evidence(
                reportId = savedReport.id!!,
                buktiKe = index + 1,
                filePath = filePath
            )
            evidenceRepository.create(newEvidence)
        }

        return savedReport.copy(bukti = request.bukti)
    }

    override suspend fun update(id: Int, request: ReportRequest): Report {
        if (request.deskripsi.isBlank() || request.jenisKekerasan !in JenisKekerasan.entries || request.jenisKekerasan !in JenisKekerasan.entries ||
            request.tempatKejadian.isBlank() || request.tanggalKejadian.isBlank()) {
            throw IllegalArgumentException("Laporan tidak valid: perlu deskripsi, status laporan, jenis kekerasan, tempat kejadian, dan tanggal kejadian")
        }

        if (!repository.findById(id)) {
            throw NotFoundException("Report dengan id $id tidak ditemukan")
        }

        if (request.bukti.isEmpty()) {
            throw IllegalArgumentException("Laporan harus memiliki minimal satu bukti")
        }

        val updatedReport = request.toEntity().copy(updatedAt = JavaLocalDateTime.now())
        val savedReport = repository.update(id, updatedReport)

        // Hapus bukti lama dan tambahkan bukti baru
        evidenceRepository.deleteByReportId(id)
        request.bukti.forEachIndexed { index, filePath ->
            val newEvidence = Evidence(
                reportId = id,
                buktiKe = index + 1,
                filePath = filePath
            )
            evidenceRepository.create(newEvidence)
        }

        return savedReport.copy(bukti = request.bukti)
    }

    override suspend fun delete(id: Int): Boolean {
        val report = repository.getById(id) ?: throw NotFoundException("Report dengan id $id tidak ditemukan")
        val updatedReport = report.copy(
            statusLaporan = StatusLaporan.DELETED,
            updatedAt = JavaLocalDateTime.now()
        )
        repository.update(id, updatedReport)
        return true
    }

    override suspend fun getById(id: Int): Report? {
        val report = super.getById(id) ?: return null
        val evidences = evidenceRepository.getByReportId(id)
        return report.copy(bukti = evidences.mapNotNull { it.filePath })
    }

    override suspend fun getAll(): List<Report> {
        val reports = super.getAll()
        return reports.map { report ->
            val evidences = evidenceRepository.getByReportId(report.id!!)
            report.copy(bukti = evidences.mapNotNull { it.filePath })
        }
    }

    override suspend fun updateStatusLaporan(id: Int, status: StatusLaporanRequest): Boolean {
        if (!repository.findById(id)) {
            throw NotFoundException("Laporan dengan id $id tidak ditemukan")
        }
        return reportRepository.updateStatusLaporan(id, status.statusLaporan)
    }
}