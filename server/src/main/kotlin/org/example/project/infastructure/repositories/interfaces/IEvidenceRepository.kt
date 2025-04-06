package org.example.project.infastructure.repositories.interfaces

import org.example.project.model.entities.Evidence

interface IEvidenceRepository : IEntityRepository<Evidence, Pair<Int, Int>> {
    suspend fun getByReportId(reportId: Int): List<Evidence>
    suspend fun deleteByReportId(reportId: Int): Boolean
}