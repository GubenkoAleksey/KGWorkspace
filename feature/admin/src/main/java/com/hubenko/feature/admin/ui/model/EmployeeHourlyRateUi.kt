package com.hubenko.feature.admin.ui.model

import com.hubenko.domain.model.EmployeeHourlyRate

data class EmployeeHourlyRateUi(
    val hourlyRateId: String = "",
    val hourlyRateValue: Double = 0.0,
    val statusType: String
)

fun EmployeeHourlyRate.toUi() = EmployeeHourlyRateUi(
    hourlyRateId = hourlyRateId,
    hourlyRateValue = hourlyRateValue,
    statusType = statusType
)

fun EmployeeHourlyRateUi.toDomain() = EmployeeHourlyRate(
    hourlyRateId = hourlyRateId,
    hourlyRateValue = hourlyRateValue,
    statusType = statusType
)
