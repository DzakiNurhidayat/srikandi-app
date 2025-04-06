package org.example.project.domain.services.inmemory

import io.ktor.server.plugins.*
import org.example.project.application.dtos.toEntity
import org.example.project.domain.services.interfaces.IEvidenceService
import org.example.project.infastructure.repositories.interfaces.IEvidenceRepository
import org.example.project.model.entities.Evidence
import org.example.project.model.request.EvidenceRequest

class EvidenceService(
    private val evidenceRepository: IEvidenceRepository
) : BaseService<EvidenceRequest, Pair<Int, Int>, Evidence>(evidenceRepository), IEvidenceService {

    override suspend fun create(request: EvidenceRequest): Evidence {
        if (request.filePath.isBlank()) {
            throw IllegalArgumentException("File path tidak boleh kosong")
        }

        // Hitung jumlah bukti yang sudah ada untuk reportId
        val existingEvidences = evidenceRepository.getByReportId(request.reportId)
        val buktiKe = existingEvidences.size + 1 // buktiKe = jumlah existing + 1

        val newEvidence = request.toEntity(buktiKe)
        return repository.create(newEvidence)
    }

    override suspend fun update(id: Pair<Int, Int>, request: EvidenceRequest): Evidence {
        if (request.filePath.isBlank()) {
            throw IllegalArgumentException("File path tidak boleh kosong")
        }
        if (!repository.findById(id)) {
            throw NotFoundException("Evidence dengan id $id tidak ditemukan")
        }

        // Pastikan `buktiKe` tetap sama saat update
        val updatedEvidence = request.toEntity(id.second)
        return repository.update(id, updatedEvidence)
    }

    override suspend fun delete(id: Pair<Int, Int>): Boolean {
        if (!repository.findById(id)) {
            throw NotFoundException("Evidence dengan id $id tidak ditemukan")
        }

        // Cek apakah masih ada bukti lain untuk laporan ini
        val remainingEvidences = evidenceRepository.getByReportId(id.first)
        if (remainingEvidences.size == 1) {
            throw IllegalArgumentException("Tidak dapat menghapus bukti terakhir. Setiap laporan harus memiliki minimal satu bukti.")
        }

        return repository.delete(id)
    }

    override suspend fun getByReportId(reportId: Int): List<Evidence> {
        return evidenceRepository.getByReportId(reportId)
    }
}
