package org.example.project.data.repositories

import okhttp3.MultipartBody
import org.example.project.data.remote.ApiService
import org.example.project.model.Response
import org.example.project.model.entities.Report
import org.example.project.model.request.ReportRequest
import org.example.project.model.request.StatusLaporanRequest
import javax.inject.Inject
import javax.inject.Named

class ReportRepository @Inject constructor(
    @Named("internal") private val apiService: ApiService,
) {

    suspend fun getReports(): Response<List<Report>> = apiService.getReports()
    suspend fun getUserReports(): Response<List<Report>> = apiService.getUserReports()

    suspend fun updateReport(id: Int, status: StatusLaporanRequest) {
        apiService.updateStatus(id, status)
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

    suspend fun getReportById(id: Int): Response<Report> {
        return apiService.getReportById(id)
    }

    suspend fun editReport(id: Int, reportRequest: ReportRequest): Response<Report> {
        return apiService.editReport(
            id,
            reportRequest
        )
    }
}
