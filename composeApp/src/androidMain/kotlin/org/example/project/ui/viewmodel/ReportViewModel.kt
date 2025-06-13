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
import java.io.File
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.project.data.model.ProcessedMediaItem
import java.util.HashMap
import android.util.Log

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val repository: ReportRepository,
) : ViewModel() {

    private val _reports = MutableStateFlow<List<Report>>(emptyList())
    val reports: StateFlow<List<Report>> = _reports

    private val _updateReportState = MutableStateFlow<Resource>(Resource.Idle)
    val updateReportState: StateFlow<Resource> get() = _updateReportState

    private companion object {
        const val MEDIA_BASE_URL = "http://192.168.137.62:8080"
    }

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
            // Prepare MultipartBody.Part from URIs
            val parts = uploadedFiles.map { uri ->
                // Get file name with extension
                val fileName = getFileName(context, uri) ?: "evidence_${System.currentTimeMillis()}.dat"
                val mimeType = context.contentResolver.getType(uri)

                // Validate MIME type
                if (mimeType == null) {
                    throw Exception("Tidak dapat menentukan tipe file. Pastikan file yang dipilih adalah gambar, video, atau audio yang valid.")
                }

                // Validate file extension
                val fileExtension = fileName.substringAfterLast('.', "").lowercase()
                val isValidExtension = when (mimeType) {
                    in listOf("image/jpeg", "image/png", "image/jpg") -> fileExtension in listOf("jpg", "jpeg", "png")
                    in listOf("video/mp4", "video/quicktime", "video/x-msvideo") -> fileExtension in listOf("mp4", "mov", "avi")
                    in listOf("audio/mpeg", "audio/wav", "audio/ogg") -> fileExtension in listOf("mp3", "wav", "ogg")
                    else -> false
                }

                if (!isValidExtension) {
                    throw Exception("Ekstensi file tidak sesuai dengan tipe file. File: $fileName, Tipe: $mimeType")
                }

                val inputStream = context.contentResolver.openInputStream(uri)
                val requestBody = inputStream?.readBytes()?.toRequestBody(mimeType.toMediaType())
                    ?: throw Exception("Gagal membaca konten file")

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
            Result.failure(e)
        }
    }

    suspend fun processReportMedia(report: org.example.project.model.entities.Report, context: Context): List<ProcessedMediaItem> {
        return withContext(Dispatchers.IO) {
            Log.d("ReportViewModel", "Starting to process media for report ID: ${report.id}")
            val processedItems = mutableListOf<ProcessedMediaItem>()

            report.bukti.forEach { buktiPath ->
                Log.d("ReportViewModel", "Processing evidence path: $buktiPath")
                val fullUrlString = if (buktiPath.startsWith("http://") || buktiPath.startsWith("https://")) {
                    Log.d("ReportViewModel", "Using direct HTTP URL: $buktiPath")
                    buktiPath
                } else {
                    val baseUrlClean = MEDIA_BASE_URL.removeSuffix("/")
                    val pathClean = buktiPath.removePrefix("/")
                    val finalUrl = "$baseUrlClean/$pathClean"
                    Log.d("ReportViewModel", "Constructed URL: $finalUrl")
                    finalUrl
                }

                try {
                    Log.d("ReportViewModel", "Creating URI from URL: $fullUrlString")
                    val remoteUri = Uri.parse(fullUrlString)
                    val mediaType = getMimeTypeFromPath(buktiPath)
                    Log.d("ReportViewModel", "Detected media type: $mediaType")

                    var videoThumbnail: Bitmap? = null
                    if (mediaType.startsWith("video/")) {
                        Log.d("ReportViewModel", "Attempting to generate video thumbnail")
                        try {
                            videoThumbnail = getVideoThumbnail(context, remoteUri)
                            if (videoThumbnail != null) {
                                Log.d("ReportViewModel", "Successfully generated video thumbnail")
                            } else {
                                Log.w("ReportViewModel", "Failed to generate video thumbnail - returned null")
                            }
                        } catch (e: Exception) {
                            Log.e("ReportViewModel", "Error generating video thumbnail: ${e.message}", e)
                        }
                    }

                    val processedItem = ProcessedMediaItem(remoteUri, videoThumbnail, mediaType, buktiPath)
                    Log.d("ReportViewModel", "Created ProcessedMediaItem: type=$mediaType, hasThumbnail=${videoThumbnail != null}")
                    processedItems.add(processedItem)
                } catch (e: Exception) {
                    Log.e("ReportViewModel", "Failed to process media path: $buktiPath", e)
                }
            }

            Log.d("ReportViewModel", "Finished processing media. Total items: ${processedItems.size}")
            processedItems
        }
    }

    private fun getMimeTypeFromPath(pathOrUrl: String): String {
        Log.d("ReportViewModel", "Getting MIME type for path: $pathOrUrl")
        val fileName = try {
            Uri.parse(pathOrUrl).lastPathSegment ?: pathOrUrl
        } catch (e: Exception) {
            Log.w("ReportViewModel", "Failed to parse URI, using original path: $pathOrUrl")
            pathOrUrl
        }

        val extension = fileName.substringAfterLast('.', "").lowercase()
        Log.d("ReportViewModel", "File extension: $extension")

        val mimeType = when (extension) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "mp4" -> "video/mp4"
            "mov" -> "video/quicktime"
            "avi" -> "video/x-msvideo"
            "mp3" -> "audio/mpeg"
            "wav" -> "audio/wav"
            "ogg" -> "audio/ogg"
            else -> "application/octet-stream"
        }
        Log.d("ReportViewModel", "Detected MIME type: $mimeType")
        return mimeType
    }

    fun getVideoThumbnail(context: Context, uri: Uri): Bitmap? {
        Log.d("ReportViewModel", "Getting video thumbnail for URI: $uri")
        val retriever = MediaMetadataRetriever()
        try {
            if (uri.scheme?.startsWith("http") == true) {
                Log.d("ReportViewModel", "Using HTTP data source for remote video")
                retriever.setDataSource(uri.toString(), HashMap<String, String>())
            } else {
                Log.d("ReportViewModel", "Using local data source for video")
                retriever.setDataSource(context, uri)
            }

            Log.d("ReportViewModel", "Attempting to get frame at time 0")
            val thumbnail = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
            if (thumbnail != null) {
                Log.d("ReportViewModel", "Successfully generated video thumbnail")
            } else {
                Log.w("ReportViewModel", "Failed to generate video thumbnail - returned null")
            }
            return thumbnail
        } catch (e: Exception) {
            Log.e("ReportViewModel", "Error getting video thumbnail: ${e.message}", e)
            return null
        } finally {
            try {
                Log.d("ReportViewModel", "Releasing MediaMetadataRetriever")
                retriever.release()
            } catch (e: RuntimeException) {
                Log.e("ReportViewModel", "Error releasing MediaMetadataRetriever: ${e.message}", e)
            }
        }
    }
}

