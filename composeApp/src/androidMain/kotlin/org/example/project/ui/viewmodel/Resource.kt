package org.example.project.ui.viewmodel

sealed class Resource {
    object Loading : Resource()
    data class Success(val data: Any?) : Resource()
    data class Error(val message: String) : Resource()
    object Idle : Resource()
}
