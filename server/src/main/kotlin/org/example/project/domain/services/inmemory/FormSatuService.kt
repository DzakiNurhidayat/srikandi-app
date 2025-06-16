package org.example.project.domain.services.inmemory

import io.ktor.server.plugins.*
import org.example.project.common.enums.StatusLaporan
import org.example.project.domain.services.interfaces.IFormSatuService
import org.example.project.infastructure.repositories.interfaces.IFormSatuRepository
import org.example.project.infastructure.repositories.interfaces.IReportRepository
import org.example.project.model.entities.FormSatu
import org.example.project.model.request.FormSatuRequest
import java.time.LocalDateTime

class FormSatuService(
    private val formSatuRepository: IFormSatuRepository,
    private val reportRepository: IReportRepository
) : BaseService<FormSatuRequest, Pair<Int, Int>, FormSatu>(formSatuRepository), IFormSatuService {

    override suspend fun getAll(): List<FormSatu> = formSatuRepository.getAll()

    override suspend fun getById(id: Pair<Int, Int>): FormSatu? = formSatuRepository.getById(id)

    override suspend fun findById(id: Pair<Int, Int>): Boolean = formSatuRepository.findById(id)

    override suspend fun create(request: FormSatuRequest, reportId: Int): FormSatu? {
        val report = reportRepository.getById(reportId)
            ?: throw NotFoundException("Report dengan ID $reportId tidak ditemukan")

        val form = FormSatu(
            form1id = 0, // dummy karena auto-increment
            reportId = reportId,
            ciriFisik = request.ciriFisik,
            domisili = request.domisili,
            ceritaSingkat = request.ceritaSingkat,
            memilikiDisabilitas = request.memilikiDisabilitas,
            statusTerlapor = request.statusTerlapor,
            jenisKelaminTerlapor = request.jenisKelaminTerlapor,
            alasanPengaduan = request.alasanPengaduan,
            kontakLain = request.kontakLain,
            kebutuhanKorban = request.kebutuhanKorban,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val savedForm = formSatuRepository.create(form)
            ?: throw IllegalStateException("Gagal menyimpan FormSatu")

        reportRepository.updateStatusLaporan(reportId, StatusLaporan.FORM1)

        return savedForm
    }

    override suspend fun delete(id: Pair<Int, Int>): Boolean = formSatuRepository.delete(id)

    override suspend fun update(id: Pair<Int, Int>, request: FormSatuRequest): FormSatu {
        throw UnsupportedOperationException("Update FormSatu tidak didukung")
    }
}