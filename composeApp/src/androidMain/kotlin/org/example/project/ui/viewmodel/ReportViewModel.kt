package org.example.project.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.project.data.repositories.ReportRepository
import org.example.project.model.entities.Report
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(private val repository: ReportRepository) : ViewModel() {
    private val _reports = MutableStateFlow<List<Report>>(emptyList())
    val reports: StateFlow<List<Report>> = _reports

    fun getReports(onFinish: (() -> Unit)? = null) {
        viewModelScope.launch {
            try {
                val response = repository.getReports()
                if (response.status) {
                    _reports.value = response.data ?: emptyList()
                } else {
                    _reports.value = emptyList()
                    Log.e("ReportViewModel", "Error: ${response.message}")
                }
            } catch (e: Exception) {
                _reports.value = emptyList()
                Log.e("ReportViewModel", "Exception: ${e.message}")
            } finally {
                delay(500)
                onFinish?.invoke()
            }
        }
    }

}

