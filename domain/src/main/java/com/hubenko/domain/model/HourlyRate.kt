package com.hubenko.domain.model

data class HourlyRate(
    val id: String,
    val label: String,
    val value: Double,
    val isSystem: Boolean = false
)
