package org.example.project.infastructure.repositories.interfaces

import kotlinx.datetime.LocalDate
import org.example.project.model.Report

interface IReportRepository : IEntityRepository<Report, Int> {
    suspend fun getByStatus(status: String): List<Report>
    suspend fun getByDateRange(startDate: String, endDate: String): List<Report>
}