package com.hubenko.feature.admin.ui.model

import com.hubenko.domain.model.EmployeeStatus

data class EmployeeStatusUi(
    val id: String,
    val employeeId: String,
    val employeeFullName: String?,
    val status: String,
    val statusLabel: String = status,
    val note: String? = null,
    val startTime: Long,
    val endTime: Long? = null,
    val isSynced: Boolean
)

fun EmployeeStatus.toEmployeeStatusUi() = EmployeeStatusUi(
    id = id,
    employeeId = employeeId,
    employeeFullName = employeeFullName,
    status = status,
    note = note,
    startTime = startTime,
    endTime = endTime,
    isSynced = isSynced
)
