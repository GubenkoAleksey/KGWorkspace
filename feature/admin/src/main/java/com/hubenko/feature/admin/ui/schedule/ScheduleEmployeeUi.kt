package com.hubenko.feature.admin.ui.schedule

import com.hubenko.domain.model.Employee
import com.hubenko.domain.model.ReminderSettings

data class ScheduleEmployeeUi(
    val id: String,
    val fullName: String,
    val morningEnabled: Boolean,
    val morningStartTime: String,
    val morningEndTime: String,
    val eveningEnabled: Boolean,
    val eveningStartTime: String,
    val eveningEndTime: String,
    val formattedDaysOfWeek: String
)

fun Employee.toScheduleEmployeeUi(settings: ReminderSettings): ScheduleEmployeeUi =
    ScheduleEmployeeUi(
        id = id,
        fullName = listOf(lastName, firstName, middleName)
            .filter { it.isNotBlank() }
            .joinToString(" "),
        morningEnabled = settings.morningEnabled,
        morningStartTime = settings.morningStartTime,
        morningEndTime = settings.morningEndTime,
        eveningEnabled = settings.eveningEnabled,
        eveningStartTime = settings.eveningStartTime,
        eveningEndTime = settings.eveningEndTime,
        formattedDaysOfWeek = settings.daysOfWeek.toFormattedDaysOfWeek()
    )

private fun List<Int>.toFormattedDaysOfWeek(): String {
    val names = mapOf(1 to "Нд", 2 to "Пн", 3 to "Вт", 4 to "Ср", 5 to "Чт", 6 to "Пт", 7 to "Сб")
    return sorted().mapNotNull { names[it] }.joinToString(", ")
}
