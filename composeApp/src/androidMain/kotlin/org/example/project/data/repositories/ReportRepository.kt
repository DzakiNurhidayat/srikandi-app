package org.example.project.data.repositories

import org.example.project.common.enums.StatusLaporan
import org.example.project.data.remote.ApiService
import org.example.project.model.Response
import org.example.project.model.entities.Report
import org.example.project.model.request.StatusLaporanRequest
import javax.inject.Inject

class ReportRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getReports(): Response<List<Report>> = apiService.getReports()
    suspend fun updateStatus(id: Int, status: StatusLaporan) {
        val request = StatusLaporanRequest(status)
        apiService.updateStatus(id, request)
    }
}
