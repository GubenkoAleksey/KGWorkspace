package com.hubenko.data.mapper

import com.hubenko.data.local.entity.EmployeeEntity
import com.hubenko.domain.model.Employee

fun EmployeeEntity.toDomain() = Employee(
    id = id,
    lastName = lastName,
    firstName = firstName,
    middleName = middleName,
    phoneNumber = phoneNumber,
    role = role,
    email = email,
    password = password
)

fun Employee.toEntity() = EmployeeEntity(
    id = id,
    lastName = lastName,
    firstName = firstName,
    middleName = middleName,
    phoneNumber = phoneNumber,
    role = role,
    email = email,
    password = password
)
