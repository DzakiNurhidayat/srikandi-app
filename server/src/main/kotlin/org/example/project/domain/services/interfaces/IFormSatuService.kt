package org.example.project.domain.services.interfaces

import org.example.project.model.entities.FormSatu
import org.example.project.model.request.FormSatuRequest

interface IFormSatuService : IEntityService<FormSatuRequest, Pair<Int, Int>, FormSatu> {
    suspend fun create(request: FormSatuRequest, reportId: Int): FormSatu?

    // Override create dari IEntityService dengan pengecualian jika reportId tidak disediakan
    override suspend fun create(request: FormSatuRequest): FormSatu?
            = throw IllegalArgumentException("Report ID harus disediakan sebagai parameter terpisah")
}