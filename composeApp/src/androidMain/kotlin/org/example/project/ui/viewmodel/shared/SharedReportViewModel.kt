// SharedReportViewModel.kt
package org.example.project.ui.viewmodel.shared

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.example.project.model.entities.Report
import javax.inject.Inject

@HiltViewModel
class SharedReportViewModel @Inject constructor() : ViewModel() {
    private val _selectedReport = MutableStateFlow<Report?>(null)
    val selectedReport: StateFlow<Report?> = _selectedReport

    fun setReport(report: Report) {
        Log.d("SharedReportViewModel", "Setting report: $report")
        _selectedReport.value = report
    }
}