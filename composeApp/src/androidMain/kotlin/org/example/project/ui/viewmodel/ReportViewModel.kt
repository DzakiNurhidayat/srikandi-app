package org.example.project.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.example.project.common.enums.StatusLaporan
import org.example.project.data.repository.ReportRepository
import org.example.project.model.entities.Report
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(private val repository: ReportRepository) : ViewModel() {
    val reports = MutableStateFlow<List<Report>>(emptyList())

    init {
        viewModelScope.launch {
            try {
                val response = repository.getReports()
                if (response.status) {
                    reports.value = response.data ?: emptyList()
                }
            } catch (e: Exception) {
                Log.e("ReportViewModel", "Error fetching products: ${e.message}")
            }
        }
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

