package org.example.project.domain.services.interfaces

import org.example.project.application.dtos.requests.ReportRequest
import org.example.project.model.Report

interface IReportService : IEntityService<ReportRequest, Int, Report>{
    suspend fun getByStatus(status: String): List<Report>
    suspend fun getByDateRange(startDate: String, endDate: String): List<Report>
}