package com.hubenko.data.remote.document

data class ReminderSettingsDocument(
    val employeeId: String = "",
    val morningEnabled: Boolean = true,
    val morningStartTime: String = "07:30",
    val morningEndTime: String = "08:00",
    val morningIntervalMinutes: Int = 5,
    val eveningEnabled: Boolean = true,
    val eveningStartTime: String = "17:30",
    val eveningEndTime: String = "18:00",
    val eveningIntervalMinutes: Int = 5,
    val daysOfWeek: List<Long> = emptyList()  // Firestore deserializes numbers as Long
)
