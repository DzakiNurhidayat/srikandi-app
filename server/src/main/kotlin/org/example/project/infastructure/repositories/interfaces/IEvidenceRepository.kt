package org.example.project.infastructure.repositories.interfaces

import org.example.project.model.Evidence

interface IEvidenceRepository : IEntityRepository<Evidence, Pair<Int, Int>> {
    suspend fun getByReportId(reportId: Int): List<Evidence>
}