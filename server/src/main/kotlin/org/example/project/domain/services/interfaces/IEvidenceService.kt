package org.example.project.domain.services.interfaces

import org.example.project.application.dtos.requests.EvidenceRequest
import org.example.project.model.Evidence

interface IEvidenceService : IEntityService<EvidenceRequest, Pair<Int, Int>, Evidence> {
    suspend fun getByReportId(reportId: Int): List<Evidence>
}