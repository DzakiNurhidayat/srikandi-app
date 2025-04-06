package org.example.project.domain.services.interfaces

import org.example.project.model.entities.Evidence
import org.example.project.model.request.EvidenceRequest

interface IEvidenceService : IEntityService<EvidenceRequest, Pair<Int, Int>, Evidence> {
    suspend fun getByReportId(reportId: Int): List<Evidence>
}