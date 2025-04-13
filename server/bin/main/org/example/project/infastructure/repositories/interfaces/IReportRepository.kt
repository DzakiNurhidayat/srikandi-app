package org.example.project.infastructure.repositories.interfaces

import org.example.project.common.enums.StatusLaporan
import org.example.project.model.entities.Report

interface IReportRepository : IEntityRepository<Report, Int> {
    suspend fun updateStatusLaporan(
        id: Int, status: StatusLaporan
    ): Boolean
}