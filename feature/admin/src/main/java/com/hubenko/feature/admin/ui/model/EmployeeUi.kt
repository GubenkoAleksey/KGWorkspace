package com.hubenko.feature.admin.ui.model

import com.hubenko.domain.model.Employee
import com.hubenko.feature.admin.ui.model.ReminderSettingsUi

data class EmployeeUi(
    val id: String,
    val lastName: String,
    val firstName: String,
    val middleName: String,
    val fullName: String,
    val phoneNumber: String,
    val role: String,
    val email: String = "",
    val baseRateId: String = "",
    val baseRateValue: Double = 0.0,
    val hourlyRateId: String = "",
    val hourlyRateValue: Double = 0.0,
    val reminderSettings: ReminderSettingsUi? = null
)

fun Employee.toEmployeeUi() = EmployeeUi(
    id = id,
    lastName = lastName,
    firstName = firstName,
    middleName = middleName,
    fullName = listOf(lastName, firstName, middleName).filter { it.isNotBlank() }.joinToString(" "),
    phoneNumber = phoneNumber,
    role = role,
    email = email,
    baseRateId = baseRateId,
    baseRateValue = baseRateValue,
    hourlyRateId = hourlyRateId,
    hourlyRateValue = hourlyRateValue
)

fun EmployeeUi.toDomain() = Employee(
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
