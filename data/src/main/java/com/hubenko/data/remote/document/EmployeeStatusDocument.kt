package com.hubenko.data.remote.document

data class EmployeeStatusDocument(
    val id: String = "",
    val employeeId: String = "",
    val status: String = "",
    val note: String? = null,
    val startTime: Long = 0L,
    val endTime: Long? = null
)
