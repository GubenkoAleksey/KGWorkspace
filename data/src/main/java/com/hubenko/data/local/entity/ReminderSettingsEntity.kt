package com.hubenko.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminder_settings")
data class ReminderSettingsEntity(
    @PrimaryKey val employeeId: String,
    val morningEnabled: Boolean,
    val morningStartTime: String,
    val morningEndTime: String,
    val morningIntervalMinutes: Int,
    val eveningEnabled: Boolean,
    val eveningStartTime: String,
    val eveningEndTime: String,
    val eveningIntervalMinutes: Int,
    val daysOfWeek: String // Comma separated integers: "2,3,4,5,6"
)
