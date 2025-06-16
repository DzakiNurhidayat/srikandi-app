package org.example.project.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.example.project.common.enums.StatusLaporan
import org.example.project.data.repositories.ReportRepository
import org.example.project.model.entities.Report
import org.example.project.model.request.ReportRequest
import org.example.project.model.request.StatusLaporanRequest
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val repository: ReportRepository,
) : ViewModel() {

    private val _reports = MutableStateFlow<List<Report>>(emptyList())
    val reports: StateFlow<List<Report>> = _reports

    private val _updateReportState = MutableStateFlow<Resource>(Resource.Idle)
    val updateReportState: StateFlow<Resource> get() = _updateReportState

    fun getReports(onFinish: (() -> Unit)? = null) {
        viewModelScope.launch {
            try {
                val response = repository.getReports()
                if (response.status) {
                    _reports.value = response.data ?: emptyList()
                } else {
                    _reports.value = emptyList()
                    Log.e("ReportViewModel", "Error: ${response.message}")
                }
            } catch (e: Exception) {
                _reports.value = emptyList()
                Log.e("ReportViewModel", "Exception: ${e.message}")
            } finally {
                delay(500)
                onFinish?.invoke()
            }
        }
    }

    suspend fun uploadFilesAndGetPaths(
        context: Context,
        uploadedFiles: List<Uri>
    ): Result<List<String>> {
        return try {
            val parts = uploadedFiles.map { uri ->
                val fileName = getFileName(context, uri) ?: "evidence_${System.currentTimeMillis()}.dat"
                val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"
                val inputStream = context.contentResolver.openInputStream(uri)
                val requestBody = inputStream?.readBytes()?.toRequestBody(mimeType.toMediaType())
                    ?: throw Exception("Failed to read file content")
                MultipartBody.Part.createFormData("files", fileName, requestBody)
            }

            val response = repository.uploadFilesAndGetPaths(parts)
            if (response.status) {
                Result.success(response.data ?: emptyList())
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Log.e("ReportViewModel", "Error uploading files: ${e.message}")
            Result.failure(e)
        }
    }

    private fun getFileName(context: Context, uri: Uri): String? {
        var fileName: String? = null
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex)
                }
            }
        }
        return fileName ?: uri.lastPathSegment?.substringAfterLast("/")?.takeIf { it.contains(".") }
    }

    suspend fun createReport(
        reportRequest: ReportRequest
    ): Result<Report> {
        return try {
            val response = repository.createReport(reportRequest)
            if (response.status) {
                Result.success(response.data ?: throw Exception("No report data returned"))
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Log.e("ReportViewModel", "Error submitting report: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun updateReport(id: Int, status: StatusLaporan) {
        val statusLaporanRequest = StatusLaporanRequest(status)
        repository.updateReportStatus(id, statusLaporanRequest, )
        _updateReportState.value = Resource.Success(Unit)

    }

    suspend fun getReportById(id: Int): Report? {
        return try {
            val response = repository.getReportById(id)
            if (response.status) {
                response.data
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("ReportViewModel", "Error fetching report: ${e.message}")
            null
        }
    }

    suspend fun editReport(id: Int, reportRequest: ReportRequest): Result<Report> {
        return try {
            val response = repository.editReport(id, reportRequest)
            if (response.status) {
                Result.success(response.data ?: throw Exception("No report data returned"))
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Log.e("ReportViewModel", "Error updating report: ${e.message}")
            Result.failure(e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateKlarifikasiDateForReport(
        reportId: Int,
        newKlarifikasiDate: String,
        currentReport: Report
    ) {
        viewModelScope.launch {
            _updateReportState.value = Resource.Loading
            try {
                // Buat ReportRequest baru dengan tanggal klarifikasi yang diperbarui
                // dan data lainnya diambil dari currentReport
                val updatedRequest = ReportRequest(
                    isKorban = currentReport.isKorban,
                    deskripsi = currentReport.deskripsi,
                    jenisKekerasan = currentReport.jenisKekerasan,
                    tempatKejadian = currentReport.tempatKejadian,
                    tanggalKejadian = currentReport.tanggalKejadian.toString(),
                    statusLaporan = currentReport.statusLaporan, // Status tetap sama
                    bukti = currentReport.bukti,
                    tanggalPemanggilan = newKlarifikasiDate // Field baru yang diupdate
                )

                // Panggil fungsi editReport yang sudah ada di repository Anda
                val response = repository.editReport(reportId, updatedRequest)

                if (response.status) {
                    _updateReportState.value = Resource.Success(response.data!!)
                    Log.d("ReportViewModel", "Tanggal klarifikasi berhasil diupdate untuk $reportId")

                    // Perbarui _reports StateFlow secara lokal agar UI otomatis terupdate
                    _reports.value = _reports.value.map {
                        if (it.id == reportId) {
                            response.data ?: it // Ganti dengan data terbaru dari respons API
                        } else {
                            it
                        }
                    }
                } else {
                    _updateReportState.value = Resource.Error(response.message ?: "Gagal memperbarui tanggal klarifikasi")
                    Log.e("ReportViewModel", "Error updating klarifikasi date for $reportId: ${response.message}")
                }
            } catch (e: Exception) {
                _updateReportState.value = Resource.Error(e.message ?: "Terjadi kesalahan tidak dikenal saat memperbarui tanggal klarifikasi")
                Log.e("ReportViewModel", "Exception updating klarifikasi date for $reportId: ${e.message}")
            }
        }
    }
}