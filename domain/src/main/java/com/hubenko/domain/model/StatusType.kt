package com.hubenko.domain.model

data class StatusType(
    val type: String,
    val label: String,
    val isSystem: Boolean = false
)
