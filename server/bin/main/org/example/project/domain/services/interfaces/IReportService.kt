package org.example.project.domain.services.interfaces

import org.example.project.model.entities.Report
import org.example.project.model.request.ReportRequest
import org.example.project.model.request.StatusLaporanRequest

interface IReportService : IEntityService<ReportRequest, Int, Report> {
    suspend fun updateStatusLaporan(id: Int, status: StatusLaporanRequest): Boolean
}