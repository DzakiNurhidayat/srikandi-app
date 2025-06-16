package org.example.project.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.example.project.common.enums.StatusTerlapor
import org.example.project.data.repositories.FormSatuRepository
import org.example.project.data.repositories.ReportRepository
import org.example.project.model.entities.FormSatu
import org.example.project.model.entities.Report
import org.example.project.model.request.FormSatuRequest
import org.example.project.model.request.ReportRequest
import javax.inject.Inject

@HiltViewModel
class FormSatuViewModel @Inject constructor(
    private val formSatuRepository: FormSatuRepository,
    private val reportRepository: ReportRepository
) : ViewModel() {

    // State untuk field formulir
    var domisili by mutableStateOf("")
        private set
    var ciriFisik by mutableStateOf("")
        private set
    var memilikiDisabilitas by mutableStateOf(false)
        private set
    var ceritaSingkat by mutableStateOf("")
        private set
    var kontakLain by mutableStateOf("")
        private set
    var kebutuhanKorban by mutableStateOf("")
        private set
    var statusTerlapor by mutableStateOf(StatusTerlapor.Mahasiswa)
        private set
    var jenisKelaminTerlapor by mutableStateOf(false)
        private set
    var alasanPengaduan by mutableStateOf("")
        private set

    // State untuk menyimpan reportId
    private var reportId by mutableStateOf<Int?>(null)

    // State untuk UI (loading, error, dll.)
    private val _formSatuState = MutableStateFlow<Resource>(Resource.Idle)
    val formSatuState: StateFlow<Resource> get() = _formSatuState

    // Fungsi untuk memperbarui state field
    fun updateDomisili(value: String) { domisili = value }
    fun updateCiriFisik(value: String) { ciriFisik = value }
    fun updateMemilikiDisabilitas(value: Boolean) { memilikiDisabilitas = value }
    fun updateCeritaSingkat(value: String) { ceritaSingkat = value }
    fun updateKontakLain(value: String) { kontakLain = value }
    fun updateKebutuhanKorban(value: String) { kebutuhanKorban = value }
    fun updateStatusTerlapor(value: StatusTerlapor) { statusTerlapor = value }
    fun updateJenisKelaminTerlapor(value: Boolean) { jenisKelaminTerlapor = value }
    fun updateAlasanPengaduan(value: String) { alasanPengaduan = value }

    // Fungsi untuk membuat laporan dan menyimpan reportId
    suspend fun createReport(reportRequest: ReportRequest): Result<Report> {
        return try {
            _formSatuState.value = Resource.Loading
            val response = reportRepository.createReport(reportRequest)
            if (response.status) {
                val report = response.data ?: throw Exception("No report data returned")
                reportId = report.id // Simpan reportId
                Log.d("FormSatuViewModel", "Report created with ID: ${report.id}")
                _formSatuState.value = Resource.Success(Unit)
                Result.success(report)
            } else {
                _formSatuState.value = Resource.Error(response.message ?: "Failed to create report")
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Log.e("FormSatuViewModel", "Error creating report: ${e.message}")
            _formSatuState.value = Resource.Error(e.message ?: "Network error")
            Result.failure(e)
        }
    }

    // Fungsi untuk membuat FormSatu
    suspend fun createFormSatu(): Result<Unit> {
        val currentReportId = reportId ?: return Result.failure(Exception("Report ID tidak tersedia, buat laporan terlebih dahulu"))
        Log.d("FormSatuViewModel", "Creating FormSatu with reportId: $currentReportId")
        val formSatuRequest = FormSatuRequest(
            domisili = domisili,
            ciriFisik = ciriFisik,
            memilikiDisabilitas = memilikiDisabilitas,
            ceritaSingkat = ceritaSingkat,
            kontakLain = kontakLain,
            kebutuhanKorban = kebutuhanKorban,
            statusTerlapor = statusTerlapor,
            jenisKelaminTerlapor = jenisKelaminTerlapor,
            alasanPengaduan = alasanPengaduan
        )
        return try {
            _formSatuState.value = Resource.Loading
            val response = formSatuRepository.createFormSatu(currentReportId, formSatuRequest)
            if (response.status) {
                Log.d("FormSatuViewModel", "FormSatu created successfully")
                _formSatuState.value = Resource.Success(Unit)
                Result.success(Unit)
            } else {
                _formSatuState.value = Resource.Error(response.message ?: "Failed to create Form 1")
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Log.e("FormSatuViewModel", "Error creating FormSatu: ${e.message}")
            _formSatuState.value = Resource.Error(e.message ?: "Form-1 belum tersedia, silakan buat laporan terlebih dahulu")
            Result.failure(e)
        }
    }

    // Fungsi untuk mengambil FormSatu dengan validasi laporan
    suspend fun getFormSatu(): Result<FormSatu> {
        val currentReportId = reportId ?: return Result.failure(Exception("Report ID tidak tersedia, buat laporan terlebih dahulu"))
        Log.d("FormSatuViewModel", "Fetching FormSatu with reportId: $currentReportId")
        return try {
            _formSatuState.value = Resource.Loading
            val reportResponse = reportRepository.getReportById(currentReportId)
            if (!reportResponse.status || reportResponse.data == null) {
                Log.e("FormSatuViewModel", "Report with ID $currentReportId not found")
                _formSatuState.value = Resource.Error("Laporan dengan ID $currentReportId tidak ditemukan")
                return Result.failure(Exception("Laporan tidak ditemukan"))
            }
            val response = formSatuRepository.getFormSatuByReportId(currentReportId)
            if (response.status) {
                val formSatu = response.data ?: throw Exception("No FormSatu data returned")
                domisili = formSatu.domisili
                ciriFisik = formSatu.ciriFisik
                memilikiDisabilitas = formSatu.memilikiDisabilitas
                ceritaSingkat = formSatu.ceritaSingkat
                kontakLain = formSatu.kontakLain
                kebutuhanKorban = formSatu.kebutuhanKorban
                statusTerlapor = formSatu.statusTerlapor
                jenisKelaminTerlapor = formSatu.jenisKelaminTerlapor
                alasanPengaduan = formSatu.alasanPengaduan
                Log.d("FormSatuViewModel", "FormSatu fetched successfully")
                _formSatuState.value = Resource.Success(formSatu)
                Result.success(formSatu)
            } else {
                Log.e("FormSatuViewModel", "FormSatu not found: ${response.message}")
                _formSatuState.value = Resource.Error(response.message ?: "Form-1 belum tersedia, silakan buat form terlebih dahulu")
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Log.e("FormSatuViewModel", "Error fetching FormSatu: ${e.message}")
            _formSatuState.value = Resource.Error(e.message ?: "Network error")
            Result.failure(e)
        }
    }

    // Fungsi untuk mengatur reportId secara manual
    fun setReportId(id: Int) {
        reportId = id
        Log.d("FormSatuViewModel", "Report ID set to: $id")
    }
}