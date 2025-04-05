package org.example.project.data.model

import androidx.compose.runtime.MutableState

data class Filter(
    val name: String,
    val enabled: MutableState<Boolean>
)