package com.hubenko.domain.model

data class EmployeeStatus(
    val id: String,
    val employeeId: String,
    val employeeFullName: String?, // Для відображення адміну
    val status: String,
    val timestamp: Long,
    val isSynced: Boolean
)
