package org.example.project.domain.services.inmemory

import io.ktor.server.plugins.*
import org.example.project.common.enums.StatusLaporan
import org.example.project.domain.entities.FormSatuTable.reportId
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

    override suspend fun create(request: FormSatuRequest): FormSatu {
        val reportId = request.reportId
            ?: throw IllegalArgumentException("Report ID tidak boleh null")

        val report = reportRepository.getById(reportId)
            ?: throw NotFoundException("Report dengan ID $reportId tidak ditemukan")

        val form = FormSatu(
            id = 0, // dummy karena auto-increment
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

    override suspend fun update(id: Pair<Int, Int>, request: FormSatuRequest): FormSatu {
        if (!formSatuRepository.findById(id)) {
            throw NotFoundException("FormSatu dengan ID ${id.first} dan reportID ${id.second} tidak ditemukan")
        }

        val reportId = request.reportId
            ?: throw IllegalArgumentException("Report ID tidak boleh null")

        // Validasi apakah reportId masih valid
        val report = reportRepository.getById(reportId)
            ?: throw NotFoundException("Report dengan ID $reportId tidak ditemukan")

        val updatedForm = FormSatu(
            id = id.first,
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
            createdAt = null, // Tidak diubah, sesuai dengan definisi nullable
            updatedAt = LocalDateTime.now()
        )

        return formSatuRepository.update(id, updatedForm)
    }
}