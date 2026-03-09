package com.hubenko.domain.model

data class EmployeeStatus(
    val id: String,
    val employeeId: String,
    val status: String,
    val timestamp: Long,
    val isSynced: Boolean
)
