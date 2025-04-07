package org.example.project.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.example.project.common.enums.StatusLaporan
import org.example.project.data.repository.ReportRepository
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

    fun updateReportStatus(id: Int, status: StatusLaporan) {
        viewModelScope.launch {
            try {
                repository.updateStatus(id, status)
            } catch (e: Exception) {
                Log.e("ReportViewModel", "Failed to update report status", e)
            }
        }
    }
}
