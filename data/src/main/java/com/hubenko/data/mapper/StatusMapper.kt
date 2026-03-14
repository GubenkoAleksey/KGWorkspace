package com.hubenko.data.mapper

import com.hubenko.data.local.entity.EmployeeStatusEntity
import com.hubenko.data.local.entity.EmployeeStatusWithDetails
import com.hubenko.domain.model.EmployeeStatus

fun EmployeeStatusWithDetails.toDomain() = EmployeeStatus(
    id = status.id,
    employeeId = status.employeeId,
    employeeFullName = "${employee.lastName} ${employee.firstName} ${employee.middleName}",
    status = status.status,
    timestamp = status.timestamp,
    isSynced = status.isSynced
)

fun EmployeeStatusEntity.toDomain() = EmployeeStatus(
    id = id,
    employeeId = employeeId,
    employeeFullName = null,
    status = status,
    timestamp = timestamp,
    isSynced = isSynced
)
