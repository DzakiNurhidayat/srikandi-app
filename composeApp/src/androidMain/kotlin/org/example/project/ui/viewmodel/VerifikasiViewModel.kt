package org.example.project.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.example.project.data.repositories.ReportRepository
import org.example.project.model.entities.Report
import javax.inject.Inject

@HiltViewModel
class VerifikasiViewModel @Inject constructor(
    private val repository: ReportRepository
) : ViewModel() {

    private val _report = mutableStateOf<Report?>(null)
    val report: State<Report?> = _report

    fun setReport(report: Report) {
        _report.value = report
    }
}