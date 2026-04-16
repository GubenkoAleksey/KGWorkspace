package com.hubenko.data.mapper

import com.hubenko.data.local.entity.EmployeeEntity
import com.hubenko.data.remote.document.EmployeeDocument
import com.hubenko.domain.model.Employee

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
    hourlyRateId = hourlyRateId,
    hourlyRateValue = hourlyRateValue
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
    baseRateValue = baseRateValue,
    hourlyRateId = hourlyRateId,
    hourlyRateValue = hourlyRateValue
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
    hourlyRateId = hourlyRateId,
    hourlyRateValue = hourlyRateValue
)
