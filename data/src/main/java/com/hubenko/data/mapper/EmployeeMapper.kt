package com.hubenko.data.mapper

import com.hubenko.data.local.entity.EmployeeEntity
import com.hubenko.data.local.entity.EmployeeHourlyRateEntity
import com.hubenko.data.local.entity.EmployeeWithHourlyRates
import com.hubenko.domain.model.Employee
import com.hubenko.domain.model.EmployeeHourlyRate

fun EmployeeWithHourlyRates.toDomain() = Employee(
    id = employee.id,
    lastName = employee.lastName,
    firstName = employee.firstName,
    middleName = employee.middleName,
    phoneNumber = employee.phoneNumber,
    role = employee.role,
    email = employee.email,
    baseRateId = employee.baseRateId,
    baseRateValue = employee.baseRateValue,
    hourlyRates = hourlyRates.map { it.toDomain() }
)

fun Employee.toEntity() = EmployeeEntity(
    id = id,
    lastName = lastName,
    firstName = firstName,
    middleName = middleName,
    phoneNumber = phoneNumber,
    role = role,
    email = email,
    baseRateId = baseRateId,
    baseRateValue = baseRateValue
)

fun EmployeeHourlyRateEntity.toDomain() = EmployeeHourlyRate(
    hourlyRateId = hourlyRateId,
    hourlyRateValue = hourlyRateValue,
    statusType = statusType
)

fun EmployeeHourlyRate.toEntity(employeeId: String) = EmployeeHourlyRateEntity(
    employeeId = employeeId,
    hourlyRateId = hourlyRateId,
    hourlyRateValue = hourlyRateValue,
    statusType = statusType
)
