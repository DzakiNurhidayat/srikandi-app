package org.example.project.application.dtos

import kotlinx.serialization.Serializable
import org.example.project.model.entities.FormSatu
import org.example.project.model.entities.Report

@Serializable
data class FormSatuWithReportDTO(
    val formSatu: FormSatu,
    val report: Report
)