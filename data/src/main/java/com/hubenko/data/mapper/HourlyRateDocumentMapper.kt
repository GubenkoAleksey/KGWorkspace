package com.hubenko.data.mapper

import com.hubenko.data.local.entity.HourlyRateEntity
import com.hubenko.data.remote.document.HourlyRateDocument
import com.hubenko.domain.model.HourlyRate

fun HourlyRateDocument.toHourlyRate() = HourlyRate(id = id, label = label, value = value, isSystem = isSystem)

fun HourlyRateDocument.toHourlyRateEntity() = HourlyRateEntity(id = id, label = label, value = value)
