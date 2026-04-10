package com.hubenko.feature.admin.ui.model

import com.hubenko.domain.model.ReminderSettings

data class ReminderSettingsUi(
    val employeeId: String = "",
    val morningEnabled: Boolean = true,
    val morningStartTime: String = "07:30",
    val morningEndTime: String = "08:00",
    val morningIntervalMinutes: Int = 5,
    val eveningEnabled: Boolean = true,
    val eveningStartTime: String = "17:30",
    val eveningEndTime: String = "18:00",
    val eveningIntervalMinutes: Int = 5,
    val daysOfWeek: List<Int> = listOf(2, 3, 4, 5, 6)
)

fun ReminderSettings.toReminderSettingsUi() = ReminderSettingsUi(
    employeeId = employeeId,
    morningEnabled = morningEnabled,
    morningStartTime = morningStartTime,
    morningEndTime = morningEndTime,
    morningIntervalMinutes = morningIntervalMinutes,
    eveningEnabled = eveningEnabled,
    eveningStartTime = eveningStartTime,
    eveningEndTime = eveningEndTime,
    eveningIntervalMinutes = eveningIntervalMinutes,
    daysOfWeek = daysOfWeek
)

fun ReminderSettingsUi.toDomain() = ReminderSettings(
    employeeId = employeeId,
    morningEnabled = morningEnabled,
    morningStartTime = morningStartTime,
    morningEndTime = morningEndTime,
    morningIntervalMinutes = morningIntervalMinutes,
    eveningEnabled = eveningEnabled,
    eveningStartTime = eveningStartTime,
    eveningEndTime = eveningEndTime,
    eveningIntervalMinutes = eveningIntervalMinutes,
    daysOfWeek = daysOfWeek
)
