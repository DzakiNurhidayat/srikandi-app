package org.example.project.data.repositories

import org.example.project.data.remote.ApiService
import org.example.project.model.Response
import org.example.project.model.entities.FormSatu
import org.example.project.model.request.FormSatuRequest
import javax.inject.Inject

class FormSatuRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun createFormSatu(reportId: Int, formSatuRequest: FormSatuRequest): Response<FormSatu> {
        return apiService.createFormSatu(reportId, formSatuRequest)
    }

    suspend fun getFormSatuByReportId(reportId: Int): Response<FormSatu> {
        return apiService.getFormSatuByReportId(reportId)
    }
}