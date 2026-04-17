package com.hubenko.data.mapper

import com.hubenko.data.local.entity.EmployeeEntity
import com.hubenko.data.remote.document.EmployeeDocument
import com.hubenko.data.remote.document.EmployeeHourlyRateDocument
import com.hubenko.domain.model.Employee
import com.hubenko.domain.model.EmployeeHourlyRate

fun EmployeeDocument.toEmployee() = Employee(
    id = id,
    lastName = lastName,
    firstName = firstName,
    middleName = middleName,
    phoneNumber = phoneNumber,
    role = role,
    email = email,
    baseRateId = baseRateId,
    baseRateValue = baseRateValue,
    hourlyRates = hourlyRates.map { it.toDomain() }
)

fun EmployeeDocument.toEmployeeEntity(uid: String = id) = EmployeeEntity(
    id = uid.ifEmpty { id },
    lastName = lastName,
    firstName = firstName,
    middleName = middleName,
    phoneNumber = phoneNumber,
    role = role,
    email = email,
    baseRateId = baseRateId,
    baseRateValue = baseRateValue
)

fun Employee.toDocument() = EmployeeDocument(
    id = id,
    lastName = lastName,
    firstName = firstName,
    middleName = middleName,
    phoneNumber = phoneNumber,
    role = role,
    email = email,
    baseRateId = baseRateId,
    baseRateValue = baseRateValue,
    hourlyRates = hourlyRates.map { it.toDocument() }
)

fun EmployeeHourlyRateDocument.toDomain() = EmployeeHourlyRate(
    hourlyRateId = hourlyRateId,
    hourlyRateValue = hourlyRateValue,
    statusType = statusType
)

fun EmployeeHourlyRate.toDocument() = EmployeeHourlyRateDocument(
    hourlyRateId = hourlyRateId,
    hourlyRateValue = hourlyRateValue,
    statusType = statusType
)
