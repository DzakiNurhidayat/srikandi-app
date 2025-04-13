package org.example.project.data.repositories

import org.example.project.common.enums.StatusLaporan
import org.example.project.data.remote.ApiService
import org.example.project.model.Response
import org.example.project.model.entities.Report
import org.example.project.model.request.StatusLaporanRequest
import org.example.project.model.request.ReportRequest
import javax.inject.Inject
import okhttp3.MultipartBody

class ReportRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getReports(): Response<List<Report>> = apiService.getReports()
    suspend fun getUserReports(): Response<List<Report>> = apiService.getUserReports()
    suspend fun updateStatus(id: Int, status: StatusLaporan) {
        val request = StatusLaporanRequest(status)
        apiService.updateStatus(id, request)
    }
    suspend fun uploadFilesAndGetPaths(
        parts: List<MultipartBody.Part>
    ): Response<List<String>> {
        return apiService.uploadEvidence(parts)
    }

    suspend fun createReport(
        reportRequest: ReportRequest
    ): Response<Report> {
        return apiService.createReport(reportRequest)
    }

    suspend fun deleteReport(id: Int) {
        apiService.deleteReport(id)
    }

    suspend fun getReportById(id: Int): Response<Report> {
        return apiService.getReportById(id)
    }

    suspend fun updateReport(id: Int, reportRequest: ReportRequest): Response<Report> {
        return apiService.updateReport(id, reportRequest)
    }
}
