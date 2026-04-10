package com.hubenko.data.mapper

import com.hubenko.data.local.entity.EmployeeStatusEntity
import com.hubenko.data.remote.document.EmployeeStatusDocument

fun EmployeeStatusDocument.toEntity() = EmployeeStatusEntity(
    id = id,
    employeeId = employeeId,
    status = status,
    note = note,
    startTime = startTime,
    endTime = endTime,
    isSynced = true
)
