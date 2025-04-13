package org.example.project.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.project.data.repositories.ReportRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.example.project.common.enums.StatusLaporan
import org.example.project.model.entities.Report
import org.example.project.model.request.ReportRequest
import javax.inject.Inject
import java.io.File

@HiltViewModel
class ReportViewModel @Inject constructor(private val repository: ReportRepository) : ViewModel() {
    private val _reports = MutableStateFlow<List<Report>>(emptyList())
    val reports: StateFlow<List<Report>> = _reports

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
            // Prepare MultipartBody.Part from URIs
            val parts = uploadedFiles.map { uri ->
                // Get file name with extension
                val fileName = getFileName(context, uri) ?: "evidence_${System.currentTimeMillis()}.dat"
                val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"

                val inputStream = context.contentResolver.openInputStream(uri)
                val requestBody = inputStream?.readBytes()?.toRequestBody(mimeType.toMediaType())
                    ?: throw Exception("Failed to read file content")

                MultipartBody.Part.createFormData("files", fileName, requestBody)
            }

            // Call repository with prepared parts
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

    // Helper function to get file name with extension
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
        // Fallback to lastPathSegment if query fails
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

    fun deleteReport(id: Int) {
        viewModelScope.launch {
            try {
                repository.deleteReport(id)
                // Refresh reports list after deletion using getUserReports
                val response = repository.getUserReports()
                if (response.status) {
                    _reports.value = response.data ?: emptyList()
                }
            } catch (e: Exception) {
                Log.e("ReportViewModel", "Failed to delete report", e)
            }
        }
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

    suspend fun updateReport(id: Int, reportRequest: ReportRequest): Result<Report> {
        return try {
            val response = repository.updateReport(id, reportRequest)
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
}

