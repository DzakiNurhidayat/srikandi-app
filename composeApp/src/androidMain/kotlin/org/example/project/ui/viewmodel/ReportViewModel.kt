package org.example.project.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
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
                }
            } catch (e: Exception) {
                _reports.value = emptyList()
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
            Result.failure(e)
        }
    }

    suspend fun updateReport(id: Int, status: StatusLaporan) {
        val statusLaporanRequest = StatusLaporanRequest(status)
        repository.updateReport(id, statusLaporanRequest)
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
            Result.failure(e)
        }
    }
}
