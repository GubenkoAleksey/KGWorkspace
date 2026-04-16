package com.hubenko.feature.admin.ui.model

import com.hubenko.domain.model.HourlyRate

data class HourlyRateUi(
    val id: String,
    val label: String,
    val value: Double,
    val isSystem: Boolean = false
)

fun HourlyRate.toHourlyRateUi() = HourlyRateUi(id = id, label = label, value = value, isSystem = isSystem)
