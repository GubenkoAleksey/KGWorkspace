package com.hubenko.feature.admin.ui.model

import com.hubenko.domain.model.HourlyRate

data class HourlyRateUi(
    val id: String,
    val label: String,
    val value: Double
)

fun HourlyRate.toHourlyRateUi() = HourlyRateUi(id = id, label = label, value = value)
