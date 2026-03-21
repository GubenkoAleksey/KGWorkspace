package com.hubenko.data.mapper

import com.hubenko.data.local.entity.EmployeeStatusEntity
import com.hubenko.data.local.entity.EmployeeStatusWithDetails
import com.hubenko.domain.model.EmployeeStatus

fun EmployeeStatusWithDetails.toDomain() = EmployeeStatus(
    id = status.id,
    employeeId = status.employeeId,
    employeeFullName = employee?.let { 
        "${it.lastName} ${it.firstName} ${it.middleName}" 
    } ?: "Невідомий працівник (ID: ${status.employeeId})",
    status = status.status,
    note = status.note,
    startTime = status.startTime,
    endTime = status.endTime,
    isSynced = status.isSynced
)

fun EmployeeStatusEntity.toDomain() = EmployeeStatus(
    id = id,
    employeeId = employeeId,
    employeeFullName = null,
    status = status,
    note = note,
    startTime = startTime,
    endTime = endTime,
    isSynced = isSynced
)
