package com.hubenko.data.mapper

import com.hubenko.data.local.entity.ReminderSettingsEntity
import com.hubenko.domain.model.ReminderSettings

fun ReminderSettingsEntity.toDomain() = ReminderSettings(
    employeeId = employeeId,
    morningEnabled = morningEnabled,
    morningStartTime = morningStartTime,
    morningEndTime = morningEndTime,
    morningIntervalMinutes = morningIntervalMinutes,
    eveningEnabled = eveningEnabled,
    eveningStartTime = eveningStartTime,
    eveningEndTime = eveningEndTime,
    eveningIntervalMinutes = eveningIntervalMinutes,
    daysOfWeek = daysOfWeek.split(",").mapNotNull { it.trim().toIntOrNull() }
)

fun ReminderSettings.toEntity() = ReminderSettingsEntity(
    employeeId = employeeId,
    morningEnabled = morningEnabled,
    morningStartTime = morningStartTime,
    morningEndTime = morningEndTime,
    morningIntervalMinutes = morningIntervalMinutes,
    eveningEnabled = eveningEnabled,
    eveningStartTime = eveningStartTime,
    eveningEndTime = eveningEndTime,
    eveningIntervalMinutes = eveningIntervalMinutes,
    daysOfWeek = daysOfWeek.joinToString(",")
)
