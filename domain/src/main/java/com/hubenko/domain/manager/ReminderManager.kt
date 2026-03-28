package com.hubenko.domain.manager

import com.hubenko.domain.model.ReminderSettings

interface ReminderManager {
    fun scheduleReminder(settings: ReminderSettings)
    fun scheduleTestAlarm(employeeId: String)
    fun cancelAllReminders(employeeId: String)
}
